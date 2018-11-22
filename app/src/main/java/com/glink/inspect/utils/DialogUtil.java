package com.glink.inspect.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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

    public  void simpleDialogShow(Context context, String message, DialogInterface.OnClickListener listener) {
        String buttonPName = "确定";
        String buttonNName = "取消";
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT < 21) {
            builder = new AlertDialog.Builder(context);
        } else {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        builder.setMessage(message);
        if (listener != null) {
            builder.setPositiveButton(buttonPName, listener);
            builder.setNegativeButton(buttonNName,listener);
        }

        builder.show();
    }

}
