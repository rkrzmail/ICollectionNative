package com.icollection.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by user on 1/10/2018.
 */

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("X-Api-Key", AppUtil.API_KEY).build();
                return chain.proceed(newRequest);
            }
        };


       // HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        gb.serializeNulls();
        Gson gson = gb.create();

//192.168.43.171
        //192.168.1.2
        retrofit = new Retrofit.Builder()
                .baseUrl(AppUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))

                .client(client)
                .build();



        return retrofit;
    }

    public static Retrofit getPlainClient() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("X-Api-Key", AppUtil.API_KEY).build();
                return chain.proceed(newRequest);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppUtil.BASE_URL)
                .addConverterFactory(new ToStringConverterFactory())

                .client(client)
                .build();



        return retrofit;
    }

    public static class ToStringConverterFactory extends Converter.Factory {
        private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");


        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            if (String.class.equals(type)) {
                return new Converter<ResponseBody, String>() {
                    @Override
                    public String convert(ResponseBody value) throws IOException
                    {
                        return value.string();
                    }
                };
            }
            return null;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                              Annotation[] methodAnnotations, Retrofit retrofit) {

            if (String.class.equals(type)) {
                return new Converter<String, RequestBody>() {
                    @Override
                    public RequestBody convert(String value) throws IOException {
                        return RequestBody.create(MEDIA_TYPE, value);
                    }
                };
            }
            return null;
        }
    }
}
