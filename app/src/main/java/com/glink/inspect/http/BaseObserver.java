package com.glink.inspect.http;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.glink.inspect.utils.DialogUtil;
import com.glink.inspect.utils.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BaseObserver<T> implements Observer<T> {

    private Context mContext;
    private Dialog progressDialog;

    public BaseObserver(Context context) {
        mContext = context;
    }

    private Activity getActivity() {
        return (Activity) mContext;
    }

    @Override

    public void onSubscribe(Disposable d) {
        showProgress();
    }

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
        hideProgress();
        ToastUtils.showMsg(mContext, "http error");
    }

    @Override
    public void onComplete() {
        hideProgress();

    }

//    public abstract void onSuccess(T t);
//
//    public abstract void onFail(T t);


    public void showProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = DialogUtil.getInstance().loadingDialog(getActivity(), "");
                }
                if (!progressDialog.isShowing()) {
//                    progressDialog.setMessage("Server connection...");
                    progressDialog.show();
                }
            }
        });

    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
