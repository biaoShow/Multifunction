package com.example.biao.multifunction.model;

import java.util.List;

/**
 * Created by benxiang on 2019/4/10.
 */

public class SongNameGetLyrics {

    /**
     * code : 0
     * count : 12
     * result : [{"aid":2536832,"artist_id":2,"lrc":"http://s.gecimi.com/lrc/301/30132/3013249.lrc","sid":3013249,"song":"不再犹豫"},{"aid":1490186,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/157/15723/1572390.lrc","sid":1572390,"song":"不再犹豫"},{"aid":1495973,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/158/15805/1580566.lrc","sid":1580566,"song":"不再犹豫"},{"aid":1531118,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/162/16261/1626144.lrc","sid":1626144,"song":"不再犹豫"},{"aid":1656038,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/179/17907/1790763.lrc","sid":1790763,"song":"不再犹豫"},{"aid":1775234,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/195/19515/1951521.lrc","sid":1951521,"song":"不再犹豫"},{"aid":2051669,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/233/23323/2332303.lrc","sid":2332303,"song":"不再犹豫"},{"aid":2412704,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/283/28377/2837707.lrc","sid":2837707,"song":"不再犹豫"},{"aid":2607041,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/311/31116/3111651.lrc","sid":3111651,"song":"不再犹豫"},{"aid":3093833,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/377/37740/3774078.lrc","sid":3774078,"song":"不再犹豫"},{"aid":3161846,"artist_id":9208,"lrc":"http://s.gecimi.com/lrc/386/38612/3861236.lrc","sid":3861236,"song":"不再犹豫"},{"aid":2132588,"artist_id":30512,"lrc":"http://s.gecimi.com/lrc/244/24437/2443718.lrc","sid":2443718,"song":"不再犹豫"}]
     */

    private int code;
    private int count;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * aid : 2536832
         * artist_id : 2
         * lrc : http://s.gecimi.com/lrc/301/30132/3013249.lrc
         * sid : 3013249
         * song : 不再犹豫
         */

        private int aid;
        private int artist_id;
        private String lrc;
        private int sid;
        private String song;

        public int getAid() {
            return aid;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public int getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(int artist_id) {
            this.artist_id = artist_id;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }
    }
}
