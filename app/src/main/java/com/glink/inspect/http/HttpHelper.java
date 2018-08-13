package com.glink.inspect.http;

import android.text.TextUtils;
import android.util.Base64;

import com.glink.inspect.App;
import com.glink.inspect.base.BaseResponse;
import com.glink.inspect.utils.CommonUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by jiangshuyang on 17/11/23/023.
 */

public class HttpHelper {

    public final static String CONTENT_TYPE_MAP = "application/x-www-form-urlencoded;charset=UTF-8";
    public final static String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    public final static String CONTENT_TYPE_MULTIPART = "multipart/form-data;charset=UTF-8";
    public final static String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

    /**
     * 获取语言
     */
    public static String getLan() {
        return "zh_CN";
    }

    /**
     * 获取IP地址
     */
    public static String getIP() {
        return CommonUtil.getLocalIp();
    }

    /**
     * 获取必要的header
     */
    public static Map<String, String> getHeaders(String contentType) {
        Map<String, String> map = new HashMap<>();
        map.put("DevNo", CommonUtil.getDeviceNum(App.getAppContext()));
        map.put("ClientIP", getIP());
        map.put("Accept-Language", getLan());
        if (!TextUtils.isEmpty(contentType))
            map.put("Content-Type", contentType);
        return map;
    }

    /**
     * 转换，获取Response中的data
     */
    public static class HttpResultFunc<T> implements Function<BaseResponse<T>, T> {

        @Override
        public T apply(BaseResponse<T> baseResponse) throws Exception {
            if (!baseResponse.getCode().equals("100000") && !baseResponse.getCode().equals("0")) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("code", baseResponse.getCode());
                    jsonObject.put("message", baseResponse.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new RuntimeException(jsonObject.toString());
            }
            return baseResponse.getData();
        }
    }


    /**
     * 游戏圈请求，数组参数转换，加逗号
     */
    public static String arrayToString(Object[] arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            stringBuilder.append(arr[i]);
            if (arr.length - 1 != i)
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    /**
     * 游戏圈请求，数组参数转换，加逗号
     */
    public static String arrayToString(List list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (list.size() - 1 != i)
                stringBuilder.append(",");
        }
//        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        return stringBuilder.toString();
    }

    /**
     * 获取表单格式的RequestBody
     */
    public static RequestBody getDefaultRequestBody(String content) {
        return RequestBody.create(MediaType.parse(CONTENT_TYPE_MAP), content);
    }

    /**
     * 获取JSON格式的RequestBody
     */
    public static RequestBody getJsonRequestBody(JSONObject jsonObject) {
        return RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString());
    }

    /**
     * 获取JSON格式的RequestBody
     */
    public static RequestBody getJsonRequestBody(Map<String, String> map) {
        Gson gson = new Gson();
        gson.toJson(map);
        return RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), gson.toString());
    }

    /**
     * 获取Text格式的RequestBody
     */
    public static RequestBody getTextRequestBody(String content) {
        return RequestBody.create(MediaType.parse(CONTENT_TYPE_TEXT), content);
    }

    /**
     * 获取file格式的RequestBody
     */
    public static RequestBody getFileRequestBody(File file) {
        return RequestBody.create(MediaType.parse(CONTENT_TYPE_MULTIPART), file);
    }

    /**
     * 获取file格式的RequestBody
     */
    public static RequestBody getFileRequestBody(File file, boolean isNeedBase64) {
        if (isNeedBase64) {
            try {
                return RequestBody.create(MediaType.parse(CONTENT_TYPE_MULTIPART), encodeBase64File(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getFileRequestBody(file);

    }

    public static String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

}
