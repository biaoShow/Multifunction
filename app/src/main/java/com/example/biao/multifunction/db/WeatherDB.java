package com.example.biao.multifunction.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.biao.multifunction.model.City;
import com.example.biao.multifunction.model.County;
import com.example.biao.multifunction.model.LaterWeather;
import com.example.biao.multifunction.model.Province;
import com.example.biao.multifunction.model.WeatherNow;
import com.example.biao.multifunction.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建数据库并封装读取写入方法
 * Created by biao on 2018/5/15.
 */

public class WeatherDB {
    private static final String DB_NAME = "weather_db";//数据库名称

    private SQLiteDatabase db;
    private static WeatherDB weatherDB;

    private WeatherDB(Context context){
        WeatherOpenHelper weatherOpenHelper = new WeatherOpenHelper(MyApplication.getContext(),
                DB_NAME,null,1);
        db = weatherOpenHelper.getWritableDatabase();
    }

    /**
     * 提供方法返回一个当前类对象
     * @param context 调用上下文
     * @return 返回当前类对象
     */
    public synchronized static WeatherDB getInstance(Context context){
        if(weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 保存Province实例到数据库
     * @param province Province实例对象
     */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 保存City实例对象到数据库
     * @param city City实例对象
     */
    public void saveCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 保存County实例对象到数据库
     * @param county County实例对象
     */
    public void saveCounty(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }

    /**
     * 读取数据库省份数据
     * @return 返回读取省级结果
     */
    public List<Province> loadProvince(){
        List<Province> provinces = new ArrayList<>();
        Cursor cursor = db.query("Province",null,
                null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return provinces;
    }

    /**
     * 读取数据库市级数据
     * @return 返回数据库市级结果
     */
    public List<City> loadCity(String provinceId){
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.query("City",null,"province_id=?",
                new String[]{provinceId},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                cities.add(city);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return cities;
    }

    /**
     * 读取数据库县/区数据
     * @return 返回数据库县/区数据
     */
    public List<County> loadCounty(String cityId){
        List<County> counties = new ArrayList<>();
        Cursor cursor = db.query("County",null,"city_id=?",
                new String[]{cityId},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                counties.add(county);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return counties;
    }

    /**
     * 保存实时天气到SharedPreferences
     * @param weatherNow 网络返回json数据对象
     */
    public void saveWeatherNow(WeatherNow weatherNow){
        if(weatherNow != null){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
            editor.putString("tmp",weatherNow.getTmp());//当前温度
            editor.putString("cond_txt",weatherNow.getCond_txt());//实况天气
            editor.putString("wind_dir",weatherNow.getWind_dir());//风向
            editor.putString("wind_sc",weatherNow.getWind_sc());//风力
            editor.putString("hum",weatherNow.getHum());//相对湿度
            editor.putString("vis",weatherNow.getVis());//可见度
            editor.apply();
        }else{
            Log.i("WeatherDB","网络返回实时天气为空");
        }
    }

    /**
     * 保存未来天气到SharedPreferences
     * @param laterWeather 天气对象
     * @param i 未来天数
     */
    public void saveWeatherLater(LaterWeather laterWeather,int i){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        if(i == 0){
            editor.putString("tcond_txt_d",laterWeather.getCond_txt_d());
            editor.putString("twind_sc",laterWeather.getWind_sc());
            editor.putString("ttmp_min",laterWeather.getTmp_min());
            editor.putString("ttmp_max",laterWeather.getTmp_max());
            editor.apply();
        }else if(i == 1){
            editor.putString("trcond_txt_d",laterWeather.getCond_txt_d());
            editor.putString("trwind_sc",laterWeather.getWind_sc());
            editor.putString("trtmp_min",laterWeather.getTmp_min());
            editor.putString("trtmp_max",laterWeather.getTmp_max());
            editor.apply();
        }else if(i == 2){
            editor.putString("atrcond_txt_d",laterWeather.getCond_txt_d());
            editor.putString("atrwind_sc",laterWeather.getWind_sc());
            editor.putString("atrtmp_min",laterWeather.getTmp_min());
            editor.putString("atrtmp_max",laterWeather.getTmp_max());
            editor.apply();
        }
    }

}
