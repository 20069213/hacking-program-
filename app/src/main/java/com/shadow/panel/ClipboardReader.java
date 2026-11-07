package com.shadow.panel;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData;
public class ClipboardReader {
    public static String read(Context ctx) {
        try {
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm.hasPrimaryClip()) {
                ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
                return item.coerceToText(ctx).toString();
            }
        } catch (Exception ignored) {}
        return "";
    }
}
