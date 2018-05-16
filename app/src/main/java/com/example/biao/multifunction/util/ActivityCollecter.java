package com.example.biao.multifunction.util;

import android.app.Activity;

import java.util.ArrayList;

/**
 * activity管理工具类
 * Created by biao on 2018/5/15.
 */

public class ActivityCollecter {
    public static ArrayList<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void removeAllActivity(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
