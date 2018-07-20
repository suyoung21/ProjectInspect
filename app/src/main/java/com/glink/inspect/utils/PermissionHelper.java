package com.glink.inspect.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.glink.inspect.App;
import com.glink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态权限申请
 * @author jiangshuyang
 */

public class PermissionHelper {
    public static class PermissionType {
        public static int WRITE_EXTERNAL_STORAGE = 0;//读写
        public static int RECORD_AUDIO = 1;//语音
        public static int CAMERA = 2;//相机
        public static int READ_PHONE_STATE = 3;//手机状态
        public static int READ_CONTACTS = 4;//通讯录
        public static int VIBRATE = 5;//震动
        public static int LOCATION = 6;//GPS定位
        public static int SMS = 7;//发短信
        public static int READ_CALENDAR = 8;
        public static int WRITE_CALENDAR = 9;
        public static int LOCATION_FINE = 10;
        public static int LOCATION_COARSE = 11;
    }

    private static final int REQ_PERMISSION_CODE = 0x11;
    private static final int REQ_PERMISSION_SINGLE_CODE = 0x12;
    //需要申请的权限列表(0 & 1 必须)
    private static final String[] fPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    //Manifest.permission.SYSTEM_ALERT_WINDOW,//极度敏感,同Settings.ACTION_MANAGE_OVERLAY_PERMISSION

    private static OnPermissionThroughActionListener onPermissionThroughActionListener;
    private static Activity mActivity;

