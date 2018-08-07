package com.glink.inspect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.glink.R;
import com.glink.inspect.base.BaseActivity;
import com.glink.inspect.bus.BusProvider;
import com.glink.inspect.bus.FinishZxingEvent;
import com.glink.inspect.data.ZxingData;
import com.google.zxing.Result;
import com.squareup.otto.Subscribe;

import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZxingActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private static final String CALLBACK_NAME = "ZXING_CALLBACK_NAME";
    private static final String FLASH_STATE = "FLASH_STATE";
    private boolean mFlash;
    private ZXingScannerView mScannerView;
    private String callbackName;


    public static Intent newIntent(Activity fromActivity, String callbackName) {
        Intent intent = new Intent(fromActivity, ZxingActivity.class);
        intent.putExtra(CALLBACK_NAME, callbackName);
        return intent;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_zxing);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getIntent() != null) {
            callbackName = getIntent().getStringExtra(CALLBACK_NAME);
        }
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
        }
//        mScannerView = new ZXingScannerView(this) {
//            @Override
//            protected IViewFinder createViewFinderView(Context context) {
//                return new CustomViewFinderView(context);
//            }
//        };
        mScannerView = new ZXingScannerView(this);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);
        findViewById(R.id.light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlash();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size
        //mScannerView.setAspectTolerance(0.2f);

        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void toggleFlash() {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(getApplicationContext(), "内容=" + result.getText() + ",格式=" + result.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        BusProvider.getInstance().post(new ZxingData(result.getText(), result.getBarcodeFormat(), callbackName));
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ZxingActivity.this);
            }
        }, 2000);

    }

    @Subscribe
    public void finishSelf(FinishZxingEvent event) {
        finish();
    }

    private static class CustomViewFinderView extends ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
//            setSquareViewFinder(true);
//            setBorderColor(R.color.gl_blue);
//            setBorderStrokeWidth(5);
            setLaserEnabled(true);
        }
    }
}
