package com.glink.inspect.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;

import com.glink.inspect.App;
import com.glink.inspect.data.Const;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    /**
     * 判断list是不是为null或者size小于1
     */
    public static boolean isListNull(List list) {
        return null == list || list.isEmpty();
    }

    /**
     * 获取手机的IP
     *
     * @return ip地址，失败返回0.0.0.0
     */
    public static String getLocalIp() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp() || ni.getName().equals("ppp0")){
                    continue;}
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ia = ips.nextElement();
                    if (ia instanceof Inet6Address){
                        continue;}
                    return ia.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    /**
     * 获取设备相对唯一标示
     *
     * @param context
     * @return
     */
    public static String getDeviceNum(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String device_uuid = sharedPreferences.getString("device_uuid", null);
        if (TextUtils.isEmpty(device_uuid)) {
            String devID = Build.SERIAL;
            try {
                device_uuid = !TextUtils.isEmpty(devID) ? UUID.nameUUIDFromBytes(devID.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                sharedPreferences.edit().putString("device_uuid", device_uuid).apply();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (device_uuid.contains("-")) {
            device_uuid = device_uuid.replace("-", "");
        }
        return device_uuid;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static String parseUploadUrl(String ip, String port) {
        String fullUrl;
        if (TextUtils.isEmpty(port.trim())) {
            fullUrl = ip + Const.HTTP_WEBVIEW_ADDRESS;
        } else {
            fullUrl = ip + ":" + port + Const.HTTP_WEBVIEW_ADDRESS;
        }
        if (!fullUrl.startsWith(Const.HTTP_PRE) && !fullUrl.startsWith(Const.HTTPS_PRE)) {
            fullUrl = Const.HTTP_PRE + fullUrl;
        }
        if (isValidHttp(fullUrl)) {
            return fullUrl;
        }
        return null;
    }

    public static String getPingUrl(String ip, String port) {
        String fullUrl;
        if (TextUtils.isEmpty(port.trim())) {
            fullUrl = ip + Const.HTTP_WEBVIEW_PING;
        } else {
            fullUrl = ip + ":" + port + Const.HTTP_WEBVIEW_PING;
        }
        if (!fullUrl.startsWith(Const.HTTP_PRE) && !fullUrl.startsWith(Const.HTTPS_PRE)) {
            fullUrl = Const.HTTP_PRE + fullUrl;
        }
        if (isValidHttp(fullUrl)) {
            return fullUrl;
        }
        return null;
    }

    public static boolean isValidHttp(String url) {
        Pattern pattern = Pattern.compile("(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
        Matcher validMatcher = pattern.matcher(url);
        return validMatcher.matches();
    }
}
