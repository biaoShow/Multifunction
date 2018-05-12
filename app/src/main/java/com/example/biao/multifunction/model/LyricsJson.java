package com.example.biao.multifunction.model;

/**
 * 歌词json实体类
 * Created by biao on 2018/5/6.
 */

public class LyricsJson {
    private int aid;
    private int artist_id;
    private String lrc;
    private int sid;
    private String song;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }


}
