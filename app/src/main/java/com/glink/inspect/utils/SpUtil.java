package com.glink.inspect.utils;

import android.content.Context;

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
}
