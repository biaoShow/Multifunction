package com.example.biao.multifunction.model;

import java.io.Serializable;

/**
 * 音乐实体类
 * Created by biao on 2018/5/3.
 */

public class Song implements Serializable {
    private String song;//歌曲名字
    private String singer;//歌手名字
    private String path;//歌曲路径
    private int duration;//歌曲长度
    private long size;//歌曲大小

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
