package com.glink.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.glink.App;
import com.glink.data.Const;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mLocalExceptionHandler;
    //CrashHandler实例
    private static CrashHandler mInstance = null;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息
    private SortedMap<String, String> mDeviceInfos = new TreeMap<String, String>();
    //用来存储应用信息
    private SortedMap<String, String> mAppInfos = new TreeMap<String, String>();


    private boolean mInitialized = false;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        synchronized (CrashHandler.class) {
            if (null == mInstance) {
                mInstance = new CrashHandler();
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void initialize(Context context) {

        if (mInitialized) {
            return;
        }

        Set<String> neededPermissions = new TreeSet<>();
        neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!checkPermissions(context, neededPermissions)) {
            StringBuilder sb = new StringBuilder();
            for (String permission : neededPermissions) {
                sb.append(permission).append("\n");
            }
            throw new IllegalArgumentException("Permissions needed:\n " + sb.toString());
        }

        // 在单例中使用全局Application Context
        mContext = context.getApplicationContext();

        //获取系统默认的UncaughtException处理器
        mLocalExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        mInitialized = true;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mLocalExceptionHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mLocalExceptionHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

        }
        LogUtil.i("crashlog", "捕获到错误日志");

        handleException(throwable);

//        if (mLocalExceptionHandler != null) {
//            //如果用户没有处理则让系统默认的异常处理器来处理
//            mLocalExceptionHandler.uncaughtException(thread, throwable);
//        } else {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
        }
        App.getInstance().finishAllActivity();
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
//        }
    }


    /**
     * 检查是否配置了某个权限
     *
     * @param context
     * @param permision
     * @return
     */
    private boolean checkPermission(Context context, String permision) {
        if (null == permision || "".equals(permision)) {
            return true;
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] userPermissions = packageInfo.requestedPermissions;
            if (null != userPermissions) {
                for (String userPermision : userPermissions) {
                    if (permision.equals(userPermision)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 检查所有的权限是否都有设置
     *
     * @param context
     * @param permisions
     * @return
     */
    private boolean checkPermissions(Context context, Collection<String> permisions) {
        if (null == permisions || permisions.isEmpty()) {
            return true;
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        Set<String> requestedPermissions = new TreeSet<String>();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] userPermissions = packageInfo.requestedPermissions;
            if (null != userPermissions) {
                for (String userPermission : userPermissions) {
                    requestedPermissions.add(userPermission);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requestedPermissions.containsAll(permisions);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        //收集设备参数信息
        collectDeviceInfo();

        //收集应用信息
        collectAppInfo(mContext);

        //保存日志文件
        saveCrashInfo2File(throwable);

        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        mDeviceInfos.put("Build.HARDWARE", Build.HARDWARE);
        mDeviceInfos.put("Build.MANUFACTURER", Build.MANUFACTURER);
        mDeviceInfos.put("Build.FINGERPRINT", Build.FINGERPRINT);
        mDeviceInfos.put("Build.BOARD", Build.BOARD);
        mDeviceInfos.put("Build.PRODUCT", Build.PRODUCT);
        mDeviceInfos.put("Build.DEVICE", Build.DEVICE);
        mDeviceInfos.put("Build.MODEL", Build.MODEL);
        mDeviceInfos.put("Build.BOOTLOADER", Build.BOOTLOADER);
        mDeviceInfos.put("Build.BRAND", Build.BRAND);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDeviceInfos.put("Build.CPU_ABI", Build.CPU_ABI);
            mDeviceInfos.put("Build.CPU_ABI2", Build.CPU_ABI2);
        } else {
            try {
                String[] suportedABIs = Build.SUPPORTED_ABIS;
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (String abi : suportedABIs) {
                    sb.append(abi).append(",");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append("]");
                mDeviceInfos.put("Build.SUPPORTED_ABIS", sb.toString());
            } catch (NoSuchFieldError e) {
                try {
                    mDeviceInfos.put("Build.CPU_ABI", Build.CPU_ABI);
                    mDeviceInfos.put("Build.CPU_ABI2", Build.CPU_ABI2);
                } catch (Exception e2) {

                }
            }
        }

        // 添加系统版本信息
        mDeviceInfos.put("Build.VERSION.RELEASE", Build.VERSION.RELEASE);
        mDeviceInfos.put("Build.VERSION.SDK_INT", String.valueOf(Build.VERSION.SDK_INT));
    }

    /**
     * 收集应用信息
     *
     * @param context
     */
    private void collectAppInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = String.valueOf(packageInfo.versionCode);
                mAppInfos.put("versionName", versionName);
                mAppInfos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param throwable
     */
    private void saveCrashInfo2File(Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss.SSSS", Locale.getDefault());
        String time = formatter.format(new Date(System.currentTimeMillis()));
        sb.append(time + "\n");
        sb.append("Device information:\n");
        for (Map.Entry<String, String> entry : mDeviceInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        sb.append("\nApplication information:\n");
        for (Map.Entry<String, String> entry : mAppInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("\nError information:\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result + "\n" + "-------------------------\n");

        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 文件路径
                String path = Const.CRASH_LOG_PATH;
                File dir = new File(Environment.getExternalStorageDirectory(), path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(Const.CRASH_LOG_FILE_PATH);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(Const.CRASH_LOG_FILE_PATH, true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(sb.toString());
                bufferWritter.close();
            }
        } catch (Exception e) {
            Log.e("crashlog", "an error occured while writing file...", e);
        }
    }
}