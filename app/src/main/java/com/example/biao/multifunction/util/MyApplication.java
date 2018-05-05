package com.example.biao.multifunction.util;

import android.app.Application;
import android.content.Context;

/**
 * 获取全局context
 * Created by biao on 2018/5/3.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
