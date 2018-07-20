package com.glink.inspect;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author jiangshuyang
 */
public class ChromeClient extends WebChromeClient {
    ProgressBar mProgressBar;

    TextView titleView;
    boolean isFirst = true;
    public ChromeClient(ProgressBar ProgressBar, TextView titleView) {
        this.mProgressBar = ProgressBar;
        this.titleView = titleView;
    }
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress < 100 && mProgressBar.getVisibility() == ProgressBar.GONE) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
        mProgressBar.setProgress(newProgress);
        if (newProgress == mProgressBar.getMax()) {
            mProgressBar.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (titleView != null) {
            if (isFirst && !TextUtils.isEmpty(titleView.getText().toString())) {
                isFirst = false;
                return;
            }
            titleView.setText(title);
        }
    }

//    @Override
//    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
////        super.onJsAlert(view, url, message, result)
//        CustomDialog.getInstance().createSimpleDialog(view.getContext(), message,
//                ResUtil.getString(R.string.confirm),
//                () -> {
//
//                }).show();
//        result.cancel();
//        return true;
//    }
}
