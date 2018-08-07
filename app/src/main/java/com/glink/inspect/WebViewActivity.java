package com.glink.inspect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.glink.R;
import com.glink.inspect.base.BaseActivity;
import com.glink.inspect.callback.WebViewInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author jiangshuyang
 */
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private WebViewInterface webViewInterface;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("url");
        initView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        webViewInterface.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void initView() {
        mWebView.setWebChromeClient(new ChromeClient(mProgressBar, null));
        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        };
        webViewInterface = new WebViewInterface(WebViewActivity.this, mWebView);
        setWebView(url, webViewInterface, webViewClient, "gl");
    }

    @SuppressLint("JavascriptInterface")
    private void setWebView(String url, Object callBack, WebViewClient webViewClient, String interfaceClassName) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(false);
        // 支持缩放，在SDK11以上，不显示缩放按钮
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        // 自适应网页宽度
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.addJavascriptInterface(callBack, interfaceClassName);
        if (null != webViewClient) {
            mWebView.setWebViewClient(webViewClient);
        }
        mWebView.loadUrl(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webViewInterface.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // destroy the WebView completely
        if (mWebView != null) {
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearCache(true);
            mWebView = null;
        }
        super.onDestroy();
    }
}
