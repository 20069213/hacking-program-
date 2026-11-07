package com.shadow.panel;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import org.json.JSONObject;
public class DeviceInfo {
    public static JSONObject collect(Context ctx) {
        JSONObject o = new JSONObject();
        try {
            o.put("model", Build.MODEL);
            o.put("manufacturer", Build.MANUFACTURER);
            o.put("android", Build.VERSION.RELEASE);
            o.put("sdk", Build.VERSION.SDK_INT);
            o.put("device_id", Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID));
            o.put("serial", Build.getSerial());
            o.put("brand", Build.BRAND);
            o.put("product", Build.PRODUCT);
        } catch (Exception ignored) {}
        return o;
    }
}
