package com.example.biao.multifunction.model;

/**
 * 视频实体类
 * Created by ZeQiang Fang on 2018/6/8.
 */

public class VideoInfo {
    private String name;
    private String path;
    private int time;
    private String imagePath;


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
