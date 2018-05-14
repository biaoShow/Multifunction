package com.example.biao.multifunction.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.OverlayInfo;
import com.example.biao.multifunction.util.MyOrientationListener;
import java.util.List;

/**
 * 定位导航功能
 * Created by biao on 2018/5/2.
 */

public class NavigationFragment extends Fragment implements View.OnClickListener{

    private MapView mv_view;
    private BaiduMap mBaiduMap;
    private ImageView iv_location,iv_traffic,iv_three,iv_satellite,iv_commontocompass;
    //定位相关
    private LocationClient locationClient;
    private MyLocationListener listener;
    private boolean isFirst = true;//判断是否为第一次进入地图
    private double mLatitude;
    private double mLongitude;
    private BitmapDescriptor descriptor;
    private MyOrientationListener orientationListener;
    private float mCurrenX;
    private MyLocationConfiguration.LocationMode mode;

    //覆盖物相关
    private TextView tv_cate,tv_hotel;
    private BitmapDescriptor mOverlayBD;
    private RelativeLayout rl_overlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.navigation_fragment_layout,container,false);

        //初始化控件
        mv_view = view.findViewById(R.id.mv_view);
        mBaiduMap = mv_view.getMap();
        iv_location = view.findViewById(R.id.iv_location);
        iv_traffic = view.findViewById(R.id.iv_traffic);
        iv_three = view.findViewById(R.id.iv_three);
        iv_satellite = view.findViewById(R.id.iv_satellite);
        iv_commontocompass = view.findViewById(R.id.iv_commontocompass);
        tv_cate = view.findViewById(R.id.tv_cate);
        tv_hotel = view.findViewById(R.id.tv_hotel);
        rl_overlay = view.findViewById(R.id.rl_overlay);

                MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        //初始化定位
        initLocation();

        //初始化覆盖物
        initOverlay();

        iv_location.setOnClickListener(this);
        iv_traffic.setOnClickListener(this);
        iv_three.setOnClickListener(this);
        iv_satellite.setOnClickListener(this);
        iv_commontocompass.setOnClickListener(this);
        tv_cate.setOnClickListener(this);
        tv_hotel.setOnClickListener(this);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle bundleInfo = marker.getExtraInfo();
                OverlayInfo info = (OverlayInfo) bundleInfo.getSerializable("info");
                //通过rl_overlay获取布局的控件
                ImageView iv = rl_overlay.findViewById(R.id.iv_overlay_image);
                TextView tv_overlay_address = rl_overlay.findViewById(R.id.tv_overlay_address);
                TextView tv_name = rl_overlay.findViewById(R.id.tv_overlay_name);
                TextView tv_distance = rl_overlay.findViewById(R.id.tv_distance);
                TextView tv_overlay_love = rl_overlay.findViewById(R.id.tv_overlay_love);

                //分别为控件赋值
                if(info != null){
                    iv.setImageResource(info.getImageId());
                    tv_overlay_address.setText(info.getAddress());
                    tv_name.setText(info.getName());
                    tv_distance.setText(info.getDistance());
                    tv_overlay_love.setText(String.valueOf(info.getPraise()));
                }

                //点击覆盖物时显示textName
                InfoWindow infoWindow;
                TextView showName = new TextView(getActivity());
                showName.setBackgroundColor(0xFFFFFFFF);
                showName.getBackground().setAlpha(150);
                showName.setTextColor(Color.parseColor("#EE2C2C"));
                showName.setPadding(30,20,30,20);
                if(info != null){
                    showName.setText(info.getName());
                }
                LatLng showLatLng = marker.getPosition();
//                //将地图的点转化为屏幕坐标点
//                Point p = mBaiduMap.getProjection().toScreenLocation(showLatLng);
//                p.y -= 47;//偏移量
//                //将屏幕坐标点转化为地图的点
//                LatLng showLatLngSkewing = mBaiduMap.getProjection().fromScreenLocation(p);
                //初始化infowindown
                infoWindow = new InfoWindow(showName,showLatLng,-47);
                mBaiduMap.showInfoWindow(infoWindow);
                rl_overlay.setVisibility(View.VISIBLE);

                return true;
            }
        });


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                rl_overlay.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        return view;
    }

    /**
     * 初始化覆盖物
     */
    private void initOverlay() {
        mOverlayBD = BitmapDescriptorFactory.fromResource(R.mipmap.overlay);

    }

    /**
     *定位相关初始化
     */
    private void initLocation() {
        mode = MyLocationConfiguration.LocationMode.NORMAL;
        locationClient = new LocationClient(getActivity());
        listener = new MyLocationListener();
        locationClient.registerLocationListener(listener);

        //LocationClient相关设置
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//返回地址
        option.setOpenGps(true);//打开GPS
        option.setScanSpan(1000);//每个1秒请求一次

        locationClient.setLocOption(option);

        descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.location_logo);

        orientationListener = new MyOrientationListener(getActivity());

        orientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChange(float x) {
                mCurrenX = x;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_location:
                LatLng latLng = new LatLng(mLatitude,mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                break;
            case R.id.iv_traffic:
                if(mBaiduMap.isTrafficEnabled()){
                    mBaiduMap.setTrafficEnabled(false);
                    iv_traffic.setImageResource(R.mipmap.traffic_off);
                    Toast.makeText(getActivity(),"实时交通已关闭",Toast.LENGTH_SHORT).show();
                }else{
                    mBaiduMap.setTrafficEnabled(true);
                    iv_traffic.setImageResource(R.mipmap.traffic_on);
                    Toast.makeText(getActivity(),"实时交通已开启",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_three:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.iv_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.iv_commontocompass:
                if(mode == MyLocationConfiguration.LocationMode.NORMAL){
                    mode = MyLocationConfiguration.LocationMode.COMPASS;
                }else{
                    mode = MyLocationConfiguration.LocationMode.NORMAL;
                }
                break;
            case R.id.tv_cate:
                addOverlay(OverlayInfo.overlayInfosCate);
                break;
            case R.id.tv_hotel:
                addOverlay(OverlayInfo.overlayInfosHotel);
                break;
                default:
                    break;
        }
    }

    /**
     * 添加覆盖物
     * @param overlayInfos 覆盖物数据对象
     */
    private void addOverlay(List<OverlayInfo> overlayInfos) {

        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;
        for(OverlayInfo info:overlayInfos){
            //经纬度
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            //图标
            options = new MarkerOptions().position(latLng).icon(mOverlayBD).zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info",info);
            marker.setExtraInfo(bundle);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Navigation","onStart");
        isFirst = true;
        //开始定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!locationClient.isStarted()){
            locationClient.start();
        }
        //开启方向传感器
        orientationListener.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Navigation","onResume");
        mv_view.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&&
                    ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Navigation","onPause");
        mv_view.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Navigation","onStop");
        //停止定位
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        //关闭方向传感器
        orientationListener.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Navigation","onDestroy");
        mv_view.onDestroy();
    }

    private class MyLocationListener extends BDAbstractLocationListener {


        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrenX)
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();

            mBaiduMap.setMyLocationData(data);
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();

            //自定义定位图标
            MyLocationConfiguration config = new MyLocationConfiguration(mode,true,descriptor);
            mBaiduMap.setMyLocationConfiguration(config);

            if(isFirst){
                LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);

                isFirst = false;
            }

        }
    }
}
