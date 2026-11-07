package com.shadow.panel;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
public class WifiReader {
    public static JSONArray dump(Context ctx) {
        JSONArray arr = new JSONArray();
        try {
            WifiManager wm = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> results = wm.getScanResults();
            for (ScanResult r : results) {
                JSONObject o = new JSONObject();
                o.put("ssid", r.SSID);
                o.put("bssid", r.BSSID);
                o.put("level", r.level);
                o.put("freq", r.frequency);
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
