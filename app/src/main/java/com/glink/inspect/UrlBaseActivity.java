package com.glink.inspect;

import android.text.TextUtils;

import com.glink.inspect.base.BaseActivity;
import com.glink.inspect.data.ConfigData;
import com.glink.inspect.utils.SpUtil;

public class UrlBaseActivity extends BaseActivity {
    protected String url;

    protected SpUtil spUtil;

    protected void refreshUrl() {
        String requestIP = ConfigData.getInstance().getRequestIP();

        if (TextUtils.isEmpty(requestIP)) {
            if (spUtil == null) {
                spUtil = new SpUtil(this);
            }
            ConfigData.getInstance().setRequestIP(spUtil.getIP());
            ConfigData.getInstance().setRequestPort(spUtil.getPort());
        }

    }

    protected void setDataCache(String ip, String port) {
        if (spUtil == null) {
            spUtil = new SpUtil(this);
        }
        spUtil.setIP(ip);
        spUtil.setPort(port);
        ConfigData.getInstance().setRequestIP(ip);
        ConfigData.getInstance().setRequestPort(port);
    }

    protected String getRequestIP() {
        return ConfigData.getInstance().getRequestIP();
    }

    protected String getRequestPort() {
        return ConfigData.getInstance().getRequestPort();
    }
}
