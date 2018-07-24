package com.glink.inspect.base;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
    public String code;
    public String message;
    public String exceptionMessage;
    public T data;
}
