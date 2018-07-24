package com.glink.inspect.http;

import android.content.Context;

import com.glink.inspect.base.BaseResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class HttpRequest {

    public static void uploadPics(Context context, List<File> fileList, BaseObserver<BaseResponse> observer) {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("lan", HttpHelper.getTextRequestBody(HttpHelper.getLan()));
        for (File file : fileList) {
            map.put("file " + "\"; filename=\"" + file.getName() + "", HttpHelper.getFileRequestBody(file));
        }
        HttpClient.getInstance(context).getServer().uploadPersonalImages(HttpHelper.getHeaders(null), "1701032922", map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
             .subscribe(observer);
    }

}
