package com.example.riskenvdetection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;

public class PosedDetection {

    private final String TAG = "RiskEnvDetection";

    private final int RESULT_ERROR = -1;
    private final int RESULT_NOT_FOUND = 0;
    private final int RESULT_LSPOSED = 1;
    private final int RESULT_EDXPOSED = 2;

    static {
        System.loadLibrary("native-lib");
    }

    private int readProcMaps() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/self/maps"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length == 5) { // Anonymous mem
                    if (tokens[1].contains("rwx") || tokens[1].contains("r-x")) {
                        Log.d(TAG, "line: " + line);
                        long start = Long.parseLong(tokens[0].split("-")[0], 16);
                        long end = Long.parseLong(tokens[0].split("-")[1], 16);
                        Log.d(TAG, "start: " + start + " end: " + end);
                        int result = detectAnonMem(start, end);
                        if (result > 0) {
                            return result;
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
            }
        }
        return RESULT_NOT_FOUND;
    }


    public String detect() {
        int result = readProcMaps();
        if (result == RESULT_LSPOSED) {
            return "LSPOSED found - RISK";
        } else if (result == RESULT_EDXPOSED) {
            return "EdXposed found - RISK";
        } else {
            return "LSPOSED/EdXposed not found";
        }
    }


    public native int detectAnonMem(long start, long end);
}
