package com.glink.callback;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity activity;
    private WebView webView;

    public WebViewInterface(Activity activity, WebView webView) {

        this.activity = activity;
        this.webView = webView;
    }

    // 所有res都为json
    // res.code  状态码，1为正常，其他为异常
    // res.message 状态描述
    // res.data 为其他数据
    // 比如stopRecordCallback的时候返回{time:11}表示录制了11秒
    // 比如上传完成后，返回上传后服务器返回的数据

    //    gl.startRecordCallback = function(res,time)
    //gl.stopRecordCallback = function(res)
    //    gl.uploadRecordCallback = function(res)
    //    gl.playRecordCallback = function(res,time)
    //    gl.choosePhotoCallback = function(res)
    //    gl.takePhotoCallback = function(res)


    @JavascriptInterface
    public void startRecord(int second, String stopCallbackName, String startCallbackName) {

    }

    @JavascriptInterface
    public void stopRecord(String stopCallbackName) {
    }


    @JavascriptInterface
    public void uploadRecord(String params, String callbackName) {
    }

    @JavascriptInterface
    public void playRecord(String callbackName) {
    }

    @JavascriptInterface
    public void choosePhoto(String params, String callbackName) {
    }

    @JavascriptInterface
    public void takePhoto(String params, String callbackName) {
    }
}
