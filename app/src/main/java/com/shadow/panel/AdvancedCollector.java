package com.shadow.panel;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
public class AdvancedCollector {
    public static JSONObject dump(Context ctx) {
        JSONObject o = new JSONObject();
        try {
            PackageManager pm = ctx.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            JSONArray list = new JSONArray();
            for (ApplicationInfo ai : apps) {
                JSONObject a = new JSONObject();
                a.put("pkg", ai.packageName);
                a.put("name", pm.getApplicationLabel(ai).toString());
                list.put(a);
            }
            o.put("apps", list);
            o.put("total", apps.size());
        } catch (Exception ignored) {}
        return o;
    }
}
