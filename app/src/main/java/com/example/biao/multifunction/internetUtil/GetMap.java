package com.example.biao.multifunction.internetUtil;

import android.util.ArrayMap;

import com.example.biao.multifunction.model.PublicFinalModel;

import java.util.Map;

/**
 * Created by benxiang on 2019/4/13.
 */

public class GetMap {

    public static Map<String, String> getNowWeatherMap(String cityCode) {
        Map<String, String> map = new ArrayMap<>();
        map.put("key", PublicFinalModel.WEATHER_KEY);
        map.put("location", cityCode);
        return map;
    }

    public static Map<String, String> getFutureWeatherMap(String cityCode) {
        Map<String, String> map = new ArrayMap<>();
        map.put("key", PublicFinalModel.WEATHER_KEY);
        map.put("location", cityCode);
        return map;
    }

    public static Map<String, String> getMusciDetails(String from, String method, String format, String songName) {
        Map<String, String> map = new ArrayMap<>();
        map.put("from", from);
        map.put("method", method);
        map.put("format", format);
        map.put("query", songName);
        return map;
    }

    public static Map<String, String> getLyrics(String from, String method, String format, String songId) {
        Map<String, String> map = new ArrayMap<>();
        map.put("from", from);
        map.put("method", method);
        map.put("format", format);
        map.put("songid", songId);
        return map;
    }
}
