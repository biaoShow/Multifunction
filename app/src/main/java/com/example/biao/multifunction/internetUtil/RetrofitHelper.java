package com.example.biao.multifunction.internetUtil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.biao.multifunction.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by benxiang on 2019/4/10.
 */

public class RetrofitHelper {
    private static RetrofitHelper retrofitHelper = null;
    private InternetInterface internetInterface = null;
    private static String rh_baseUrl = "";
    private Retrofit retrofit = null;
    private static final int CONNECT_TIME_OUT = 12;
    private static final int READ_TIME_OUT = 5;

    private RetrofitHelper(String baseUrl) {
        if (null == retrofit) {
            retrofit = new Retrofit.Builder()
                    .client(setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(rh_baseUrl)
                    .build();
        }
    }

    /**
     * 获取网络RetrofitHelper类对象
     *
     * @param baseUrl
     * @return
     */
    public static RetrofitHelper getInstance(String baseUrl) {
        if (!rh_baseUrl.equals(baseUrl)) {
            if (null == baseUrl || "".equals(baseUrl)) {
                rh_baseUrl = BuildConfig.BASE_URL;
            } else {
                rh_baseUrl = baseUrl;
            }
            if (retrofitHelper != null) {
                retrofitHelper = null;
            }
        }
        if (null == retrofitHelper) {
            retrofitHelper = new RetrofitHelper(baseUrl);
        }
        return retrofitHelper;
    }

    /**
     * 获取网络接口对象
     *
     * @return
     */
    public InternetInterface getInternetInterface() {
        if (null == internetInterface) {
            internetInterface = retrofit.create(InternetInterface.class);
        }
        return internetInterface;
    }

    /**
     * 设置超时和打印日志
     *
     * @return
     */
    private OkHttpClient setTimeOut() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        okHttpClient.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                        .build();
                return chain.proceed(request);
            }
        });
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new LogHelper());
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient.addNetworkInterceptor(loggingInterceptor);
        }
        okHttpClient.retryOnConnectionFailure(true);
        return okHttpClient.build();
    }

    class LogHelper implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            Log.i("HttpLogInfo", message);
        }
    }
}
