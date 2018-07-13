package com.glink.callback;

import android.app.Activity;
import android.webkit.WebView;

/**
 * @author jiangshuyang
 */
public class WebViewInterface {

    private Activity activity;
    private WebView webView;

    public WebViewInterface(Activity activity, WebView webView) {

        this.activity = activity;
        this.webView = webView;
    }
}
