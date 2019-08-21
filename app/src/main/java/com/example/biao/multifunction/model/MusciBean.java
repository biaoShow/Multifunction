package com.example.biao.multifunction.model;

import java.util.List;

/**
 * 百度音乐详情实体类
 * Created by benxiang on 2019/8/21.
 */

public class MusciBean {

    /**
     * song : [{"bitrate_fee":"{\"0\":\"129|-1\",\"1\":\"-1|-1\"}","weight":"1099","songname":"七里香","resource_type":"0","songid":"274085","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周杰伦","info":"专辑同名歌曲《七里香》其实暗示了专辑返璞归真的基调，该歌灵感来自席慕蓉的诗，伤感的歌�","resource_provider":"1","control":"1100000000","encrypted_songid":"59081D69E9760859085EA9"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"99","songname":"七里香","resource_type":"0","songid":"544468969","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"DJ阿奇","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"54082073EFE8085959E529"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"99","songname":"七里香","resource_type":"0","songid":"523895","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"施艾敏","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"86057fe7709561d1d7eL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"99","songname":"七里香","resource_type":"0","songid":"243120061","has_mv":"0","yyr_artist":"0","resource_type_ext":"2","artistname":"刘瑞琦","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"62081DF1D103085B4340DB"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"99","songname":"七里香","resource_type":"0","songid":"1075471","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"刘芳","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"340610690f0956c59757L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"79","songname":"七里香","resource_type":"2","songid":"208828","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"陈淑桦","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"370532fbc09579b1ddbL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"79","songname":"七里香","resource_type":"2","songid":"13917936","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"康乔","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"2106d45ef009562f79faL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"79","songname":"七里香","resource_type":"2","songid":"304991","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"阿紫","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"71054a75f09561cf94cL"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"79","songname":"七里香","resource_type":"2","songid":"2081735","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"梦雨","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"62061fc3c709561cf68eL"}]
     * order : song,album
     * error_code : 22000
     * album : [{"albumname":"七里香","weight":"10","artistname":"周杰伦","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/4dc725cd39963fa3f05a9cf38439b554/551080154/551080154.jpg@s_2,w_40,h_40","albumid":"67909"}]
     */

    private String order;
    private int error_code;
        private List<SongBean> song;
    private List<AlbumBean> album;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<SongBean> getSong() {
        return song;
    }

    public void setSong(List<SongBean> song) {
        this.song = song;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public static class SongBean {
        /**
         * bitrate_fee : {"0":"129|-1","1":"-1|-1"}
         * weight : 1099
         * songname : 七里香
         * resource_type : 0
         * songid : 274085
         * has_mv : 0
         * yyr_artist : 0
         * resource_type_ext : 0
         * artistname : 周杰伦
         * info : 专辑同名歌曲《七里香》其实暗示了专辑返璞归真的基调，该歌灵感来自席慕蓉的诗，伤感的歌�
         * resource_provider : 1
         * control : 1100000000
         * encrypted_songid : 59081D69E9760859085EA9
         */

        private String bitrate_fee;
        private String weight;
        private String songname;
        private String resource_type;
        private String songid;
        private String has_mv;
        private String yyr_artist;
        private String resource_type_ext;
        private String artistname;
        private String info;
        private String resource_provider;
        private String control;
        private String encrypted_songid;

        public String getBitrate_fee() {
            return bitrate_fee;
        }

        public void setBitrate_fee(String bitrate_fee) {
            this.bitrate_fee = bitrate_fee;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public String getResource_type() {
            return resource_type;
        }

        public void setResource_type(String resource_type) {
            this.resource_type = resource_type;
        }

        public String getSongid() {
            return songid;
        }

        public void setSongid(String songid) {
            this.songid = songid;
        }

        public String getHas_mv() {
            return has_mv;
        }

        public void setHas_mv(String has_mv) {
            this.has_mv = has_mv;
        }

        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getResource_provider() {
            return resource_provider;
        }

        public void setResource_provider(String resource_provider) {
            this.resource_provider = resource_provider;
        }

        public String getControl() {
            return control;
        }

        public void setControl(String control) {
            this.control = control;
        }

        public String getEncrypted_songid() {
            return encrypted_songid;
        }

        public void setEncrypted_songid(String encrypted_songid) {
            this.encrypted_songid = encrypted_songid;
        }
    }

    public static class AlbumBean {
        /**
         * albumname : 七里香
         * weight : 10
         * artistname : 周杰伦
         * resource_type_ext : 0
         * artistpic : http://qukufile2.qianqian.com/data2/pic/4dc725cd39963fa3f05a9cf38439b554/551080154/551080154.jpg@s_2,w_40,h_40
         * albumid : 67909
         */

        private String albumname;
        private String weight;
        private String artistname;
        private String resource_type_ext;
        private String artistpic;
        private String albumid;

        public String getAlbumname() {
            return albumname;
        }

        public void setAlbumname(String albumname) {
            this.albumname = albumname;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getAlbumid() {
            return albumid;
        }

        public void setAlbumid(String albumid) {
            this.albumid = albumid;
        }
    }
}
