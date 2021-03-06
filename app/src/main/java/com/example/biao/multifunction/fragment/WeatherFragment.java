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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.internetUtil.GetMap;
import com.example.biao.multifunction.internetUtil.RetrofitHelper;
import com.example.biao.multifunction.model.LaterWeather;
import com.example.biao.multifunction.model.WeatherNow;
import com.example.biao.multifunction.util.LocationAddress;
import com.example.biao.multifunction.util.SharedPreferencesUtil;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 天气预报功能
 * Created by biao on 2018/5/2.
 */

public class WeatherFragment extends Fragment {

    private WeatherReceiver weatherReceiver;
    private LocationReceiver locationReceiver;
    private TextView tv_weather_temperature, tv_weather_address, tv_weather_weather, tv_weather_direction,
            tv_weather_directionprice, tv_weather_humidity, tv_weather_visibility;//实时天气ui
    private TextView tv_weather_weathermini, tv_weather_todaywind_dir, tv_weather_mintemperature, tv_weather_maxtemperature;//今天天气ui
    private TextView tv_weather_weatherminitr, tv_weather_trwind_dir, tv_weather_mintemperaturetr, tv_weather_maxtemperaturetr;//明天天气ui
    private TextView tv_weather_weatherminiatr, tv_weather_atrwind_dir, tv_weather_mintemperatureatr, tv_weather_maxtemperatureatr;//后天天气ui
    //    private WeatherDB weatherDB;
    private RelativeLayout rl_weather_bg;
    private ImageView iv_weather_today, iv_weather_tomorrow, iv_weather_aftertomorrow;
    private boolean isFirst = true;//记录是否是第一次进入
    private String countyName;//记录搜索城市名称

    //SharePreferences
    private SharedPreferencesUtil sharedPreferencesUtil;

    //获取网络数据完成后更新ui
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播接收器
        weatherReceiver = new WeatherReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.activity.UPDATEWEATHERUI");
        getActivity().registerReceiver(weatherReceiver, intentFilter);

        locationReceiver = new LocationReceiver();
        IntentFilter intentFilterL = new IntentFilter();
        intentFilterL.addAction("com.example.biao.LOCATIONUPUI");
        getActivity().registerReceiver(locationReceiver, intentFilterL);

        sharedPreferencesUtil = SharedPreferencesUtil.getIntent(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment_layout, container, false);
//        weatherDB = WeatherDB.getInstance(getActivity());

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
        if (countyName == null) {
            countyName = "番禺";
        }
        tv_weather_address.setText(countyName);
        sendToServer(countyName);
//        getWeatherCode();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        //实时天气ui
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cond_txt = sharedPreferencesUtil.getString("cond_txt");
        tv_weather_temperature.setText(sharedPreferencesUtil.getString("tmp") + "°");
        tv_weather_weather.setText(cond_txt);
        if (cond_txt != null) {
            if (cond_txt.equals("晴")) {
                rl_weather_bg.setBackgroundResource(R.mipmap.clear);
            } else if (cond_txt.contains("雨")) {
                rl_weather_bg.setBackgroundResource(R.mipmap.rain);
            } else {
                rl_weather_bg.setBackgroundResource(R.mipmap.cloudy);
            }
        }
        tv_weather_direction.setText(sharedPreferencesUtil.getString("wind_dir"));
        tv_weather_directionprice.setText(sharedPreferencesUtil.getString("wind_sc") + "级");
        tv_weather_humidity.setText(sharedPreferencesUtil.getString("hum") + "%");
        tv_weather_visibility.setText(sharedPreferencesUtil.getString("vis"));

