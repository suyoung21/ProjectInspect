package com.glink.utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.glink.R;

public class DialogUtil {

    public static DialogUtil sCustomDialog = new DialogUtil();

    private DialogUtil() {
    }

    public static DialogUtil getInstance() {
        return sCustomDialog;
    }
    /**
     * 全局loading弹框
     */
    public Dialog loadingDialog(Activity activity, String loadingTxt) {
        final Dialog dialog = new Dialog(activity, R.style.custom_dialog);
        dialog.setContentView(R.layout.dialog_custom_progress);
        dialog.setCanceledOnTouchOutside(false);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = metrics.widthPixels;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView textView = dialog.findViewById(R.id.tv_load_dialog);
        textView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(loadingTxt)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(loadingTxt);
        } else {
            textView.setVisibility(View.GONE);
        }
        return dialog;
    }
}
