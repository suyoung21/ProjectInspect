package com.glink.inspect.data;

import android.os.Environment;

import java.io.File;

/**
 * 常量
 *
 * @author jiangshuyang
 */
public class Const {
    public static final String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "gl" + File.separator;

    public static final String CRASH_LOG_PATH = APP_PATH + "crashLog";
    /**
     * 崩溃日志文件地址
     */
    public static final String CRASH_LOG_FILE_PATH = CRASH_LOG_PATH + File.separator + "crash_log.txt";
    /**
     * 录音文件地址文件夹目录
     */
    public static final String APP_AUDIO_FILES_PATH = APP_PATH + "record" + File.separator;
    /**
     * 图片选择最大数
     */
    public static final int MAX_PIC_COUNT = 5;
    /**
     * onActivityResult 图片选择
     */
    public static final int REQUEST_CODE_PHOTO_CHOOSE = 1001;
    /**
     * onActivityResult  相机拍照
     */
    public static final int REQUEST_CODE_PHOTO_TAKE = 1002;

    /**
     * onActivityResult  设置页
     */
    public static final int REQUEST_CODE_SETTING = 2001;

    /**
     * onActivityResult  设置页，web地址已有
     */
    public static final int REQUEST_CODE_SETTING_REFRESH = 2002;

    /**
     * 文件上传类型：声音
     */
    public static final int UPLOAD_FILE_TYPE_RECORD=1;
    /**
     * 文件上传类型：图片
     */
    public static final int UPLOAD_FILE_TYPE_IMAGE=2;

    public static final String HTTP_PRE="http://";

    public static final String HTTPS_PRE="https://";
    /**
     * webviewl链接地址
     */
    public static final String HTTP_WEBVIEW_ADDRESS="/index.html";

    /**
     * 服务能否访问的地址
     */
    public static final String HTTP_WEBVIEW_PING="/GLink-Cloud-Mgr/API/session/currentUser";


}
