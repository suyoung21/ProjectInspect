package com.glink.inspect;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.glink.R;
import com.glink.inspect.base.BaseResponse;
import com.glink.inspect.callback.WebViewInterface;
import com.glink.inspect.data.CallBackData;
import com.glink.inspect.data.ZxingData;
import com.glink.inspect.http.BaseObserver;
import com.glink.inspect.http.HttpRequest;
import com.glink.inspect.utils.CommonUtil;
import com.glink.inspect.utils.DialogUtil;
import com.glink.inspect.utils.GsonUtil;
import com.glink.inspect.utils.KeyBoardListener;
import com.glink.inspect.utils.LogUtil;
import com.glink.inspect.utils.ToastUtils;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

/**
 * @author jiangshuyang
 */
public class WebViewActivity extends UrlBaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private WebViewInterface webViewInterface;
    private boolean isRestart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
//        url = getIntent().getStringExtra("url");
        initView();
        KeyBoardListener.getInstance(this).init();
//        PermissionHelper.checkPermission(WebViewActivity.this, PermissionHelper.PermissionType.WRITE_EXTERNAL_STORAGE, new PermissionHelper.OnPermissionThroughActionListener() {
//            @Override
//            public void onThroughAction(Boolean havePermission) {
//                if (havePermission) {
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUrl();
        if (TextUtils.isEmpty(getRequestIP())) {
            if (isRestart) {
                DialogUtil.getInstance().simpleDialogShow(this, "页面地址还未设置，是否跳转设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case -1:
                                startActivity(SettingActivity.newIntent(WebViewActivity.this));
                                break;
                            case -2:
                                WebViewActivity.this.finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
            } else {
                startActivity(SettingActivity.newIntent(WebViewActivity.this));
            }
        } else {
            if (isRestart) {
                url = CommonUtil.parseUploadUrl(getRequestIP(), getRequestPort());
                mWebView.loadUrl(url);
            } else {
                HttpRequest.verifyUrl(this, getRequestIP(), getRequestPort(), new BaseObserver<BaseResponse>(this) {
                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        super.onNext(baseResponse);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        url = CommonUtil.parseUploadUrl(getRequestIP(), getRequestPort());
                        mWebView.loadUrl(url);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (e instanceof HttpException) {
                            //HTTP错误
                            HttpException httpException = (HttpException) e;
                            ToastUtils.showMsg(getContext(), "Http Error:" + httpException.code());
                        }
                        startActivity(SettingActivity.newIntent(WebViewActivity.this));
                    }
                });
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestart = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webViewInterface != null) {
            webViewInterface.onPause();
        }
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
        // 设置4.2以后版本支持autoPlay，非用户手势促发
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        //开启硬件加速
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.addJavascriptInterface(callBack, interfaceClassName);
        if (null != webViewClient) {
            mWebView.setWebViewClient(webViewClient);
        }
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
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

    @Subscribe
    public void getZxingResult(ZxingData zxingData) {
        if (zxingData == null || TextUtils.isEmpty(zxingData.getCallbackName())) {
            return;
        }
        CallBackData<String> callBackData = new CallBackData();
        if (TextUtils.isEmpty(zxingData.getResult())) {
            callBackData.setCode(0);
            callBackData.setMessage("扫码失败");
        } else {
            callBackData.setCode(1);
            callBackData.setMessage("扫码成功");
            callBackData.setData(zxingData.getResult());
        }

        String loadUrl = "javascript:" + zxingData.getCallbackName() + "('" + GsonUtil.toJsonString(callBackData) + "','" + zxingData.getCodeType().toString() + "')";

        LogUtil.d("call js: " + loadUrl);
        mWebView.loadUrl(loadUrl);
    }
}
