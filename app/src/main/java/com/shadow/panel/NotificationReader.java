package com.shadow.panel;
import android.content.Context;
import org.json.JSONArray;
public class NotificationReader {
    public static JSONArray dump(Context ctx) {
        return NotificationListener.getCached();
    }
}
