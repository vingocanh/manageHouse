package com.example.managehouse.Retrofit;

import com.example.managehouse.Common.Common;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit instance = null;

    public static Retrofit getInstance() {
        if(instance == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if(Common.token.equals("login")) {
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type","application/json")
                                .addHeader("X-Requested-With", "XMLHttpRequest")
                                .build();
                        return chain.proceed(request);
                    }
                });
            }
            else {

                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type","application/json")
                                .addHeader("Authorization", Common.token)
                                .build();
                        return chain.proceed(request);
                    }
                });
            }

            instance = new Retrofit.Builder().baseUrl("http://192.168.1.53/house/public/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(builder.build())
                    .build();
        }
        return instance;
    }

}
