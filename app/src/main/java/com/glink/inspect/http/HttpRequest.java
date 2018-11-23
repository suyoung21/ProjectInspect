package com.glink.inspect.http;

import android.content.Context;
import android.text.TextUtils;

import com.glink.inspect.base.BaseResponse;
import com.glink.inspect.data.CommonData;
import com.glink.inspect.utils.CommonUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * @author jiangshuyang
 */
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

    public static void uploadFile(Context context, String url, List<CommonData> commonDataList, int fileType, List<File> fileList, BaseObserver<BaseResponse> observer) {
        Map<String, RequestBody> map = new HashMap<>(fileList.size() + commonDataList.size());
        map.put("type", HttpHelper.getTextRequestBody("" + fileType));
        for (CommonData commonData : commonDataList) {
            if (!("uploadUrl").equals(commonData.getKey()) && !("type").equals(commonData.getKey())) {
                map.put(commonData.getKey(), HttpHelper.getTextRequestBody(commonData.getValue()));
            }
        }
//        map.put("orderId", HttpHelper.getTextRequestBody(paramData.getOrderId()));
//        map.put("tunnelDevId", HttpHelper.getTextRequestBody(paramData.getTunnelDevId()));
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            if (i == 0) {
                map.put("uploadFile" + "\"; filename=\"" + file.getName() + "", HttpHelper.getFileRequestBody(file));
            } else {
                map.put("file" + (i + 1) + "\"; filename=\"" + file.getName() + "", HttpHelper.getFileRequestBody(file));
            }

        }
        HttpClient.getInstance(context).getServer().uploadFile(url, HttpHelper.getHeaders(null), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void verifyUrl(Context context, String ip, String port, BaseObserver<BaseResponse> observer) {
        String fullUrl= CommonUtil.getPingUrl(ip,port);
        HttpClient.getInstance(context).getServer().verifyUrl(fullUrl, HttpHelper.getHeaders(null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
