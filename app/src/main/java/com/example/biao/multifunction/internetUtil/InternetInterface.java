package com.example.biao.multifunction.internetUtil;

import com.example.biao.multifunction.model.LaterWeather;
import com.example.biao.multifunction.model.LyricsObjct;
import com.example.biao.multifunction.model.SongNameGetLyrics;
import com.example.biao.multifunction.model.WeatherNow;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by benxiang on 2019/4/10.
 */

public interface InternetInterface {
    @GET("api/lyric/{songname}")
    Observable<SongNameGetLyrics> getLyricsUrl(@Path("songname") String songName);//根据歌名获取歌词URL

    @GET(" ")
    Observable<LyricsObjct> getLyrics();

    @GET("/s6/weather/now")
    Observable<WeatherNow> getNowWeather(@QueryMap Map<String, String> map);//获取now天气

    @GET("/s6/weather/forecast")
    Observable<LaterWeather> getFutureWeather(@QueryMap Map<String, String> map);//获取未来天气
}
