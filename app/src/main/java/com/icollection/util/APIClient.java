package com.icollection.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        OkHttpClient client;

        try {
            // Create a trust manager that does not validate certificate chains
            X509TrustManager x509TrustManager =  new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    x509TrustManager
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), new EasyX509TrustManager(null))
                    .hostnameVerifier(getHostnameVerifier())
                    .build();
        }catch (Exception e){
            client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        }




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
    // for SSL...
// Read more at https://developer.android.com/training/articles/security-ssl.html#CommonHostnameProbs
    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                //return hv.verify("localhost", session);
            }
        };
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }


    public static Retrofit getPlainClient() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("X-Api-Key", AppUtil.API_KEY).build();
                return chain.proceed(newRequest);
            }
        };
        //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        OkHttpClient client;

        try {
            // Create a trust manager that does not validate certificate chains
            X509TrustManager x509TrustManager =  new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    x509TrustManager
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), new EasyX509TrustManager(null))
                    .hostnameVerifier(getHostnameVerifier())
                    .build();
        }catch (Exception e){
            client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        }

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
