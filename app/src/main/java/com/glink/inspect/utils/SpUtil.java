package com.glink.inspect.utils;

import android.content.Context;


/**
 * @author jiangshuyang
 */
public class SpUtil extends SpBaseUtil {

    public SpUtil(Context context) {
        super(context, context.getPackageName() + "glink");
    }

    public void setUploadUrl(String url) {
        put("upload_file_url", url);
    }

    public String getUploadUrl() {
        return (String)getSharedPreference("upload_file_url", "");
    }

    public void setIP(String ip) {
        put("request_ip", ip);
    }

    public String getIP() {
        return (String)getSharedPreference("request_ip", "");
    }

    public void setPort(String port) {
        put("request_port", port);
    }

    public String getPort() {
        return (String)getSharedPreference("request_port", "");
    }
}
