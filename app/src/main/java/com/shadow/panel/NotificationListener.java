package com.shadow.panel;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class NotificationListener extends NotificationListenerService {
    private static final List<JSONObject> cache = new ArrayList<>();
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            JSONObject o = new JSONObject();
            o.put("pkg", sbn.getPackageName());
            o.put("title", sbn.getNotification().extras.getString("android.title"));
            o.put("text", sbn.getNotification().extras.getString("android.text"));
            o.put("when", sbn.getPostTime());
            synchronized (cache) {
                cache.add(0, o);
                if (cache.size() > 200) cache.remove(cache.size() - 1);
            }
        } catch (Exception ignored) {}
    }
    public static JSONArray getCached() {
        JSONArray arr = new JSONArray();
        synchronized (cache) {
            for (JSONObject o : cache) arr.put(o);
        }
        return arr;
    }
}
