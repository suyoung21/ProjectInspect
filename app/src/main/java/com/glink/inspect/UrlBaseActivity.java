package com.glink.inspect;

import android.text.TextUtils;

import com.glink.inspect.base.BaseActivity;
import com.glink.inspect.data.ConfigData;
import com.glink.inspect.utils.SpUtil;

public class UrlBaseActivity extends BaseActivity {
    protected String url;

    protected SpUtil spUtil;

    /**
     * 获取本地域名及端口
     * @return ip==null
     */
    protected boolean refreshUrl() {
        String requestIP = ConfigData.getInstance().getRequestIP();
        boolean isNull = TextUtils.isEmpty(requestIP);
        if (isNull) {
            if (spUtil == null) {
                spUtil = new SpUtil(this);
            }
            String ip=spUtil.getIP();
            ConfigData.getInstance().setRequestIP(ip);
            ConfigData.getInstance().setRequestPort(spUtil.getPort());
            return TextUtils.isEmpty(ip);
        }
        return false;
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
