package com.glink.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.WebView;

import com.czt.mp3recorder.MP3Recorder;
import com.glink.App;
import com.glink.data.CallBackData;

import org.json.JSONObject;

import java.io.File;

public class RecordUtil implements MediaRecorder.OnErrorListener {

    //最大计时时间
    private int MAX_COUNT_DOWN_TIME = 60;
    private static final int MIN_INTERVAL_TIME = 1000;// 1000ms
    private static final int QUICK_TOUCH_TIME = 800;
    private static final int IS_ALMOST_REACH_MAX_TIME = 1;
    private static final int IS_ALREADY_REACH_MAX_TIME = 2;
    private static final int FLAG_LOOP = 3;//倒计时

    private Context mContext;
    private String mFileParentPath = null;
    private String mFilePath = null;
    private OnFinishedRecordListener finishedListener;
    private MP3Recorder recorder;
    private boolean isCanceled = false;
    private boolean isFinished = false;
    private int countDownTime;
    private long startTime;
    private boolean isCountDown = false;
    // 防快速点击录音按钮
    private static long lastClickTime;

    private WebView mWebView;
    private String mStartCallbackName;
    private String mStopCallbackName;

    public RecordUtil(Context context) {
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case IS_ALREADY_REACH_MAX_TIME:
                    ToastUtils.showMsg(getContext(), "语音时长超过" + MAX_COUNT_DOWN_TIME + "秒！抱歉，请重新发送！");
                    callWebStop(0, "语音时长超过" + MAX_COUNT_DOWN_TIME + "秒！", 0);
                    break;
                case MIN_INTERVAL_TIME:
                    ToastUtils.showMsg(getContext(), "时间太短！");
                    callWebStop(0, "时间太短！", 0);
                    break;
                case IS_ALMOST_REACH_MAX_TIME:
                    if (!isFinished) {
                        isFinished = true;
                        finishRecord();
                        ToastUtils.showMsg(getContext(), "语音时长达到" + MAX_COUNT_DOWN_TIME + "秒");
                    }
                    break;
                case FLAG_LOOP:
                    // 设置倒计时文字
//                    countDownHandler.sendEmptyMessage(countDownTime);
//                    mProgressBar.setProgress((60 - countDownTime));
                    callWebStart(1, "录音中...", (60 - countDownTime));
                    if (countDownTime == 0) {
                        mHandler.sendEmptyMessage(IS_ALMOST_REACH_MAX_TIME);
                        break;
                    }
                    countDownTime--;
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(FLAG_LOOP), MIN_INTERVAL_TIME);
                    break;
                default:
                    break;
            }
        }
    };

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < QUICK_TOUCH_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void startRecorder(int maxTime, String path, WebView webView, String startCallbackName, String stopCallbackName) {
        if (isFastDoubleClick()) {
            return;
        }
        if (!hasSdcard()) {
            ToastUtils.showMsg(getContext(), "没有内存卡，请安装内存卡！");
            callWebStop(0, "没有内存卡，请安装内存卡！", 0);
            return;
        }
        if (TextUtils.isEmpty(path)) {
            ToastUtils.showMsg(getContext(), "未设置保存路径");
            callWebStop(0, "未设置保存路径", 0);
            return;
        }
        //如果正在录音就取消
        if (isCountDown) {
            cancelRecord();
            return;
        }
        MAX_COUNT_DOWN_TIME = maxTime;
        mFileParentPath = path;
        mWebView = webView;
        mStartCallbackName = startCallbackName;
        mStopCallbackName = stopCallbackName;
        isFinished = false;
        isCanceled = false;
        PermissionHelper.checkPermissions(App.getInstance().getCurrentActivity(), new PermissionHelper.OnPermissionThroughActionListener() {
            @Override
            public void onThroughAction(Boolean havePermission) {
                if (havePermission) {
                    initDialogAndStartRecord();
                }
            }
        });
    }

    public void cancelRecorder() {
        if (isCanceled) {
            cancelRecord();
        } else {
            if (!isFinished) {
                finishRecord();
            }
        }
    }

    private void initDialogAndStartRecord() {
        countDownTime = MAX_COUNT_DOWN_TIME;
        AudioPlayManager.getInstance().stopPlay();
        startTime = System.currentTimeMillis();
        if (startRecording()) {
            isCountDown = true;
            mHandler.sendEmptyMessage(FLAG_LOOP);// 开始录音时就显示进度条
        } else {// 调用失败
            isFinished = true;
            stopRecording();
            ToastUtils.showMsg(getContext(), "无法录音，请检查麦克风是否可用。");
        }
    }

    private boolean startRecording() {
        if (!check_recformat()) {
            return false;
        }
        mFilePath = mFileParentPath + "/" + startTime + ".mp3";
        recorder = new MP3Recorder(new File(mFileParentPath, startTime + ".mp3"));
        LogUtil.i("record", "mFilePath=" + mFilePath);
        LogUtil.i("record", "");
        try {
            recorder.start();
            LogUtil.i("record", "开始录音");
            return true;
        } catch (Exception e) {
            LogUtil.e("record", "录音出错:" + e.toString());
            recorder.stop();
            recorder = null;
        }
        return false;
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder = null;
            }
        } catch (Exception e) {
            LogUtil.i("record", "recorder onStop Error");
        } finally {
            if (mHandler != null) {
                mHandler.removeMessages(FLAG_LOOP);
            }
            isCountDown = false;
        }
    }

    private void finishRecord() {
        try {
            stopRecording();
            long intervalTime = System.currentTimeMillis() - startTime;
            // 最小时间策略为 1000ms
            File file = new File(mFilePath);
            if (intervalTime < MIN_INTERVAL_TIME) {
                mHandler.sendEmptyMessage(MIN_INTERVAL_TIME);
                file.delete();
                return;
            }
            LogUtil.i("record", "finishRecord, Size=" + file.length());
            if (finishedListener != null) {
                int time = (int) (intervalTime < 1000 ? 1 : intervalTime / 1000);
                finishedListener.onFinishedRecord(mFilePath, time);
                callWebStop(1, "录音完成", time);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void cancelRecord() {
        stopRecording();
        ToastUtils.showMsg(getContext(), "取消录音！");
        File file = new File(mFilePath);
        file.delete();
    }

    /**
     * 检查是否OK
     * add by chenchaoyuan
     *
     * @return
     */
    public boolean check_recformat() {
        boolean bfound = false;
        for (int rec_resource : new int[]{MediaRecorder.AudioSource.VOICE_COMMUNICATION, MediaRecorder.AudioSource.CAMCORDER, MediaRecorder.AudioSource.MIC, MediaRecorder.AudioSource.DEFAULT}) {
            for (int sampleRate : new int[]{48000, 32000, 16000, 44100, 22050, 11025, 8000}) {
                //
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        int nBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
                        if (nBufSize < 0) {
                            continue;
                        }
                        AudioRecord dev = new AudioRecord(rec_resource, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, nBufSize);
                        if (dev.getState() == AudioRecord.STATE_INITIALIZED) {
                            bfound = true;
                        }
                        dev.release();
                        if (bfound) {
                            return true;
                        }
                    } catch (Exception e) {
                    }
                }//end for
            }//end for
        }//end for
        return false;
    }//end function

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    private static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void setSavePath(String path) {
        mFileParentPath = path;
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        LogUtil.e("MediaRecorder onError");
        stopRecording();
    }


    public interface OnFinishedRecordListener {
        void onFinishedRecord(String audioPath, int time);
    }

    private void callWebStop(int code, String msg, int time) {
        LogUtil.d("record stop---code:"+code+"--time:"+time);
        LogUtil.d("code:"+code+"--time:"+time);
        CallBackData<JSONObject> callBackData = new CallBackData();
        callBackData.setCode(code);
        if (msg != null) {
            callBackData.setMessage(msg);
        }
        if (time >= 0) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", time);
                callBackData.setData(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String loadUrl = "javascript:" + mStopCallbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
        mWebView.loadUrl(loadUrl);
    }

    private void callWebStart(int code, String msg, int time) {
        LogUtil.d("record start---code:"+code+"--time:"+time);
        CallBackData callBackData = new CallBackData();
        callBackData.setCode(code);
        if (msg != null) {
            callBackData.setMessage(msg);
        }
        String loadUrl = "javascript:" + mStartCallbackName + "('" + GsonUtil.toJsonString(callBackData) + "," + time + "')";
        mWebView.loadUrl(loadUrl);
    }


}

