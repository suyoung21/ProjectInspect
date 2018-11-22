package com.glink.inspect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.MenuItem;

import com.glink.R;
import com.glink.inspect.base.BaseResponse;
import com.glink.inspect.http.BaseObserver;
import com.glink.inspect.http.HttpRequest;
import com.glink.inspect.utils.CommonUtil;
import com.glink.inspect.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.HttpException;

/**
 * @author jiangshuyang
 */
public class SettingActivity extends UrlBaseActivity {

    @BindView(R.id.et_ip)
    TextInputEditText ipEt;
    @BindView(R.id.til_ip)
    TextInputLayout ipLayout;
    @BindView(R.id.et_port)
    TextInputEditText portEt;
    @BindView(R.id.til_port)
    TextInputLayout portLayout;
    @BindView(R.id.btn_confirm)
    AppCompatButton confirmBtn;

    public static Intent newIntent(Activity fromActivity) {
        Intent intent = new Intent(fromActivity, SettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        if (!TextUtils.isEmpty(getRequestIP())) {
            ipEt.setText(getRequestIP());
        }
        if (!TextUtils.isEmpty(getRequestPort())) {
            portEt.setText(getRequestPort());
        }
        ipEt.setSelection(ipEt.getText().toString().length());
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        String ipStr = ipEt.getText().toString().trim();
        String portStr = portEt.getText().toString().trim();
        if (TextUtils.isEmpty(ipStr)) {
            ipLayout.setError("域名或IP地址不能为空");
            return;
        }
        String url = CommonUtil.getPingUrl(ipStr, portStr);
        if (url != null) {
            verifyUrl(ipStr, portStr);
        } else {
            ipLayout.setError("请填写正确格式的域名或ip，端口号");
            ipLayout.getEditText().setFocusable(true);
            ipLayout.getEditText().setFocusableInTouchMode(true);
            ipLayout.getEditText().requestFocus();
        }
    }


    private void verifyUrl(String ip, String port) {
        HttpRequest.verifyUrl(this, ip, port, new BaseObserver<BaseResponse>(this) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                super.onNext(baseResponse);
                setDataCache(ip, port);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof HttpException) {
                    //HTTP错误
                    HttpException httpException = (HttpException) e;
                    ToastUtils.showMsg(getContext(), "Http Error:" + httpException.code());
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
