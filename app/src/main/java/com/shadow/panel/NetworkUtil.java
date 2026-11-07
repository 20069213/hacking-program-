package com.shadow.panel;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
public class NetworkUtil {
    public static String getLocalIp(Context ctx) {
        try {
            WifiManager wm = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        } catch (Exception e) { return "0.0.0.0"; }
    }
}
