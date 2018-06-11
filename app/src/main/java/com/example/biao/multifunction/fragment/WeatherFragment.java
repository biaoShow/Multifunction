package com.example.biao.multifunction.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.db.WeatherDB;
import com.example.biao.multifunction.util.LocationAddress;
import com.example.biao.multifunction.util.WeatehwrHttpCallbackListener;
import com.example.biao.multifunction.util.WeatherAnalysisUtil;
import com.example.biao.multifunction.util.WeatherHttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * 天气预报功能
 * Created by biao on 2018/5/2.
 */

public class WeatherFragment extends Fragment {

    private WeatherReceiver weatherReceiver;
    private LocationReceiver locationReceiver;
    private TextView tv_weather_temperature,tv_weather_address,tv_weather_weather,tv_weather_direction,
            tv_weather_directionprice,tv_weather_humidity,tv_weather_visibility;//实时天气ui
    private TextView tv_weather_weathermini,tv_weather_todaywind_dir,tv_weather_mintemperature,tv_weather_maxtemperature;//今天天气ui
    private TextView tv_weather_weatherminitr,tv_weather_trwind_dir,tv_weather_mintemperaturetr,tv_weather_maxtemperaturetr;//明天天气ui
    private TextView tv_weather_weatherminiatr,tv_weather_atrwind_dir,tv_weather_mintemperatureatr,tv_weather_maxtemperatureatr;//后天天气ui
    private WeatherDB weatherDB;
    private RelativeLayout rl_weather_bg;
    private ImageView iv_weather_today,iv_weather_tomorrow,iv_weather_aftertomorrow;
    private boolean isFirst=true;//记录是否是第一次进入
    private String countyCode;//记录搜索城市代号
    private String countyName;//记录搜索城市名称

