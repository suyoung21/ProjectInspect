package com.glink;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.glink.base.BaseActivity;
import com.glink.utils.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.btn_zxing)
    Button zxingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editText.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    url = "file:///android_asset/test.html";
                }

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        zxingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionHelper.checkPermission(MainActivity.this, PermissionHelper.PermissionType.CAMERA, new PermissionHelper.OnPermissionThroughActionListener() {
                    @Override
                    public void onThroughAction(Boolean havePermission) {
                        if (havePermission) {
                            startActivity(new Intent(MainActivity.this, ZxingActivity.class));
                        }
                    }
                });

            }
        });
    }

}
