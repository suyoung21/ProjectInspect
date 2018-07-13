package com.glink.data;

import android.os.Environment;

import java.io.File;

/**
 * 常量
 * @author jiangshuyang
 */
public class Const {
    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "gl" + File.separator;

    public static final String CRASH_LOG_PATH = APP_PATH + "crashLog";
    public static final String CRASH_LOG_FILE_PATH = CRASH_LOG_PATH + File.separator + "crash_log.txt";

}