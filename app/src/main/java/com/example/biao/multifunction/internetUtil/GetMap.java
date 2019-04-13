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
}
