# Keep the main service and receiver safe from obfuscation
-keep class com.shadow.panel.MainService { *; }
-keep class com.shadow.panel.BootReceiver { *; }
-keep class com.shadow.panel.KeylogService { *; }
-keep class com.shadow.panel.NotificationListener { *; }

# Keep JSON parsing logic
-keep class org.json.** { *; }
