package com.glink.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.glink.App;
import com.glink.utils.PermissionHelper;
import com.glink.App;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author jiangshuyang
 */
public class BaseActivity extends AppCompatActivity {

    protected BaseActivity context;
    private WeakReference<BaseActivity> weakContext;
    final ReferenceQueue<BaseActivity> weakContextQueue = new ReferenceQueue<BaseActivity>();
    private boolean isFinished;
    protected WeakHandler weakHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        App.getInstance().createActivity(this);
        weakHandler = new WeakHandler(this);
    }

    @Override
    protected void onDestroy() {
        App.getInstance().destroyActivity(context);
        isFinished = true;
        releaseContext();
        if (weakHandler != null) {
            weakHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public BaseActivity getContext() {
        if (weakContext != null && weakContext.get() != null) {
            return weakContext.get();
        }
        weakContext = new WeakReference<BaseActivity>(context);
        return weakContext.get();
    }

    private void releaseContext() {
        if (weakContext != null) {
            weakContext.clear();
            weakContext.enqueue();
            weakContext = null;
        }
        if (weakContextQueue != null) {
            weakContextQueue.poll();
        }
        context = null;
        System.gc();
    }

    protected static class WeakHandler extends Handler {

        WeakReference<BaseActivity> mReference = null;

        WeakHandler(BaseActivity activity) {
            this.mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mReference.get();
            if (activity == null || activity.isFinishing() || activity.isFinished()) {
                return;
            }

            activity.handleMessage(msg);
        }
    }

    /**
     * WeakHandler handleMessage
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }

    public boolean isFinished() {
        return isFinished;
    }
}
