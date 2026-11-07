package com.shadow.panel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class ShellRunner {
    public static String exec(String cmd) {
        StringBuilder sb = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append("\n");
            r.close(); p.waitFor();
        } catch (Exception e) { sb.append("err: ").append(e.getMessage()); }
        return sb.toString();
    }
}
