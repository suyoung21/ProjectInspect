package com.glink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

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
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp() || ni.getName().equals("ppp0"))
                    continue;
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ia = ips.nextElement();
                    if (ia instanceof Inet6Address)
                        continue;
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
}
