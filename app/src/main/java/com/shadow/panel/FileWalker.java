package com.shadow.panel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
public class FileWalker {
    public static JSONArray list(String path) {
        JSONArray arr = new JSONArray();
        try {
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files == null) return arr;
            for (File f : files) {
                JSONObject o = new JSONObject();
                o.put("name", f.getName()); o.put("path", f.getAbsolutePath());
                o.put("is_dir", f.isDirectory()); o.put("size", f.length()); o.put("mod", f.lastModified());
                arr.put(o);
            }
        } catch (Exception ignored) {}
        return arr;
    }
}
