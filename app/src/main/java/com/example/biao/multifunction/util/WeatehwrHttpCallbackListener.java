package com.example.biao.multifunction.util;

/**
 * 天气网络访问监听接口
 * Created by biao on 2018/5/12.
 */

public interface WeatehwrHttpCallbackListener {
    //成功执行
    void onFinish(String response);
    //失败执行
    void onError(Exception e);
}
