package com.example.biao.multifunction.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences简单封装
 * Created by benxiang on 2019/3/30.
 */

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil preferencesUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("memory", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public synchronized static SharedPreferencesUtil getIntent(Context context) {
        if (preferencesUtil == null) {
            preferencesUtil = new SharedPreferencesUtil(context);
        }
        return preferencesUtil;
    }

    public void putString(String key, String content) {
        editor.putString(key, content);
        editor.commit();
    }

    public void putInt(String key, int content) {
        editor.putInt(key, content);
        editor.commit();
    }

    public void putBoolean(String key, boolean content) {
        editor.putBoolean(key, content);
        editor.commit();
    }

    public void putFloat(String key, float content) {
        editor.putFloat(key, content);
        editor.commit();
    }

    public void putLong(String key, long content) {
        editor.putLong(key, content);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, -1);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

}
