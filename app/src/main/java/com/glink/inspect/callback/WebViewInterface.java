package com.glink.inspect.callback;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.glink.inspect.ZxingActivity;
import com.glink.inspect.bus.BusProvider;
import com.glink.inspect.bus.FinishZxingEvent;
import com.glink.inspect.data.CallBackData;
import com.glink.inspect.data.Const;
import com.glink.inspect.data.ZxingData;
import com.glink.inspect.utils.AudioPlayManager;
import com.glink.inspect.utils.CommonUtil;
import com.glink.inspect.utils.FileUtil;
import com.glink.inspect.utils.GsonUtil;
import com.glink.inspect.utils.LogUtil;
import com.glink.inspect.utils.PermissionHelper;
import com.glink.inspect.utils.RecordUtil;
import com.glink.inspect.utils.ToastUtils;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity mActivity;
    private WebView mWebView;
    private CallBackData<JSONObject> mPhotoCallBackData;
    private String mChoosePhotoCallbackName;
    private String mTakePhotoCallbackName;
    private String mCameraPhotoPath;
    private RecordUtil recordUtil;
    private String currentRecordPath;
    private int maxRecordTime = 60;

    public WebViewInterface(Activity activity, WebView webView) {

        this.mActivity = activity;
        this.mWebView = webView;
        recordUtil = new RecordUtil(activity);
        recordUtil.setOnFinishedRecordListener(new RecordUtil.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                currentRecordPath = audioPath;
            }
        });
    }

    public void onPause() {
        //停止语音的播放
        AudioPlayManager.getInstance().stopPlay();
        if (recordUtil != null) {
            recordUtil.stopRecording();
        }
    }

    // 所有res都为json
    // res.code  状态码，1为正常，其他为异常
    // res.message 状态描述
    // res.data 为其他数据
    // 比如stopRecordCallback的时候返回{time:11}表示录制了11秒
    // 比如上传完成后，返回上传后服务器返回的数据

