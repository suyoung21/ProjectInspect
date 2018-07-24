package com.glink.inspect.http;

import com.glink.inspect.base.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface HttpApi {

    @Multipart
    @POST("ImageService/gt/avatar/upload/{aid}")
    Observable<BaseResponse> uploadAvatar(@HeaderMap Map<String, String> headers, @Path("aid") String aid, @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("ImageService/gt/photo/upload/{aid}")
    Observable<BaseResponse> uploadPersonalImages(@HeaderMap Map<String, String> headers, @Path("aid") String aid, @PartMap Map<String, RequestBody> params);

}
