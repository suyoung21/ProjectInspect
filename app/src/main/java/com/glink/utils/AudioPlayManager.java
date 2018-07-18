package com.glink.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

public class AudioPlayManager {
    private final String TAG = AudioPlayManager.class.getSimpleName();
    private static AudioPlayManager audioManager;
    private MediaPlayer mPlayer;
    public String mVoicePalyingMsgId;//正在播放的语音消息id
    private OnFinishedPlayListener finishedListener;

    public void setOnFinishedRecordListener(OnFinishedPlayListener listener) {
        finishedListener = listener;
    }


    public interface OnFinishedPlayListener {
        void onFinishedPlay();
    }

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
    public void startAudio(String filePath) {
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
                finishedListener.onFinishedPlay();
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
            mPlayer.setDataSource(filePath);//语音消息存储的位置
            mPlayer.prepare();
            mPlayer.setVolume(0.5f, 0.5f);
            mPlayer.start();
            LogUtil.i(TAG, "开始播放语音");
            if (mVoicePalyingMsgId != filePath) {
                mVoicePalyingMsgId = filePath;
            }

        } catch (Exception e) {
            LogUtil.i(TAG, "MediaPlayer Error");
            e.printStackTrace();
        }
    }

    /**
     * 停止语音播放
     */
    public void stopPlay() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }


}