        //今天天气
        String tcond_txt_d = sharedPreferencesUtil.getString("tcond_txt_d");
        tv_weather_weathermini.setText(tcond_txt_d);
        if (tcond_txt_d != null) {
            if (tcond_txt_d.equals("晴")) {
                iv_weather_today.setImageResource(R.mipmap.fine);
            } else if (tcond_txt_d.contains("雨")) {
                iv_weather_today.setImageResource(R.mipmap.rain_mini);
            } else {
                iv_weather_today.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_todaywind_dir.setText(sharedPreferencesUtil.getString("twind_sc") + "级");
        tv_weather_mintemperature.setText(sharedPreferencesUtil.getString("ttmp_min") + "°");
        tv_weather_maxtemperature.setText(sharedPreferencesUtil.getString("ttmp_max") + "°");

        //明天天气
        String trcond_txt_d = sharedPreferencesUtil.getString("trcond_txt_d");
        tv_weather_weatherminitr.setText(trcond_txt_d);
        if (trcond_txt_d != null) {
            if (trcond_txt_d.equals("晴")) {
                iv_weather_tomorrow.setImageResource(R.mipmap.fine);
            } else if (trcond_txt_d.contains("雨")) {
                iv_weather_tomorrow.setImageResource(R.mipmap.rain_mini);
            } else {
                iv_weather_tomorrow.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_trwind_dir.setText(sharedPreferencesUtil.getString("trwind_sc") + "级");
        tv_weather_mintemperaturetr.setText(sharedPreferencesUtil.getString("trtmp_min") + "°");
        tv_weather_maxtemperaturetr.setText(sharedPreferencesUtil.getString("trtmp_max") + "°");

        //后天天气
        String atrcond_txt_d = sharedPreferencesUtil.getString("atrcond_txt_d");
        tv_weather_weatherminiatr.setText(atrcond_txt_d);
        if (atrcond_txt_d != null) {
            if (atrcond_txt_d.equals("晴")) {
                iv_weather_aftertomorrow.setImageResource(R.mipmap.fine);
            } else if (atrcond_txt_d.contains("雨")) {
                iv_weather_aftertomorrow.setImageResource(R.mipmap.rain_mini);
            } else {
                iv_weather_aftertomorrow.setImageResource(R.mipmap.cloudy_mini);
            }
        }
        tv_weather_atrwind_dir.setText(sharedPreferencesUtil.getString("atrwind_sc") + "级");
        tv_weather_mintemperatureatr.setText(sharedPreferencesUtil.getString("atrtmp_min") + "°");
        tv_weather_maxtemperatureatr.setText(sharedPreferencesUtil.getString("atrtmp_max") + "°");

    }

    /**
     * 广播接受器（接收SearchWeatherActivity发出的广播）
     */
    public class WeatherReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            countyName = intent.getStringExtra("countyname");
            tv_weather_address.setText(countyName);
//            getWeatherCode();
        }
    }


//    /**
//     * 通过网络获取天气代号
//     */
//    private void getWeatherCode() {
//        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
//        WeatherHttpUtils.sendHttpRequest(address, new WeatehwrHttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                String[] array = response.split("\\|");
//                if (array.length == 2) {
//                    sendToServer("CN" + array[1]);
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(), "获取天气代号失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    /**
     * 定位后接受广播获取数据并更新UI
     */
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFirst) {
                countyName = intent.getStringExtra("countyname");
                tv_weather_address.setText(countyName);
                if (LocationAddress.addressCounty != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            sendToServer(URLEncoder.encode(LocationAddress.addressCounty));
                            sendToServer(LocationAddress.addressCounty);
//                            sendToLaterServer(URLEncoder.encode(LocationAddress.addressCounty));

//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
                        }
                    }).start();

                    isFirst = false;
                }
            }
        }
    }

    /**
     * 根据获取的城市天气代号，获取天气情况
     *
     * @param code 城市代号/城市名称
     */
    private void sendToServer(final String code) {
        RetrofitHelper.getInstance("https://free-api.heweather.com").getInternetInterface()
                .getNowWeather(GetMap.getNowWeatherMap(code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<WeatherNow>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(WeatherNow weatherNow) {
                        List<WeatherNow.HeWeather6Bean> heWeather6 = weatherNow.getHeWeather6();
                        if (heWeather6.size() > 0) {
                            saveWeatherNow(heWeather6.get(0).getNow());
                        } else {
                            Log.e("onNext", "暂无now天气");
                        }
                        sendToLaterServer(code);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        String address = "https://free-api.heweather.com/s6/weather/now?key=6cc01e4baf8f47e191d0958b865e048b&location=" + code;
//        HttpsURLConnection connection;
//        try {
//            URL url = new URL(address);
//            connection = (HttpsURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(8000);
//            connection.setReadTimeout(8000);
//            InputStream in = connection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//
//            WeatherAnalysisUtil.handelNowWeatherRespose(weatherDB, builder.toString());
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 通过网络获取未来天气
     *
     * @param code 城市代号/城市名称
     */
    private void sendToLaterServer(String code) {
        RetrofitHelper.getInstance("https://free-api.heweather.com").getInternetInterface()
                .getFutureWeather(GetMap.getFutureWeatherMap(code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<LaterWeather>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(LaterWeather laterWeather) {
                        List<LaterWeather.HeWeather6Bean> heWeather6Bean = laterWeather.getHeWeather6();
                        if (heWeather6Bean.size() > 0) {
                            List<LaterWeather.HeWeather6Bean.DailyForecastBean> dailyForecastBeans = heWeather6Bean.get(0).getDaily_forecast();
                            if (dailyForecastBeans != null && dailyForecastBeans.size() >= 3) {
                                for (int i = 0; i < 3; i++) {
                                    saveWeatherLater(dailyForecastBeans.get(i), i);
                                }
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } else {
                                Log.e("获取未来天气onNext", "dailyForecastBeans");
                            }
                        } else {
                            Log.e("获取未来天气onNext", "失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("获取未来天气onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        String address = "https://free-api.heweather.com/s6/weather/forecast?key=6cc01e4baf8f47e191d0958b865e048b&location=" + code;
//        HttpsURLConnection connection;
//        try {
//            URL url = new URL(address);
//            connection = (HttpsURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(8000);
//            connection.setReadTimeout(8000);
//            InputStream in = connection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//
//            WeatherAnalysisUtil.handelLaterWeatherRespose(weatherDB, builder.toString());
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 保存实时天气到SharedPreferences
     *
     * @param weatherNow 网络返回json数据对象
     */
    public void saveWeatherNow(WeatherNow.HeWeather6Bean.NowBean weatherNow) {
        if (weatherNow != null) {
            sharedPreferencesUtil.putString("tmp", weatherNow.getTmp());
            sharedPreferencesUtil.putString("cond_txt", weatherNow.getCond_txt());
            sharedPreferencesUtil.putString("wind_dir", weatherNow.getWind_dir());
            sharedPreferencesUtil.putString("wind_sc", weatherNow.getWind_sc());
            sharedPreferencesUtil.putString("hum", weatherNow.getHum());
            sharedPreferencesUtil.putString("vis", weatherNow.getVis());
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
//            editor.putString("tmp", weatherNow.getTmp());//当前温度
//            editor.putString("cond_txt", weatherNow.getCond_txt());//实况天气
//            editor.putString("wind_dir", weatherNow.getWind_dir());//风向
//            editor.putString("wind_sc", weatherNow.getWind_sc());//风力
//            editor.putString("hum", weatherNow.getHum());//相对湿度
//            editor.putString("vis", weatherNow.getVis());//可见度
//            editor.apply();
        } else {
            Log.e("WeatherFragment", "网络返回实时天气为空");
        }
    }

    /**
     * 保存未来天气到SharedPreferences
     *
     * @param laterWeather 天气对象
     * @param i            未来天数
     */
    public void saveWeatherLater(LaterWeather.HeWeather6Bean.DailyForecastBean laterWeather, int i) {
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        if (i == 0) {
            sharedPreferencesUtil.putString("tcond_txt_d", laterWeather.getCond_txt_d());
            sharedPreferencesUtil.putString("twind_sc", laterWeather.getWind_sc());
            sharedPreferencesUtil.putString("ttmp_min", laterWeather.getTmp_min());
            sharedPreferencesUtil.putString("ttmp_max", laterWeather.getTmp_max());
//            editor.apply();
        } else if (i == 1) {
            sharedPreferencesUtil.putString("trcond_txt_d", laterWeather.getCond_txt_d());
            sharedPreferencesUtil.putString("trwind_sc", laterWeather.getWind_sc());
            sharedPreferencesUtil.putString("trtmp_min", laterWeather.getTmp_min());
            sharedPreferencesUtil.putString("trtmp_max", laterWeather.getTmp_max());
//            editor.apply();
        } else if (i == 2) {
            sharedPreferencesUtil.putString("atrcond_txt_d", laterWeather.getCond_txt_d());
            sharedPreferencesUtil.putString("atrwind_sc", laterWeather.getWind_sc());
            sharedPreferencesUtil.putString("atrtmp_min", laterWeather.getTmp_min());
            sharedPreferencesUtil.putString("atrtmp_max", laterWeather.getTmp_max());
//            editor.apply();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(weatherReceiver);
        getActivity().unregisterReceiver(locationReceiver);
    }
}
