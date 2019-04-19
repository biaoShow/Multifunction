package com.example.biao.multifunction.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.PublicFinalModel;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * 音乐控制服务类
 * Created by biao on 2018/5/4.
 */

public class MusicService extends Service {

    private List<Song> list;
    private SharedPreferencesUtil sharedPreferencesUtil;
    public int playPosition;
    boolean isFrist = true;
    private MediaPlayer mediaPlayer;
    private MusicBinder musicBinder;
    public static final int START = 0;
    public static final int PAUSE = 1;
    public static final int RESUME = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        list = MusicUtils.getMusicData(this);
        sharedPreferencesUtil = SharedPreferencesUtil.getIntent(this);
        playPosition = sharedPreferencesUtil.getInt(PreferencesKep.PLAY_POSITION);
        mediaPlayer = new MediaPlayer();
        musicBinder = new MusicBinder();
        if (list.size() > 0) {
            try {
                mediaPlayer.setDataSource(list.get(0).getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //监听播放是否完成，完成自动播放下一首
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                musicBinder.next();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }


    public class MusicBinder extends Binder {
        /**
         * 播放
         *
         * @param position
         */
        public void play(final int position) {
            Log.i("MusicBinder", position + "");
//            SharedPreferencesUtil.getIntent(MusicService.this).putInt(PreferencesKep.PLAY_POSITION, position);
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
                        sendBroadcast(START);
                        musicBroadcast(sharedPreferencesUtil.getInt(PreferencesKep.PLAY_POSITION), position);
                        savePreferences();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * 暂停或者播放
         */
        public void pause() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                sendBroadcast(PAUSE);
            } else {
                if (isFrist) {
                    play(SharedPreferencesUtil.getIntent(MyApplication.getContext()).getInt(PreferencesKep.PLAY_POSITION));
                } else {
                    mediaPlayer.start();
                    sendBroadcast(START);
                }
            }
        }

        /**
         * 下一首
         */
        public void next() {
            play(getAfterPlayPosition("next"));
        }

        /**
         * 上一首
         */
        public void last() {
            play(getAfterPlayPosition("last"));
        }
//
//        /**
//         * 获取歌曲长度
//         *
//         * @return
//         */
//        public int getPlayDuration() {
//            int rtn = 0;
//            if (mediaPlayer != null) {
//                rtn = list.get(playPosition).getDuration();
//            }
//            return rtn;
//        }


        /**
         * 获取当前歌曲播放进度
         *
         * @return
         */
        public int getPlayCurrentPosition() {
            int rtn = 0;
            if (mediaPlayer != null) {
                rtn = mediaPlayer.getCurrentPosition();
            }

            return rtn;
        }

        /**
         * 拉动进度条时候后设置进度播放
         *
         * @param position
         */
        public void seekTo(int position) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(position);
            }
        }
//
//        /**
//         * 获取播放歌曲名称
//         *
//         * @return
//         */
//        public String getPlaySong() {
//            if (list.size() > 0) {
//                return list.get(playPosition).getSong();
//            }
//            return null;
//        }

//        /**
//         * 获取播放歌曲歌手
//         *
//         * @return
//         */
//        public String getPlaySinger() {
//            return list.get(playPosition).getSinger();
//        }

        /**
         * 判断歌曲是否在播放
         *
         * @return
         */
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public void setList(List<Song> list) {
            setListSong(list);
        }

        public void setPlayPosition(int position) {
            setThisPlayPosition(position);
        }

    }

    /**
     * 设置歌单
     *
     * @param list
     */
    private void setListSong(List<Song> list) {
        this.list = list;
    }

    private void setThisPlayPosition(int position) {
        this.playPosition = position;
    }

    private int getAfterPlayPosition(String type) {
        int playPattern = sharedPreferencesUtil.getInt(PreferencesKep.PLAY_PATTERN);
        int afterPlayPosition = playPosition;
        switch (playPattern) {
            case PublicFinalModel.PLAY_SINGLE:
                break;
            case PublicFinalModel.PLAY_RANDOM:
                afterPlayPosition = new Random().nextInt(list.size());
                break;
            case PublicFinalModel.PLAY_CIRCULATION:
                if (type.equals("next")) {
                    if (afterPlayPosition == (list.size() - 1)) {
                        afterPlayPosition = 0;
                    } else {
                        afterPlayPosition += 1;
                    }
                } else if (type.equals("last")) {
                    if (afterPlayPosition == 0) {
                        afterPlayPosition = (list.size() - 1);
                    } else {
                        afterPlayPosition -= 1;
                    }
                }
                break;
        }
        return afterPlayPosition;
    }

    /**
     * 发送广播
     *
     * @param status
     */
    private void sendBroadcast(int status) {
        Intent intent = new Intent();
        intent.setAction("com.biao.Music_Broadcast");
        intent.putExtra("status", status);
        sendBroadcast(intent);
    }

    private void musicBroadcast(int oldPosition, int position) {
        //发送广播通知SecodActivity UI更新
//        if (!list.get(oldPosition).getSong().equals(sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG))) {
//            oldPosition = MusicUtils.songGetListPosition(list, sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG),
//                    sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION));
//        }
        Intent intent = new Intent("com.example.biao.service.UPDATEUI");
        intent.putExtra("play_position", position);
        intent.putExtra("old_play_position", oldPosition);
        intent.putExtra("playsong", list.get(position));
        sendBroadcast(intent);
    }

    /**
     * 保存播放歌曲的信息
     */
    private void savePreferences() {
        Song song = list.get(playPosition);
        sharedPreferencesUtil.putString(PreferencesKep.PLAY_SONG, song.getSong());
        sharedPreferencesUtil.putString(PreferencesKep.PLAY_SINGER, song.getSinger());
        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_POSITION, playPosition);
        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_DURATION, song.getDuration());
    }
}
