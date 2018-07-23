package com.glink.inspect.utils;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebView;

import com.glink.inspect.data.CallBackData;

public class AudioPlayManager {
    private final String TAG = AudioPlayManager.class.getSimpleName();
    //最大计时时间
    private int MAX_COUNT_DOWN_TIME = 60;
    private static final int MIN_INTERVAL_TIME = 1000;// 1000ms
    private static AudioPlayManager audioManager;
    private MediaPlayer mPlayer;
    private static final int IS_ALMOST_REACH_MAX_TIME = 1;
    private static final int FLAG_LOOP = 3;//倒计时
    private int countDownTime;
    private boolean isCountDown = false;
    private boolean isFinished = false;
    private String mStartCallbackName;
    private WebView mWebView;
    private Activity mActivity;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case IS_ALMOST_REACH_MAX_TIME:
                    if (!isFinished) {
                        isFinished = true;
//                        callWebStart(1, "录音播放结束", (MAX_COUNT_DOWN_TIME - countDownTime));
                        stopPlay();
                    }
                    break;
                case FLAG_LOOP:
                    callWebStart(1, "录音播放中...", (MAX_COUNT_DOWN_TIME - countDownTime));
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

    private AudioPlayManager() {

    }

    public static AudioPlayManager getInstance() {
        if (null == audioManager) {
            synchronized (AudioPlayManager.class) {
                if (null == audioManager) {
                    audioManager = new AudioPlayManager();
                }
            }
        }
        return audioManager;
    }


    /**
     * 开始播放
     */
    public void startAudio(Activity activity, String filePath, int maxTime, WebView webView, String callbackName) {
        mActivity = activity;
        mWebView = webView;
        mStartCallbackName = callbackName;
        MAX_COUNT_DOWN_TIME = maxTime;
        countDownTime = maxTime;
        isFinished = false;
        if (null != mPlayer && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtil.i(TAG, "语音播放完了");
                mPlayer.release();
                mPlayer = null;
                mHandler.sendEmptyMessage(IS_ALMOST_REACH_MAX_TIME);
            }
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                builder.setUsage(AudioAttributes.USAGE_MEDIA);
                mPlayer.setAudioAttributes(builder.build());
            } else {
                mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
            //语音消息存储的位置
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.setVolume(0.5f, 0.5f);
            mPlayer.start();
            LogUtil.i(TAG, "开始播放语音");
            isCountDown = true;
            callWebStart(1, "开始播放，录音播放中...", 0);
            mHandler.sendEmptyMessage(FLAG_LOOP);
        } catch (Exception e) {
            isFinished = true;
            isCountDown = false;
            if (mHandler != null) {
                mHandler.removeMessages(FLAG_LOOP);
            }
            LogUtil.i(TAG, "MediaPlayer Error");
            e.printStackTrace();
        }
    }

    /**
     * 停止语音播放
     */
    public void stopPlay() {
        isFinished = true;

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (mHandler != null) {
            mHandler.removeMessages(FLAG_LOOP);
        }
    }

    /**
     * 停止语音播放
     */
    public void stopPlay(Activity activity, WebView webView, String callbackName) {
        mActivity = activity;
        mWebView = webView;
        mStartCallbackName = callbackName;
        stopPlay();
        callWebStop(1, "停止播放录音", callbackName);
    }

    private void callWebStart(final int code, final String msg, final int time) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("play start---code:" + code + "--time:" + time);
                CallBackData callBackData = new CallBackData();
                callBackData.setCode(code);
                if (msg != null) {
                    callBackData.setMessage(msg);
                }
                String loadUrl = "javascript:" + mStartCallbackName + "('" + GsonUtil.toJsonString(callBackData) + "'," + time + ")";
                if (time == 0) {
                    loadUrl = "javascript:" + mStartCallbackName + "('" + GsonUtil.toJsonString(callBackData) + "')";
                }
                LogUtil.d("call js: " + loadUrl);
                mWebView.loadUrl(loadUrl);
            }
        });

    }

    private void callWebStop(final int code, final String msg, final String mStopCallBack) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("play stop---code:" + code);
                CallBackData callBackData = new CallBackData();
                callBackData.setCode(code);
                if (msg != null) {
                    callBackData.setMessage(msg);
                }
                String loadUrl = "javascript:" + mStopCallBack + "('" + GsonUtil.toJsonString(callBackData) + "')";

                LogUtil.d("call js: " + loadUrl);
                mWebView.loadUrl(loadUrl);
            }
        });

    }
}
