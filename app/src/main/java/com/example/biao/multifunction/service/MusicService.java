package com.example.biao.multifunction.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.util.MusicUtils;

import java.io.IOException;
import java.util.List;

/**
 *  音乐控制服务类
 * Created by biao on 2018/5/4.
 */

public class MusicService extends Service {

    List<Song> list;
    public int playPosition = 0;
    boolean isFrist = true;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicBinder musicBinder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        list = MusicUtils.list;
        if(list.size()>0){
            try {
                mediaPlayer.setDataSource(list.get(0).getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }



    public class MusicBinder extends Binder{
        public void play(int position) {
            Log.i("MusicBinder",position+"");
            playPosition = position;
            isFrist = false;
            try {
                mediaPlayer.reset();
                //调用方法传进去要播放的音频路径
                mediaPlayer.setDataSource(list.get(position).getPath());
                //异步准备音频资源
                mediaPlayer.prepareAsync();
                //调用mediaPlayer的监听方法，音频准备完毕会响应此方法
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();//开始音频
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            //监听播放是否完成，完成自动播放下一首
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });

            //发送广播通知SecodActivity UI更新
            Intent intent = new Intent("com.example.biao.service.UPDATEUI");
            intent.putExtra("playsong",list.get(position));
            sendBroadcast(intent);
        }
        /**
         * 暂停或者播放
         */
        public void pause() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                if (isFrist) {
                    play(0);
                } else {
                    mediaPlayer.start();
                }

            }
        }

        /**
         * 下一首
         */
        public void next(){
            if(playPosition==(list.size()-1)){
                play(0);
            }else{
                play(playPosition+1);
            }
        }

        /**
         * 上一首
         */
        public void last(){
            if(playPosition==0){
                play(list.size()-1);
            }else{
                play(playPosition-1);
            }
        }

        /**
         * 获取歌曲长度
         * @return
         */
        public int getPlayDuration(){
            int rtn = 0;
            if(mediaPlayer!=null){
                rtn =  mediaPlayer.getDuration();
            }
            return rtn;
        }


        /**
         * 获取当前歌曲播放进度
         * @return
         */
        public int getPlayCurrentPosition(){
            int rtn = 0;
            if (mediaPlayer != null)
            {
                rtn = mediaPlayer.getCurrentPosition();
            }

            return rtn;
        }

        /**
         * 拉动进度条时候后设置进度播放
         * @param position
         */
        public void seekTo(int position)
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.seekTo(position);
            }
        }

        /**
         * 获取播放歌曲名称
         * @return
         */
        public String getPlaySong(){
           return   list.get(playPosition).getSong();
        }

        /**
         * 获取播放歌曲歌手
         * @return
         */
        public String getPlaySinger(){
            return list.get(playPosition).getSinger();
        }

        /**
         * 判断歌曲是否在播放
         * @return
         */
        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }
    }
}
