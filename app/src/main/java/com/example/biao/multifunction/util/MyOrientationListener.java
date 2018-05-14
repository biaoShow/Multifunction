package com.example.biao.multifunction.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 定位相关，方向传感器监听类
 * Created by biao on 2018/5/14.
 */

public class MyOrientationListener implements SensorEventListener{

    private SensorManager mSensorManager;//传感器管理者
    private Context context;
    private Sensor mSensor;//传感器
    private float lastX;//记录x轴

    public MyOrientationListener(Context context){
        this.context = context;
    }

    /**
     * 开始监听
     */
    public void start(){
        //获取系统方向传感器
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //判断是否获取到（支不支持）
        if(mSensorManager != null){
            //获得方向传感器
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        //判断是否获取到传感器（支不支持）
        if(mSensor != null){
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * 停止监听
     */
    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x = event.values[SensorManager.DATA_X];

            if(Math.abs(x - lastX) > 1.0){
                if(onOrientationListener != null){
                    onOrientationListener.onOrientationChange(x);
                }
            }

            lastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private OnOrientationListener onOrientationListener;

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.onOrientationListener = onOrientationListener;
    }

    public interface  OnOrientationListener{
        void onOrientationChange(float x);
    }
}
