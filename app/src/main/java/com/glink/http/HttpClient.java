package com.glink.http;

import android.content.Context;

import com.glink.utils.LogUtil;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private Context mContext;
    private OkHttpClient client;
    private GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static HttpClient instance = null;
    private Retrofit mRetrofit = null;

    public static HttpClient getInstance(Context context) {
        if (instance == null) {
            instance = new HttpClient(context);
        }
        return instance;
    }

    private HttpClient(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private void init() {
        client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        resetApp();
    }

    private void resetApp() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://10.206.2.193:85/")
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public HttpApi getServer() {
        return mRetrofit.create(HttpApi.class);
    }

    public class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();

            //请求发起的时间
            long t1 = System.nanoTime();
            LogUtil.d(String.format("发送请求 %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            //收到响应的时间
            long t2 = System.nanoTime();

            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);

            LogUtil.d(String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    (t2 - t1) / 1e6d,
                    response.headers()));

            return response;
        }
    }
}
