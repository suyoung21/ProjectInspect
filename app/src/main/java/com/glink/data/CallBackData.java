package com.glink.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CallBackData<T> {

    private int code;
    private String message = "";
    private T data;

    public CallBackData() {
    }


}