//        gl.startRecordCallback = function(res,time)
//        gl.stopRecordCallback = function(res)
//        gl.uploadRecordCallback = function(res)
//        gl.playRecordCallback = function(res,time)
//        gl.stopPlayCallback = function(res)
//        gl.choosePhotoCallback = function(res)
//        gl.takePhotoCallback = function(res)
//        gl.scanCodeCallback = function(res,codeType)


    @JavascriptInterface
    public void startRecord(int second, String stopCallbackName, String startCallbackName) {
        maxRecordTime = second;
        recordUtil.startRecorder(second, FileUtil.getRecorderPathDir(), mWebView, startCallbackName, stopCallbackName);
    }

    @JavascriptInterface
    public void stopRecord(String stopCallbackName) {
        recordUtil.stopRecorder(stopCallbackName);
    }


    @JavascriptInterface
    public void uploadRecord(JSONObject params, String callbackName) {
        CallBackData<JSONObject> mCallBackData = new CallBackData();
        mCallBackData.setCode(0);
        mCallBackData.setMessage("暂无上传接口");
        if (params != null) {
            mCallBackData.setData(params);
        }
        String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
        LogUtil.d("call js: " + loadUrl);
        mWebView.loadUrl(loadUrl);

    }

    @JavascriptInterface
    public void playRecord(final String callbackName) {
        if (TextUtils.isEmpty(currentRecordPath)) {
            CallBackData mCallBackData = new CallBackData();
            mCallBackData.setCode(0);
            mCallBackData.setMessage("没有录音文件");
            String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
            LogUtil.d("call js: " + loadUrl);
            mWebView.loadUrl(loadUrl);
            return;
        }
        AudioPlayManager.getInstance().startAudio(mActivity, currentRecordPath, maxRecordTime, mWebView, callbackName);
    }

    @JavascriptInterface
    public void stopPlay(final String callbackName) {
        if (TextUtils.isEmpty(currentRecordPath)) {
            CallBackData mCallBackData = new CallBackData();
            mCallBackData.setCode(0);
            mCallBackData.setMessage("没有录音文件");
            String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
            LogUtil.d("call js: " + loadUrl);
            mWebView.loadUrl(loadUrl);
            return;
        }
        AudioPlayManager.getInstance().stopPlay(mActivity, mWebView, callbackName);
    }

    @JavascriptInterface
    public void choosePhoto(JSONObject params, String callbackName) {
        this.mChoosePhotoCallbackName = callbackName;
        mPhotoCallBackData = new CallBackData();
        mPhotoCallBackData.setData(params);
        // 打开相册
        ImageSelector.builder()
                .useCamera(false)
                .setSingle(false)
                .setMaxSelectCount(Const.MAX_PIC_COUNT)
                .start(mActivity, Const.REQUEST_CODE_PHOTO_CHOOSE);
    }

    @JavascriptInterface
    public void takePhoto(final JSONObject params, final String callbackName) {
        PermissionHelper.checkPermission(mActivity, PermissionHelper.PermissionType.CAMERA, new PermissionHelper.OnPermissionThroughActionListener() {
            @Override
            public void onThroughAction(Boolean havePermission) {
                if (havePermission) {
                    mTakePhotoCallbackName = callbackName;
                    mPhotoCallBackData = new CallBackData();
                    mPhotoCallBackData.setData(params);
                    openCamera();
                }
            }
        });
    }

    @JavascriptInterface
    public void scanCode(final String callbackName) {
        PermissionHelper.checkPermission(mActivity, PermissionHelper.PermissionType.CAMERA, new PermissionHelper.OnPermissionThroughActionListener() {
            @Override
            public void onThroughAction(Boolean havePermission) {
                if (havePermission) {
                    mActivity.startActivity(ZxingActivity.newIntent(mActivity, callbackName));
                }
            }
        });
    }

    @JavascriptInterface
    public void quitScan() {
        BusProvider.getInstance().post(new FinishZxingEvent());
    }


    @Subscribe
    public void getZxingResult(ZxingData zxingData) {
        if (zxingData == null || TextUtils.isEmpty(zxingData.getCallbackName())) {
            return;
        }
        CallBackData<String> callBackData = new CallBackData();
        if (TextUtils.isEmpty(zxingData.getResult())) {
            callBackData.setCode(0);
            callBackData.setMessage("扫码失败");
        } else {
            callBackData.setCode(1);
            callBackData.setMessage("扫码成功");
            callBackData.setData(zxingData.getResult());
        }

        String loadUrl = "javascript:" + zxingData.getCallbackName() + "('" + GsonUtil.toJsonString(callBackData) + "','" + zxingData.getCodeType().toString() + "')";

        LogUtil.d("call js: " + loadUrl);
        mWebView.loadUrl(loadUrl);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        switch (requestCode) {
            case Const.REQUEST_CODE_PHOTO_CHOOSE:
                if (data == null) {
                    return;
                }
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                uploadImageFiles(mChoosePhotoCallbackName, images);
                break;
            case Const.REQUEST_CODE_PHOTO_TAKE:
                List<String> pathList = new ArrayList<>();
                pathList.add(mCameraPhotoPath);
                uploadImageFiles(mTakePhotoCallbackName, pathList);
                break;
            default:
                break;
        }

    }

    private void uploadImageFiles(String callbackName, List<String> imagePathList) {
        if (CommonUtil.isListNull(imagePathList)) {
            return;
        }
        if (TextUtils.isEmpty(callbackName)) {
            ToastUtils.showMsg(mActivity, "web回调名称为空");
            return;
        }
        //TODO:TEST
        {
            mPhotoCallBackData.setCode(0);
            mPhotoCallBackData.setMessage("暂无上传接口");
            String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mPhotoCallBackData) + "')";
            LogUtil.d("call js: " + loadUrl);
            mWebView.loadUrl(loadUrl);
        }
//        final ArrayList<File> fileList = new ArrayList<>();
//
//        Observable.fromIterable(imagePathList).map(new Function<String, File>() {
//            @Override
//            public File apply(String s) throws Exception {
//                LogUtil.d("img path: " + s);
//                return FileUtil.createFile(s, true);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<File>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(File file) {
//                        fileList.add(file);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        HttpRequest.uploadPics(mActivity, fileList, new BaseObserver<BaseResponse>(mActivity) {
//                            @Override
//                            public void onNext(BaseResponse baseResponse) {
//                                super.onNext(baseResponse);
//                                LogUtil.d(baseResponse.code);
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                super.onComplete();
//                                mPhotoCallBackData.setCode(1);
//                                String loadUrl = "javascript:" + mCallbackName + "('" + GsonUtil.toJsonString(mPhotoCallBackData) + "')";
//                                mWebView.loadUrl(loadUrl);
//                            }
//                        });
//                    }
//                });

    }

    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = FileUtil.createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                mCameraPhotoPath = photoFile.getAbsolutePath();
                //通过FileProvider创建一个content类型的Uri
                Uri photoUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".fileprovider", photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mActivity.startActivityForResult(captureIntent, Const.REQUEST_CODE_PHOTO_TAKE);
            }
        }
    }


}
