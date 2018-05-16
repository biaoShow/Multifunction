package com.example.biao.multifunction.util;

/**
 * 获取定位地址县/区名称工具
 * Created by biao on 2018/5/16.
 */

public class LocationAddress {
    public static String addressCounty = null;
    public static void setAddressCounty(String address){
        if(address.contains("区")){
            addressCounty = address.substring(address.indexOf("市")+1,address.indexOf("区"));
        }else if(address.contains("县")){
            addressCounty = address.substring(address.indexOf("市")+1,address.indexOf("县"));
        }else{
            int i = address.lastIndexOf("市");
            int j = address.indexOf("市");
            addressCounty = address.substring(j+1,i);
        }
    }
}
