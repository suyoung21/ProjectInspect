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
import com.glink.inspect.base.BaseResponse;
import com.glink.inspect.bus.BusProvider;
import com.glink.inspect.bus.FinishZxingEvent;
import com.glink.inspect.data.CallBackData;
import com.glink.inspect.data.CallBackParamData;
import com.glink.inspect.data.CommonData;
import com.glink.inspect.data.ConfigData;
import com.glink.inspect.data.Const;
import com.glink.inspect.http.BaseObserver;
import com.glink.inspect.http.HttpRequest;
import com.glink.inspect.utils.AudioPlayManager;
import com.glink.inspect.utils.CommonUtil;
import com.glink.inspect.utils.FileUtil;
import com.glink.inspect.utils.GsonUtil;
import com.glink.inspect.utils.LogUtil;
import com.glink.inspect.utils.PermissionHelper;
import com.glink.inspect.utils.RecordUtil;
import com.glink.inspect.utils.SpUtil;
import com.glink.inspect.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity mActivity;
    private WebView mWebView;
    private CallBackData<String> mPhotoCallBackData;
    private String mChoosePhotoCallbackName;
    private String mTakePhotoCallbackName;
    private CallBackParamData mChoosePhotoParamData;
    private CallBackParamData mTakePhotoParamData;
    private String mCameraPhotoPath;
    private RecordUtil recordUtil;
    private String currentRecordPath;
    private int maxRecordTime = 60;
    private SpUtil spUtil;
    private ConfigData configData;

    public WebViewInterface(Activity activity, WebView webView) {

        this.mActivity = activity;
        this.mWebView = webView;
        spUtil = new SpUtil(activity);
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
        if (TextUtils.isEmpty(startCallbackName) || TextUtils.isEmpty(stopCallbackName)) {
            return;
        }
        maxRecordTime = second;
        recordUtil.startRecorder(second, FileUtil.getRecorderPathDir(), mWebView, startCallbackName, stopCallbackName);
    }

    @JavascriptInterface
    public void stopRecord(String stopCallbackName) {
        if (TextUtils.isEmpty(stopCallbackName)) {
            return;
        }
        recordUtil.stopRecorder(stopCallbackName);
    }


    @JavascriptInterface
    public void uploadRecord(String params, String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
        if (TextUtils.isEmpty(currentRecordPath)) {
            LogUtil.e("record file path is null");
            return;
        }
        List<String> pathList = new ArrayList<>();
        pathList.add(currentRecordPath);
        uploadRecordFile(callbackName, params, pathList);
    }

    @JavascriptInterface
    public void playRecord(final String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
        if (TextUtils.isEmpty(currentRecordPath)) {
            CallBackData mCallBackData = new CallBackData();
            mCallBackData.setCode(0);
            mCallBackData.setMessage("没有录音文件");
            String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
            webViewLoadUrl(loadUrl);
            return;
        }
        AudioPlayManager.getInstance().startAudio(mActivity, currentRecordPath, maxRecordTime, mWebView, callbackName);
    }

    @JavascriptInterface
    public void stopPlay(final String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
        if (TextUtils.isEmpty(currentRecordPath)) {
            CallBackData mCallBackData = new CallBackData();
            mCallBackData.setCode(0);
            mCallBackData.setMessage("没有录音文件");
            String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
            webViewLoadUrl(loadUrl);
            return;
        }
        AudioPlayManager.getInstance().stopPlay(mActivity, mWebView, callbackName);
    }

    @JavascriptInterface
    public void choosePhoto(String params, String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
        this.mChoosePhotoCallbackName = callbackName;
        mChoosePhotoParamData = GsonUtil.jsonToObject(params, CallBackParamData.class);
        mPhotoCallBackData = new CallBackData();

        // 打开相册
        ImageSelector.builder()
                .useCamera(false)
                .setSingle(false)
                .setMaxSelectCount(Const.MAX_PIC_COUNT)
                .start(mActivity, Const.REQUEST_CODE_PHOTO_CHOOSE);
    }

    @JavascriptInterface
    public void takePhoto(final String params, final String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
        mTakePhotoParamData = GsonUtil.jsonToObject(params, CallBackParamData.class);
        PermissionHelper.checkPermission(mActivity, PermissionHelper.PermissionType.CAMERA, new PermissionHelper.OnPermissionThroughActionListener() {
            @Override
            public void onThroughAction(Boolean havePermission) {
                if (havePermission) {
                    mTakePhotoCallbackName = callbackName;
                    mPhotoCallBackData = new CallBackData();
                    openCamera();
                }
            }
        });
    }

    @JavascriptInterface
    public void scanCode(final String callbackName) {
        if (TextUtils.isEmpty(callbackName)) {
            return;
        }
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

    @JavascriptInterface
    public void setConfig(String configJson) {
        configData = GsonUtil.jsonToObject(configJson, ConfigData.class);
        if (configData != null) {
            if (!TextUtils.isEmpty(configData.getUploadUrl())) {
                spUtil.setUploadUrl(configData.getUploadUrl());
                LogUtil.d("get upload url: " + configData.getUploadUrl());
            }
        }
    }

    private String getUploadUrl() {
        String url = "";
        if (configData != null && !TextUtils.isEmpty(configData.getUploadUrl())) {
            url = configData.getUploadUrl();
        }
        if (TextUtils.isEmpty(url)) {
            url = spUtil.getUploadUrl();
        }
        return url;
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
                uploadImageFiles(mChoosePhotoCallbackName, mChoosePhotoParamData, images);
                break;
            case Const.REQUEST_CODE_PHOTO_TAKE:
                List<String> pathList = new ArrayList<>();
                pathList.add(mCameraPhotoPath);
                uploadImageFiles(mTakePhotoCallbackName, mTakePhotoParamData, pathList);
                break;
            default:
                break;
        }

    }

    private void uploadRecordFile(final String callbackName, final String param, final List<String> filePathList) {
        if (CommonUtil.isListNull(filePathList)) {
            return;
        }
        if (TextUtils.isEmpty(callbackName)) {
            ToastUtils.showMsg(mActivity, "web callback name is null");
            return;
        }
        final CallBackParamData paramData = GsonUtil.jsonToObject(param, CallBackParamData.class);
        if (paramData == null || TextUtils.isEmpty(paramData.getOrderId()) || TextUtils.isEmpty(paramData.getTunnelDevId())) {
            ToastUtils.showMsg(mActivity, "web json param has null");
            return;
        }
        final ArrayList<File> fileList = new ArrayList<>();
        final CallBackData<String> callBackData = new CallBackData<>();

        Observable.fromIterable(filePathList).map(new Function<String, File>() {
            @Override
            public File apply(String s) throws Exception {
                LogUtil.d("record path: " + s);
                return FileUtil.createFile(s, true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        fileList.add(file);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        HttpRequest.uploadFile(mActivity, getUploadUrl(), paramData, Const.UPLOAD_FILE_TYPE_RECORD, fileList, new BaseObserver<BaseResponse>(mActivity) {
                            @Override
                            public void onNext(BaseResponse baseResponse) {
                                super.onNext(baseResponse);
                                LogUtil.d(baseResponse.code);
//                                try {
//                                    JSONObject jsonObject=new JSONObject();
//                                    callBackData.setData(jsonObject);
//                                }catch (JSONException e){
//                                    e.printStackTrace();
//                                }
                                callBackData.setData(GsonUtil.toJsonString(baseResponse));
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                                callBackData.setCode(1);
                                String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
                                String newUrl = loadUrl.replace("\\", "\\\\");
                                webViewLoadUrl(newUrl);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                CallBackData callBackData = new CallBackData();
                                if (e instanceof HttpException) {
                                    //HTTP错误
                                    HttpException httpException = (HttpException) e;
                                    callBackData.setCode(httpException.code());
                                } else {
                                    callBackData.setCode(0);
                                }
                                callBackData.setMessage(e.getMessage());
                                String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
                                String newUrl = loadUrl.replace("\\", "\\\\");
                                webViewLoadUrl(newUrl);
                                ToastUtils.showMsg(mActivity, "http error: " + e.getMessage());
                            }
                        });
                    }
                });

    }

    private void uploadImageFiles(final String callbackName, final CallBackParamData paramData, final List<String> imagePathList) {
        if (CommonUtil.isListNull(imagePathList)) {
            return;
        }
        if (TextUtils.isEmpty(callbackName)) {
            ToastUtils.showMsg(mActivity, "web callback name is null");
            return;
        }
        if (paramData == null || TextUtils.isEmpty(paramData.getOrderId()) || TextUtils.isEmpty(paramData.getTunnelDevId())) {
            ToastUtils.showMsg(mActivity, "web json param has null");
            return;
        }
        final ArrayList<File> fileList = new ArrayList<>();

        Observable.fromIterable(imagePathList).map(new Function<String, File>() {
            @Override
            public File apply(String s) throws Exception {
                LogUtil.d("img path: " + s);
                return FileUtil.createFile(s, true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        fileList.add(file);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        HttpRequest.uploadFile(mActivity, getUploadUrl(), paramData, Const.UPLOAD_FILE_TYPE_IMAGE, fileList, new BaseObserver<BaseResponse>(mActivity) {
                            @Override
                            public void onNext(BaseResponse baseResponse) {
                                super.onNext(baseResponse);
                                LogUtil.d(baseResponse.code);
                                mPhotoCallBackData.setData(GsonUtil.toJsonString(baseResponse));
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                                mPhotoCallBackData.setCode(1);
                                String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mPhotoCallBackData) + "')";
                                String newUrl = loadUrl.replace("\\", "\\\\");
                                webViewLoadUrl(newUrl);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                CallBackData callBackData = new CallBackData();
                                if (e instanceof HttpException) {
                                    //HTTP错误
                                    HttpException httpException = (HttpException) e;
                                    callBackData.setCode(httpException.code());
                                } else {
                                    callBackData.setCode(0);
                                }
                                callBackData.setMessage(e.getMessage());
                                String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
                                String newUrl = loadUrl.replace("\\", "\\\\");
                                webViewLoadUrl(newUrl);
                                ToastUtils.showMsg(mActivity, "http error: " + e.getMessage());
                            }
                        });
                    }
                });

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

    private synchronized void webViewLoadUrl(String loadUrl) {
        mActivity.runOnUiThread(() -> {
            LogUtil.d("call js: " + loadUrl);
            mWebView.loadUrl(loadUrl);
        });
    }

    private List<CommonData> praseJsonToCommonData(String params) {
        List<CommonData> dataList = new ArrayList<CommonData>();
        if (!TextUtils.isEmpty(params)) {
            try {
                //Json的解析类对象
                JsonParser parser = new JsonParser();
                //将JSON的String 转成一个JsonArray对象
                JsonArray jsonArray = parser.parse(params).getAsJsonArray();
                Gson gson = new Gson();
                //加强for循环遍历JsonArray
                for (JsonElement user : jsonArray) {
                    //使用GSON，直接转成Bean对象
                    CommonData commonData = gson.fromJson(user, CommonData.class);
                    dataList.add(commonData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dataList;
    }
}
