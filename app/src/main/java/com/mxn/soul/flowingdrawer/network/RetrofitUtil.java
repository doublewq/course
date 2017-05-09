package com.mxn.soul.flowingdrawer.network;

import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by weijie.liu on 16/04/2017.
 */

public class RetrofitUtil {

    private static final String mBaseUrl = "http://api.github.com";
    private Map<Class, Object> cachedService = new HashMap<>();

    private Retrofit retrofit;

    private static volatile RetrofitUtil instance;

    public RetrofitUtil(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public static void init() {
        initWithRetrofit(null);
    }

    public synchronized static void initWithRetrofit(Retrofit retrofit) {
        if (instance != null) {
            throw new IllegalStateException("already initialized RetrofitManager");
        }
        if (retrofit == null) {
            try {
                retrofit = createDefaultRetrofit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance = new RetrofitUtil(retrofit);
    }

    public static RetrofitUtil getInstance() {
        return instance;
    }

    private static Retrofit createDefaultRetrofit() throws Exception {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkhttp())
                .baseUrl(mBaseUrl) //just place holder
                .build();
    }

    private static OkHttpClient getOkhttp() throws Exception {

        return new OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory()).build();

    }

    private static SSLSocketFactory getSSLSocketFactory() throws Exception {
        //创建一个不验证证书链的证书信任管理器。
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts,
                new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        return sslContext
                .getSocketFactory();
    }

    public synchronized <T> T getService(Class<T> clazz) {
        T service = (T) cachedService.get(clazz);
        if (service == null) {
            service = retrofit.create(clazz);
            cachedService.put(clazz, service);
        }
        return service;
    }

    public interface ApiCallback {
        void onSuccess(BaseRsp ret);        //ret=1时返回

        void onError(int err_code, String err_msg);   //ret=0时返回

        void onFailure();   //网络请求失败
    }
}
