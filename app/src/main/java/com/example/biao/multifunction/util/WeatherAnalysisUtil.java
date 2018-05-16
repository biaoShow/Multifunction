package com.example.biao.multifunction.util;

import android.text.TextUtils;
import com.example.biao.multifunction.db.WeatherDB;
import com.example.biao.multifunction.model.City;
import com.example.biao.multifunction.model.County;
import com.example.biao.multifunction.model.LaterWeather;
import com.example.biao.multifunction.model.Province;
import com.example.biao.multifunction.model.WeatherBaseData;
import com.example.biao.multifunction.model.WeatherNow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

/**
 * 解析处理通过网络返回的数据
 * Created by biao on 2018/5/15.
 */

public class WeatherAnalysisUtil {

    /**
     * 处理网络返回的省级数据并保存到数据库
     *
     * @param weatherDB 数据库读写类
     * @param response  网络返回数据
     * @return 返回是否处理成功
     */
    public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces.length > 0) {
                for (String string : allProvinces) {
                    String[] array = string.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理网络返回的市级数据并保存到数据库
     *
     * @param weatherDB  数据库读写类
     * @param response   网络返回数据
     * @param provinceId 所选省级的id
     * @return 返回是否处理成功
     */
    public static boolean handelCityResponse(WeatherDB weatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities.length > 0) {
                for (String string : allCities) {
                    String[] array = string.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理网络返回的县/区数据并保存到数据库
     *
     * @param weatherDB 数据库读写类
     * @param response  网络返回数据
     * @param cityId    所选市级级的id
     * @return 返回是否处理成功
     */
    public static boolean handelCountyRespose(WeatherDB weatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties.length > 0) {
                for (String string : allCounties) {
                    String[] array = string.split("\\|");
                    County county = new County();
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理返回实时天气数据
     * @param weatherDB 数据库读写和保存对象
     * @param response 网络返回json数据
     */
    public static void handelNowWeatherRespose(WeatherDB weatherDB, String response) {
        WeatherBaseData weatherBaseData = null;
        String nowStr = null;
        WeatherNow weatherNow = null;
        if (!TextUtils.isEmpty(response)) {
            String responseStr = response.substring(response.indexOf("["));
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(responseStr);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nowStr = jsonObject.getString("now");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (nowStr != null) {
                Gson gsonNow = new Gson();
                weatherNow = gsonNow.fromJson(nowStr, WeatherNow.class);
            }

            weatherDB.saveWeatherNow(weatherNow);
        }
    }

    /**
     * 处理返回未来天气数据
     * @param weatherDB 数据库读写和保存对象
     * @param response 网络返回json数据
     */
    public static void handelLaterWeatherRespose(WeatherDB weatherDB,String response){
        String laterStr = null;
        List<LaterWeather> laterWeatherList;
        JSONArray jsonArray = null;
        if(!TextUtils.isEmpty(response)){
            String responseStr = response.substring(response.indexOf("["));
            try {
                jsonArray = new JSONArray(responseStr);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    laterStr = jsonObject.getString("daily_forecast");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(laterStr != null){
                Gson gson = new Gson();
                laterWeatherList = gson.fromJson(laterStr,new TypeToken<List<LaterWeather>>(){}.getType());
                for(int i=0;i<laterWeatherList.size();i++){
                    weatherDB.saveWeatherLater(laterWeatherList.get(i),i);
                }
            }
        }
    }
}