    //获取网络数据完成后更新ui
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment_layout,container,false);
        weatherDB = WeatherDB.getInstance(getActivity());
        //注册广播接收器
        weatherReceiver = new WeatherReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.activity.UPDATEWEATHERUI");
        getActivity().registerReceiver(weatherReceiver,intentFilter);

        locationReceiver = new LocationReceiver();
        IntentFilter intentFilterL = new IntentFilter();
        intentFilterL.addAction("com.example.biao.LOCATIONUPUI");
        getActivity().registerReceiver(locationReceiver,intentFilterL);

        //实时天气ui
        tv_weather_temperature = view.findViewById(R.id.tv_weather_temperature);
        tv_weather_address = view.findViewById(R.id.tv_weather_address);
        tv_weather_weather = view.findViewById(R.id.tv_weather_weather);
        tv_weather_direction = view.findViewById(R.id.tv_weather_direction);
        tv_weather_directionprice = view.findViewById(R.id.tv_weather_directionprice);
        tv_weather_humidity = view.findViewById(R.id.tv_weather_humidity);
        tv_weather_visibility = view.findViewById(R.id.tv_weather_visibility);
        rl_weather_bg = view.findViewById(R.id.rl_weather_bg);

        //今天天气ui
        tv_weather_weathermini = view.findViewById(R.id.tv_weather_weathermini);
        tv_weather_todaywind_dir = view.findViewById(R.id.tv_weather_todaywind_dir);
        tv_weather_mintemperature = view.findViewById(R.id.tv_weather_mintemperature);
        tv_weather_maxtemperature = view.findViewById(R.id.tv_weather_maxtemperature);
        iv_weather_today = view.findViewById(R.id.iv_weather_today);

        //明天天气ui
        tv_weather_weatherminitr = view.findViewById(R.id.tv_weather_weatherminitr);
        tv_weather_trwind_dir = view.findViewById(R.id.tv_weather_trwind_dir);
        tv_weather_mintemperaturetr = view.findViewById(R.id.tv_weather_mintemperaturetr);
        tv_weather_maxtemperaturetr = view.findViewById(R.id.tv_weather_maxtemperaturetr);
        iv_weather_tomorrow = view.findViewById(R.id.iv_weather_tomorrow);

        //后天天气ui
        tv_weather_weatherminiatr = view.findViewById(R.id.tv_weather_weatherminiatr);
        tv_weather_atrwind_dir = view.findViewById(R.id.tv_weather_atrwind_dir);
        tv_weather_mintemperatureatr = view.findViewById(R.id.tv_weather_mintemperatureatr);
        tv_weather_maxtemperatureatr = view.findViewById(R.id.tv_weather_maxtemperatureatr);
        iv_weather_aftertomorrow = view.findViewById(R.id.iv_weather_aftertomorrow);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //每次进入更新天气
        tv_weather_address.setText(countyName);
        getWeatherCode();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        //实时天气ui
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cond_txt = preferences.getString("cond_txt",null);
        tv_weather_temperature.setText(preferences.getString("tmp",null) + "°");
        tv_weather_weather.setText(cond_txt);
        if(cond_txt != null){
            if(cond_txt.equals("晴")){
                rl_weather_bg.setBackgroundResource(R.mipmap.clear);
            }else if(cond_txt.contains("雨")){
                rl_weather_bg.setBackgroundResource(R.mipmap.rain);
            }else{
                rl_weather_bg.setBackgroundResource(R.mipmap.cloudy);
            }
        }
        tv_weather_direction.setText(preferences.getString("wind_dir",null));
        tv_weather_directionprice.setText(preferences.getString("wind_sc",null) + "级");
        tv_weather_humidity.setText(preferences.getString("hum",null) + "%");
        tv_weather_visibility.setText(preferences.getString("vis",null));

        //今天天气
        String tcond_txt_d = preferences.getString("tcond_txt_d",null);
        tv_weather_weathermini.setText(tcond_txt_d);
        if (tcond_txt_d != null) {
            if(tcond_txt_d.equals("晴")){
                iv_weather_today.setImageResource(R.mipmap.fine);
            }else if(tcond_txt_d.contains("雨")){
                iv_weather_today.setImageResource(R.mipmap.rain_mini);
            }else{
                iv_weather_today.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_todaywind_dir.setText(preferences.getString("twind_sc",null) + "级");
        tv_weather_mintemperature.setText(preferences.getString("ttmp_min",null) + "°");
        tv_weather_maxtemperature.setText(preferences.getString("ttmp_max",null) + "°");

        //明天天气
        String trcond_txt_d = preferences.getString("trcond_txt_d",null);
        tv_weather_weatherminitr.setText(trcond_txt_d);
        if (trcond_txt_d != null) {
            if(trcond_txt_d.equals("晴")){
                iv_weather_tomorrow.setImageResource(R.mipmap.fine);
            }else if(trcond_txt_d.contains("雨")){
                iv_weather_tomorrow.setImageResource(R.mipmap.rain_mini);
            }else{
                iv_weather_tomorrow.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_trwind_dir.setText(preferences.getString("trwind_sc",null) + "级");
        tv_weather_mintemperaturetr.setText(preferences.getString("trtmp_min",null) + "°");
        tv_weather_maxtemperaturetr.setText(preferences.getString("trtmp_max",null) + "°");

        //后天天气
        String atrcond_txt_d = preferences.getString("atrcond_txt_d",null);
        tv_weather_weatherminiatr.setText(atrcond_txt_d);
        if(atrcond_txt_d != null){
            if(atrcond_txt_d.equals("晴")){
                iv_weather_aftertomorrow.setImageResource(R.mipmap.fine);
            }else if(atrcond_txt_d.contains("雨")){
                iv_weather_aftertomorrow.setImageResource(R.mipmap.rain_mini);
            }else{
                iv_weather_aftertomorrow.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_atrwind_dir.setText(preferences.getString("atrwind_sc",null) + "级");
        tv_weather_mintemperatureatr.setText(preferences.getString("atrtmp_min",null) + "°");
        tv_weather_maxtemperatureatr.setText(preferences.getString("atrtmp_max",null) + "°");

    }

    /**
     * 广播接受器（接收SearchWeatherActivity发出的广播）
     */
    public class WeatherReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            countyCode = intent.getStringExtra("countycode");
            countyName = intent.getStringExtra("countyname");
            tv_weather_address.setText(countyName);
            getWeatherCode();
        }
    }


    /**
     * 通过网络获取天气代号
     */
    private void getWeatherCode(){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        WeatherHttpUtils.sendHttpRequest(address, new WeatehwrHttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                String[] array = response.split("\\|");
                if(array.length == 2){
                    sendToServer("CN" + array[1]);
                    sendToLaterServer("CN" + array[1]);
                }

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取天气代号失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    /**
     * 定位后接受广播获取数据并更新UI
     */
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isFirst){
                countyName = intent.getStringExtra("countyname");
                tv_weather_address.setText(countyName);
                if(LocationAddress.addressCounty != null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendToServer(URLEncoder.encode(LocationAddress.addressCounty));
                            sendToLaterServer(URLEncoder.encode(LocationAddress.addressCounty));
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }).start();

                    isFirst = false;
                }
            }
        }
    }

    /**
     * 根据获取的城市天气代号，获取天气情况
     * @param code 城市代号/城市名称
     */
    private void sendToServer(String code){
        String address = "https://free-api.heweather.com/s6/weather/now?key=6cc01e4baf8f47e191d0958b865e048b&location="+code;
        HttpsURLConnection connection;
        try {
            URL url = new URL(address);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!=null){
                builder.append(line);
            }

            WeatherAnalysisUtil.handelNowWeatherRespose(weatherDB,builder.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过网络获取未来天气
     * @param code 城市代号/城市名称
     */
    private void sendToLaterServer(String code){
        String address = "https://free-api.heweather.com/s6/weather/forecast?key=6cc01e4baf8f47e191d0958b865e048b&location="+code;
        HttpsURLConnection connection;
        try {
            URL url = new URL(address);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine())!=null){
                builder.append(line);
            }

            WeatherAnalysisUtil.handelLaterWeatherRespose(weatherDB,builder.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(weatherReceiver);
        getActivity().unregisterReceiver(locationReceiver);
    }
}
