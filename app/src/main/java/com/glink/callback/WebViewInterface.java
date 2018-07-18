package com.glink.callback;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.glink.R;
import com.glink.base.BaseResponse;
import com.glink.data.CallBackData;
import com.glink.data.Const;
import com.glink.http.BaseObserver;
import com.glink.http.HttpRequest;
import com.glink.utils.AudioPlayManager;
import com.glink.utils.CommonUtil;
import com.glink.utils.FileUtil;
import com.glink.utils.GsonUtil;
import com.glink.utils.LogUtil;
import com.glink.utils.PermissionHelper;
import com.glink.utils.RecordUtil;
import com.glink.utils.ResUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity mActivity;
    private WebView mWebView;
    private CallBackData mCallBackData;
    private String mCallbackName;
    private String mCameraPhotoPath;
    private RecordUtil recordUtil;
    private String currentRecordPath;

    public WebViewInterface(Activity activity, WebView webView) {

        this.mActivity = activity;
        this.mWebView = webView;
        recordUtil = new RecordUtil(activity);
        recordUtil.setOnFinishedRecordListener(new RecordUtil.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {

            }
        });
    }

    public void onPause() {
        //停止语音的播放
        AudioPlayManager.getInstance().stopPlay();
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
        currentRecordPath = getRecorderPath();
        recordUtil.startRecorder(second, currentRecordPath, mWebView, startCallbackName, stopCallbackName);
    }

    @JavascriptInterface
    public void stopRecord(String stopCallbackName) {
        recordUtil.cancelRecorder();
    }


    @JavascriptInterface
    public void uploadRecord(String params, String callbackName) {

    }

    @JavascriptInterface
    public void playRecord(final String callbackName) {
        AudioPlayManager.getInstance().startAudio(currentRecordPath);
        AudioPlayManager.getInstance().setOnFinishedRecordListener(new AudioPlayManager.OnFinishedPlayListener() {
            @Override
            public void onFinishedPlay() {
                mCallBackData = new CallBackData();
                mCallBackData.setCode(1);
                mCallBackData.setMessage("录音播放完毕");
                String loadUrl = "javascript:" + callbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
                mWebView.loadUrl(loadUrl);
            }
        });
    }

    @JavascriptInterface
    public void choosePhoto(String params, String callbackName) {
        mCallBackData = new CallBackData();
        this.mCallbackName = callbackName;
        mCallBackData.setData(params);
        ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(Const.MAX_PIC_COUNT) // 图片的最大选择数量，小于等于0时，不限数量。
                .start(mActivity, Const.REQUEST_CODE_PHOTO_CHOOSE); // 打开相册
    }

    @JavascriptInterface
    public void takePhoto(final String params, final String callbackName) {
        PermissionHelper.checkPermission(mActivity, PermissionHelper.PermissionType.CAMERA, new PermissionHelper.OnPermissionThroughActionListener() {
            @Override
            public void onThroughAction(Boolean havePermission) {
                if (havePermission) {
                    mCallBackData = new CallBackData();
                    mCallbackName = callbackName;
                    mCallBackData.setData(params);
                    openCamera();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        List<String> pathList = new ArrayList<>();
        switch (requestCode) {
            case Const.REQUEST_CODE_PHOTO_CHOOSE:
                if (data == null) {
                    return;
                }
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                pathList.addAll(images);
                break;
            case Const.REQUEST_CODE_PHOTO_TAKE:
                pathList.add(mCameraPhotoPath);
                break;
            default:
                break;
        }
        uploadImageFiles(pathList);

    }

    private void uploadImageFiles(List<String> imagePathList) {
        if (CommonUtil.isListNull(imagePathList)) {
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
                        HttpRequest.uploadPics(mActivity, fileList, new BaseObserver<BaseResponse>(mActivity) {
                            @Override
                            public void onNext(BaseResponse baseResponse) {
                                super.onNext(baseResponse);
                                LogUtil.d(baseResponse.code);
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                                mCallBackData.setCode(1);
                                String loadUrl = "javascript:" + mCallbackName + "('" + GsonUtil.toJsonString(mCallBackData) + "')";
                                mWebView.loadUrl(loadUrl);
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
                photoFile = createImageFile();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format(ResUtil.getString(R.string.app_name) + "_%s.jpg", timeStamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageFileName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    private String getRecorderPath() {
        String storageDir = Const.APP_AUDIO_FILES_PATH;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = String.format(ResUtil.getString(R.string.app_name) + "_%s.mp3", timeStamp);
        File tempFile = new File(storageDir, fileName);
        return tempFile.getAbsolutePath();

    }

}
