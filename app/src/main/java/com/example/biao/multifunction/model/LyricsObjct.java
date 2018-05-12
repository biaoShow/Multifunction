package com.example.biao.multifunction.model;

import android.support.annotation.NonNull;

/**
 * 每一句的时间歌词作为一个实体
 * Created by biao on 2018/5/9.
 */

public class LyricsObjct implements Comparable<LyricsObjct>{

    public String strTime;
    public long time;
    public String content;

    public LyricsObjct(){}

    public LyricsObjct(String strTime,long time,String content){
        this.strTime = strTime;
        this.time = time;
        this.content = content;
    }



    @Override
    public int compareTo(@NonNull LyricsObjct another) {
        return (int)(this.time - another.time);
    }
}
