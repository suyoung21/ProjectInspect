package com.glink.inspect.data;

import com.google.zxing.BarcodeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZxingData {
    private String result;
    private BarcodeFormat codeType;
    private String callbackName;

    public ZxingData(String result, BarcodeFormat codeType) {
        this.result = result;
        this.codeType = codeType;
    }

    public ZxingData(String result, BarcodeFormat codeType, String callbackName) {
        this.result = result;
        this.codeType = codeType;
        this.callbackName = callbackName;
    }
}
