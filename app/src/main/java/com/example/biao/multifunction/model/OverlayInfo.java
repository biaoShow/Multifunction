package com.example.biao.multifunction.model;

import com.example.biao.multifunction.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟网络返回数据覆盖物实体类
 * Created by biao on 2018/5/14.
 */

public class OverlayInfo implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
    private int imageId;
    private String distance;
    private int praise;
    private String address;


    public static List<OverlayInfo> overlayInfosCate = new ArrayList<>();
    public static List<OverlayInfo> overlayInfosHotel = new ArrayList<>();

    static {
        overlayInfosCate.add(new OverlayInfo("全兴酒店",22.9629121118,113.3740186920,
                R.mipmap.overlay_one,"距离2300米",1024,"广东省广州市番禺区大北路414号"));
        overlayInfosCate.add(new OverlayInfo("尊宝比萨",22.9604679203,113.3683214726,
                R.mipmap.overlay_two,"距离3400米",2120,"广东省广州市番禺区光明北路391号"));
        overlayInfosHotel.add(new OverlayInfo("裕珑酒店",22.9592579203,113.3709614726,
                R.mipmap.overlay_three,"距离2200米",1089,"广东省广州市番禺区东环路445号"));
        overlayInfosHotel.add(new OverlayInfo("戴斯酒店",22.9562427051,113.3755700485,
                R.mipmap.overlay_four,"距离2540米",2201,"广东省广州市番禺区大北路373号"));
    }

    public OverlayInfo(String name, double latitude, double longitude, int imageId, String distance, int praise,String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageId = imageId;
        this.distance = distance;
        this.praise = praise;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }
}

