package com.example.riskenvdetection;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

public class DualOpenDection {

    private static final String TAG = "RiskEnvDetection";

    public static String readFile(String filename) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
            }
        }
        return sb.toString();
    }

    private static String getPrefix(int targetSdkVersion, int sdkVersion) {
        String domainPrefix = "u:r:untrusted_app";
        if (sdkVersion < Build.VERSION_CODES.O) {
            return domainPrefix;
        }
        if (targetSdkVersion >= sdkVersion) {
            return domainPrefix + ":s0";
        } else if (targetSdkVersion < Build.VERSION_CODES.O) {
            return domainPrefix + "_25:s0";
        }
        if (sdkVersion == Build.VERSION_CODES.O_MR1) {
            if (targetSdkVersion >= Build.VERSION_CODES.O) {
                return domainPrefix + ":s0";
            }
        } else if (sdkVersion == Build.VERSION_CODES.P || sdkVersion == Build.VERSION_CODES.Q) {
            if (targetSdkVersion >= Build.VERSION_CODES.O) {
                return domainPrefix + "_27:s0";
            }
        } else if (sdkVersion == Build.VERSION_CODES.R || sdkVersion == Build.VERSION_CODES.S) {
            if (targetSdkVersion >= Build.VERSION_CODES.Q) {
                return domainPrefix + "_29:s0";
            } else {
                return domainPrefix + "_27:s0";
            }
        }
        return domainPrefix;
    }

    public static String detect(Context context) {

        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        String currentDomain = null;

        try {
            Class clazz = Class.forName("android.os.SELinux");
            Method method = clazz.getDeclaredMethod("getContext");
            currentDomain = (String) method.invoke(clazz);
        } catch (Exception e) {
        }

        if (TextUtils.isEmpty(currentDomain)) {
            Log.e(TAG, "Will get domain from: /proc/self/attr/current");
            currentDomain = readFile("/proc/self/attr/current");
        }

        if (TextUtils.isEmpty(currentDomain)) {
            Log.e(TAG, "Cannot get current selinux domain");
            return "Error reason: cannot get current selinux domain";
        }

        String calDomain = getPrefix(targetSdkVersion, Build.VERSION.SDK_INT);

        StringBuilder sb = new StringBuilder();
        if (currentDomain.startsWith(calDomain)) {
            sb.append("Not found").append("\n");
        } else {
            sb.append("Found - RISK").append("\n");
        }
        sb.append("current domain: " + currentDomain + "\ncalculation domain prefix: " + calDomain);
        return sb.toString();
    }

}
