package com.glink.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CallBackData {

    private int code;
    private String message = "";
    private String data;

    public CallBackData() {
    }

    public CallBackData(int code, String msg, String data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }


}
