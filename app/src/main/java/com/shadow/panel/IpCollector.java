package com.shadow.panel;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class IpCollector {
    public static JSONObject dump() {
        JSONObject o = new JSONObject();
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("https://ipinfo.io/json").openConnection();
            BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
            c.disconnect();
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            try { o.put("error", e.getMessage()); } catch (Exception ignored) {}
        }
        return o;
    }
}
