package com.example.biao.multifunction.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.example.biao.multifunction.R;
import com.example.biao.multifunction.adapter.WeatherRecyclerViewAdapter;
import com.example.biao.multifunction.db.WeatherDB;
import com.example.biao.multifunction.model.City;
import com.example.biao.multifunction.model.County;
import com.example.biao.multifunction.model.Province;
import com.example.biao.multifunction.util.OnClickWeatherItemListener;
import com.example.biao.multifunction.util.WeatehwrHttpCallbackListener;
import com.example.biao.multifunction.util.WeatherAnalysisUtil;
import com.example.biao.multifunction.util.WeatherHttpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索城市天气预报Activity
 * Created by biao on 2018/5/15.
 */

public class SearchWeatherActivity extends BaseActivity{

    private List<Province> provinceList;//省份对象
    private List<City> cityList;//市级对象
    private List<County> countyList;//县/区对象
    private WeatherDB weatherDB;//数据操作库对象
    private Province selectProvince;//记录选择的省
    private City selectCity;//记录选择的市
    private String current;//记录当前页面
    private TextView tv_title_text;
    private RecyclerView weather_recyclerview;
    private WeatherRecyclerViewAdapter weatherRecyclerViewAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_search_layuot);

        //沉浸效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }

        weatherDB = WeatherDB.getInstance(this);
        tv_title_text = findViewById(R.id.tv_title_text);
        weather_recyclerview = findViewById(R.id.weather_recyclerview);
        weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(this,list);

        weather_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        weather_recyclerview.setAdapter(weatherRecyclerViewAdapter);
        weatherRecyclerViewAdapter.setOnClickWeatherItemListener(new OnClickWeatherItemListener() {
            @Override
            public void onClickItem(int position) {
                if("province".equals(current)){
                    selectProvince = provinceList.get(position);
                    queryCities();
                }else if("city".equals(current)){
                    selectCity = cityList.get(position);
                    queryCounties();
                }else if("county".equals(current)){
                    //发送广播给WeatherFragment更新UI
                    Intent intent = new Intent("com.example.biao.activity.UPDATEWEATHERUI");
                    intent.putExtra("countycode",countyList.get(position).getCountyCode());
                    intent.putExtra("countyname",countyList.get(position).getCountyName());
                    sendBroadcast(intent);
                    finish();
                }
            }
        });

        queryProvinces();
    }

    /**
     * 获取省份数据，先查询数据库是否有数据，没有则到服务器获取
     */
    private void queryProvinces(){
        provinceList = weatherDB.loadProvince();
        if(provinceList.size() > 0){
            list.clear();
            for(Province province:provinceList){
                list.add(province.getProvinceName());
            }
            weatherRecyclerViewAdapter.notifyDataSetChanged();
            current = "province";
            tv_title_text.setText("中国");
        }else{
            queryFromServer("province",null);
        }
    }

    /**
     * 获取市级数据，先查询数据库是否有数据，没有则到服务区获取
     */
    private void queryCities() {
        cityList = weatherDB.loadCity(String.valueOf(selectProvince.getId()));
        if(cityList.size() > 0){
            list.clear();
            for(City city: cityList){
                list.add(city.getCityName());
            }
            weatherRecyclerViewAdapter.notifyDataSetChanged();
            current = "city";
            tv_title_text.setText(selectProvince.getProvinceName());
        }else{
            queryFromServer("city",selectProvince.getProvinceCode());
        }
    }

    /**
     * 获取县/区数据，先查询数据库是否存在数据，没有则到服务器获取
     */
    private void queryCounties() {
        countyList = weatherDB.loadCounty(String.valueOf(selectCity.getId()));
        if(countyList.size() > 0){
            list.clear();
            for(County county: countyList){
                list.add(county.getCountyName());
            }
            weatherRecyclerViewAdapter.notifyDataSetChanged();
            current = "county";
            tv_title_text.setText(selectCity.getCityName());
        }else{
            queryFromServer("county",selectCity.getCityCode());
        }
    }

    /**
     * 通过网络请求获取省市区/县的数据
     * @param type 请求的类型（省/市/县）
     * @param code 选择的省/市代号
     */
    private void queryFromServer(final String type, String code) {
        String address = "http://www.weather.com.cn/data/list3/";
        if("province".equals(type)){
            address += "city.xml";
        }else{
            address = address + "city" + code + ".xml";
        }
        WeatherHttpUtils.sendHttpRequest(address, new WeatehwrHttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = WeatherAnalysisUtil.handleProvinceResponse(weatherDB,response);
                }else if("city".equals(type)){
                    result = WeatherAnalysisUtil.handelCityResponse(weatherDB,response,selectProvince.getId());
                }else if("county".equals(type)){
                    result = WeatherAnalysisUtil.handelCountyRespose(weatherDB,response,selectCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchWeatherActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if("county".equals(current)){
            queryCities();
        }else if("city".equals(current)){
            queryProvinces();
        }else{
            finish();
        }
    }
}
