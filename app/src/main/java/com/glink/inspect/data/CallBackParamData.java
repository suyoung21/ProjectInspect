package com.glink.inspect.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBackParamData {
    private String orderId = "";
    private String tunnelDevId = "";
    /**
     * 上传文件URL
     */
    private String uploadUrl="";
}
