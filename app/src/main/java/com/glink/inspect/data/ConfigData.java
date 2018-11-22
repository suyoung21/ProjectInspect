package com.glink.inspect.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigData {

    private static ConfigData instance;

    public static ConfigData getInstance() {
        if (instance == null) {
            instance = new ConfigData();
        }
        return instance;
    }

    private ConfigData() {
    }

    private String requestIP;
    private String requestPort;
    /**
     * 上传文件URl
     */
    private String uploadUrl="";
}
