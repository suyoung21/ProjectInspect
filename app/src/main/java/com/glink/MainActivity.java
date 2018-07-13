package com.glink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;

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
    }

    private static final int REQUEST_CODE = 0x00000011;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            //
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_single:
//                //单选
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, true, 0);
//                ImageSelector.builder()
//                        .useCamera(true) // 设置是否使用拍照
//                        .setSingle(true)  //设置是否单选
//                        .start(this, REQUEST_CODE); // 打开相册
//                break;
//
//            case R.id.btn_limit:
//                //多选(最多9张)
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9);
////                ImageSelector.builder().setSingle(true).start(this,REQUEST_CODE);
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9, mAdapter.getImages()); // 把已选的传入。
//                ImageSelector.builder()
//                        .useCamera(true) // 设置是否使用拍照
//                        .setSingle(false)  //设置是否单选
//                        .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
//                        .start(this, REQUEST_CODE); // 打开相册
//                break;
//
//            case R.id.btn_unlimited:
//                //多选(不限数量)
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE);
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, mAdapter.getImages()); // 把已选的传入。
//                //或者
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 0);
////                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 0, mAdapter.getImages()); // 把已选的传入。
//
//                ImageSelector.builder()
//                        .useCamera(true) // 设置是否使用拍照
//                        .setSingle(false)  //设置是否单选
//                        .setMaxSelectCount(0) // 图片的最大选择数量，小于等于0时，不限数量。
//                        .start(this, REQUEST_CODE); // 打开相册
//                break;
//
//            case R.id.btn_clip:
//                //单选并剪裁
////                ImageSelectorUtils.openPhotoAndClip(MainActivity.this, REQUEST_CODE);
//                ImageSelector.builder()
//                        .useCamera(true) // 设置是否使用拍照
//                        .setCrop(true)  // 设置是否使用图片剪切功能。
//                        .setSingle(true)  //设置是否单选
//                        .start(this, REQUEST_CODE); // 打开相册
//                break;
//        }
//    }
}