    /**
     * 权限未开启提示
     *
     * @param permission
     * @return
     */
    public static String getToastContent(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }
        String toast;
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                toast = App.getInstance().getString(R.string.permission_write_external_storage);
                break;
            case Manifest.permission.CAMERA:
                toast = App.getInstance().getString(R.string.permission_camera);
                break;
            case Manifest.permission.READ_CONTACTS:
                toast = App.getInstance().getString(R.string.permission_contact);
                break;
            case Manifest.permission.RECORD_AUDIO:
                toast = App.getInstance().getString(R.string.permission_audio);
                break;
            case Manifest.permission.READ_PHONE_STATE:
                toast = App.getInstance().getString(R.string.permission_phone_state);
                break;
            case Manifest.permission.SEND_SMS:
                toast = App.getInstance().getString(R.string.permission_sms);
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                toast = App.getInstance().getString(R.string.permission_location);
                break;
            default:
                toast = App.getInstance().getString(R.string.permission_error);
                break;
        }
        return toast;
    }

    /**
     * 动态单个权限申请
     */
    public static void checkPermission(Activity activity, int permissionIndex, OnPermissionThroughActionListener onPermissionThroughListener) {
        mActivity = activity;
        //6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionIndex >= 0 && permissionIndex <= fPermissions.length) {
            //检查权限是不是已经授予
            List<String> noOkPermissions = new ArrayList<>();
            String permission = fPermissions[permissionIndex];
            // 检查该权限是否已经获取:GRANTED---授权  DINIED---拒绝
            // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                noOkPermissions.add(permission);
            }
            //该权限已经授予，不再申请
            if (noOkPermissions.size() <= 0) {
                onPermissionThroughListener.onThroughAction(true);
                return;
            }
            onPermissionThroughActionListener = onPermissionThroughListener;
            //兼容包jar
            ActivityCompat.requestPermissions(activity, noOkPermissions.toArray(new String[noOkPermissions.size()]), REQ_PERMISSION_SINGLE_CODE);
        } else {
            //6.0以下下不需要申请
            onPermissionThroughListener.onThroughAction(true);
        }
    }

    /**
     * 全部权限申请
     */
    public static void checkPermissions(Activity activity, OnPermissionThroughActionListener onPermissionThroughListener) {
        mActivity = activity;
        //6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限是不是已经授予
            List<String> noOkPermissions = new ArrayList<>();
            for (int i = 0; i < 2; ++i) {
                // 检查该权限是否已经获取:GRANTED---授权  DINIED---拒绝
                // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED
//				if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                if (ContextCompat.checkSelfPermission(activity, fPermissions[i]) == PackageManager.PERMISSION_DENIED) {
                    noOkPermissions.add(fPermissions[i]);
                }
            }
            //该权限已经授予，不再申请
            if (noOkPermissions.size() <= 0) {
                onPermissionThroughListener.onThroughAction(true);
                return;
            }
            onPermissionThroughActionListener = onPermissionThroughListener;
            //6.0以上需要申请权限
            ActivityCompat.requestPermissions(activity, noOkPermissions.toArray(new String[noOkPermissions.size()]), REQ_PERMISSION_CODE);//兼容包jar

        } else {
            //6.0以下下不需要申请
            onPermissionThroughListener.onThroughAction(true);
        }
    }

    /**
     * 权限验证
     *
     * @param activity
     * @param onPermissionThroughListener
     * @param permissionIndexs
     */
    public static void checkPermissions(Activity activity, OnPermissionThroughActionListener onPermissionThroughListener, int... permissionIndexs) {
        mActivity = activity;
        //6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限是不是已经授予
            List<String> noOkPermissions = new ArrayList<>();
            for (int i = 0; i < permissionIndexs.length; i++) {
                // 检查该权限是否已经获取:GRANTED---授权  DINIED---拒绝
                // 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED
//				if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {

                if (ContextCompat.checkSelfPermission(activity, fPermissions[permissionIndexs[i]]) == PackageManager.PERMISSION_DENIED) {
                    noOkPermissions.add(fPermissions[permissionIndexs[i]]);
                }
            }
            //该权限已经授予，不再申请
            if (noOkPermissions.size() <= 0) {
                onPermissionThroughListener.onThroughAction(true);
                return;
            }
            onPermissionThroughActionListener = onPermissionThroughListener;
            //6.0以上需要申请权限
            ActivityCompat.requestPermissions(activity, noOkPermissions.toArray(new String[noOkPermissions.size()]), REQ_PERMISSION_CODE);//兼容包jar

        } else {
            //6.0以下下不需要申请
            onPermissionThroughListener.onThroughAction(true);
        }
    }

    /**
     * 全部权限申请
     */
    public static void checkCameraPermissions(Activity activity, OnPermissionThroughActionListener onPermissionThroughListener) {
        mActivity = activity;
        //6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限是不是已经授予
            List<String> noOkPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(activity, fPermissions[3]) == PackageManager.PERMISSION_DENIED) {
                noOkPermissions.add(fPermissions[3]);
            }
            if (ContextCompat.checkSelfPermission(activity, fPermissions[4]) == PackageManager.PERMISSION_DENIED) {
                noOkPermissions.add(fPermissions[4]);
            }

            //该权限已经授予，不再申请
            if (noOkPermissions.size() <= 0) {
                onPermissionThroughListener.onThroughAction(true);
                return;
            }
            onPermissionThroughActionListener = onPermissionThroughListener;
            //6.0以上需要申请权限
            ActivityCompat.requestPermissions(activity, noOkPermissions.toArray(new String[noOkPermissions.size()]), REQ_PERMISSION_CODE);//兼容包jar

        } else {
            //6.0以下下不需要申请
            onPermissionThroughListener.onThroughAction(true);
        }
    }

    /**
     * 处理权限申请的结果，返回结构化的数据
     *
     * @param requestCode  请求码
     * @param permissions  被请求的权限
     * @param grantResults 请求结果
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQ_PERMISSION_CODE && requestCode != REQ_PERMISSION_SINGLE_CODE) {
            return;
        }
        if (onPermissionThroughActionListener == null)
            return;
        Map<String, Integer> result = new HashMap<>();
        boolean isHavePermissionNotOk = false;

        for (int i = 0; i < permissions.length; i++) {
            result.put(permissions[i], grantResults[i]);
            //有权限没有同意
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isHavePermissionNotOk = true;
            }
        }
        if (!isHavePermissionNotOk) {
            onPermissionThroughActionListener.onThroughAction(true);
        } else {
            Set<String> permissionSet = result.keySet();
            if (REQ_PERMISSION_SINGLE_CODE == requestCode) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionSet.iterator().next())) {
                    ToastUtils.showMsg(App.getAppContext(), PermissionHelper.getToastContent(permissionSet.iterator().next()));
                } else {
                    //open setting
                    String message = String.format(ResUtil.getString(R.string.permission_setting_content_pre), ResUtil.getString(R.string.app_name)).concat(getToastContent(permissions[0]));
                    showGoToSettingDialog(message);
                }

            } else if (REQ_PERMISSION_CODE == requestCode) {
//                Boolean isShouldShowRequest = true;
//
//                for (String str : permissionSet) {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, str)) {
//                        isShouldShowRequest = false;
//                    }
//                }
                //open setting
                String message = ResUtil.getString(R.string.permission_setting_content_all);
                showGoToSettingDialog(message);
            }

        }
    }

    public interface OnPermissionThroughActionListener {
        void onThroughAction(Boolean havePermission);
    }

    public static void showGoToSettingDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        builder.setPositiveButton(ResUtil.getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onPermissionThroughActionListener != null){
                    onPermissionThroughActionListener.onThroughAction(false);}
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                    mActivity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(ResUtil.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onPermissionThroughActionListener != null){
                    onPermissionThroughActionListener.onThroughAction(false);}
            }
        });

        builder.setMessage(message);
        builder.show();
    }

    public static void showGoToSettingDialog(final Activity activity,String message,final OnPermissionThroughActionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        builder.setPositiveButton(ResUtil.getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null){
                    listener.onThroughAction(false);}
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(ResUtil.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null){
                    listener.onThroughAction(false);}
            }
        });

        builder.setMessage(message);
        builder.show();
    }

}
