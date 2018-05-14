package com.example.biao.multifunction.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * 天气网络请求工具类
 * Created by biao on 2018/5/12.
 */

public class WeatherHttpUtils{

    private static void sendHttpRequest(final String address, final WeatehwrHttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine())!=null){
                        builder.append(line);
                    }

                    if(listener != null){
                        listener.onFinish(builder.toString());
                    }
                }  catch (Exception e) {
                    listener.onError(e);
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
