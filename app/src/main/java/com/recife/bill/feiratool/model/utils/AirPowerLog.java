package com.recife.bill.feiratool.model.utils;

import android.util.Log;

public class AirPowerLog {
    private static final String TAG = "AirPowerCostumerApp:";
    public static final boolean ISLOGABLE = AirPowerUtil.isDebugVersion();
    public static final boolean ISVERBOSE = ISLOGABLE && AirPowerUtil.isVerbose();

    public static void d(String subtag, String message) {
        Log.d(TAG, " [" + subtag + "]: " + message);
    }

    public static void d(String subtag, Thread thread, String message) {
        Log.d(TAG, " [" + subtag + "] " + " [" + thread.getName() + "] " + message);
    }

    public static void e(String subtag, String message) {
        Log.e(TAG, " [" + subtag + "]: " + message);
    }

    public static void w(String subtag, String message) {
        Log.w(TAG, " [" + subtag + "]: " + message);
    }
}