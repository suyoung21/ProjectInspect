package com.glink.callback;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.glink.data.CallBackData;
import com.glink.data.Const;
import com.glink.utils.GsonUtil;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity mActivity;
    private WebView mWebView;
    private CallBackData callBackData;
    private String callbackName;

    public WebViewInterface(Activity activity, WebView webView) {

        this.mActivity = activity;
        this.mWebView = webView;
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
        callBackData = new CallBackData();
        this.callbackName = callbackName;
        callBackData.setData(params);
        ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(Const.MAX_PIC_COUNT) // 图片的最大选择数量，小于等于0时，不限数量。
                .start(mActivity, Const.REQUEST_CODE_PHOTO_CHOOSE); // 打开相册
    }

    @JavascriptInterface
    public void takePhoto(String params, String callbackName) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != -1 || data == null) {
            return;
        }

        switch (requestCode) {
            case Const.REQUEST_CODE_PHOTO_CHOOSE:
//                String loadUrl = "javascript:gl.choosePhotoCallback('" + gameBean.getGameId() + "'," + status + "," + progress + ")";
                callBackData.setCode(1);
                String loadUrl = "javascript:" + this.callbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
                mWebView.loadUrl(loadUrl);
                break;
            default:
                break;
        }

    }

//    String loadUrl = "javascript:gt.setGameStatus('" + gameBean.getGameId() + "'," + status + "," + progress + ")";
}
