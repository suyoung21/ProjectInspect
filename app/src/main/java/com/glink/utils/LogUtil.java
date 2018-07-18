package com.glink.utils;

import android.util.Log;

/**
 * 日志工具类<br/>
 * 设置LEVEL可调整显示级别
 * @author jiangshuyang
 */

public class LogUtil {
    public static final String TAG = "@GLink--";
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int LEVEL = 1;

    public LogUtil() {
    }

    public static void v(String tag, String msg) {
        if (LEVEL <= 1)
            Log.v("" + tag, msg);
    }

    public static void v(String msg) {
        if (LEVEL <= 1)
            Log.v(TAG, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (LEVEL <= 1)
            Log.v("" + tag, msg, tr);

    }

    public static void i(String tag, String msg) {
        if (LEVEL <= 3)
            Log.i("" + tag, msg);
    }

    public static void i(String msg) {
        if (LEVEL <= 1)
            Log.i(TAG, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (LEVEL <= 3)
            Log.i("" + tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= 2)
            Log.d("" + tag, msg);
    }

    public static void d(String msg) {
        if (LEVEL <= 1)
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LEVEL <= 2)
            Log.d("" + tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= 4)
            Log.w("" + tag, msg);
    }

    public static void w(String msg) {
        if (LEVEL <= 1)
            Log.w(TAG, msg);
    }

    public static void w(String tag, Throwable tr) {
        if (LEVEL <= 4)
            Log.w("" + tag, tr);
    }

    public static void e(String tag, String msg) {
        if (LEVEL <= 5)
            Log.e("" + tag, msg);
    }

    public static void e(String msg) {
        if (LEVEL <= 1)
            Log.e(TAG, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LEVEL <= 5)
            Log.e("" + tag, msg, tr);

    }
}
