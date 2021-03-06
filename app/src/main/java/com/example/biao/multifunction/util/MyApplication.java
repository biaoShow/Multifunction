package com.example.biao.multifunction.util;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.Window;

import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.PublicFinalModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 获取全局context
 * Created by biao on 2018/5/3.
 */

public class MyApplication extends Application {

    private static Context context;
    //线程池
    public static final ExecutorService bgTp = Executors.newCachedThreadPool();


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化SharedPreferences
        if (SharedPreferencesUtil.getIntent(this).getInt(PreferencesKep.PLAY_POSITION) == -1) {
            SharedPreferencesUtil.getIntent(this).putInt(PreferencesKep.PLAY_POSITION, 0);
        }
        if (SharedPreferencesUtil.getIntent(this).getInt(PreferencesKep.PLAY_PATTERN) == -1) {
            SharedPreferencesUtil.getIntent(this).putInt(PreferencesKep.PLAY_PATTERN, PublicFinalModel.PLAY_CIRCULATION);
        }
    }

    public static Context getContext() {
        return context;
    }
}
