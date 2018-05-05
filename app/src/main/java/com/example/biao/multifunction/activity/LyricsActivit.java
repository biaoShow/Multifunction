package com.example.biao.multifunction.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.MusicUtils;
import java.util.List;

/**
 *
 * Created by biao on 2018/5/3.
 */

public class LyricsActivit extends AppCompatActivity implements View.OnClickListener{

    private List<Song> list = MusicUtils.list;//歌曲对象列表
    private MusicService.MusicBinder musicBinder;//服务对象
    private TextView tv_lyrics_song,tv_lyrics_singer,tv_lyrics_alltime,tv_lyrics_playtime;
    private ImageView iv_lyrics_back,iv_lyrics_startandpause,iv_lyrics_last,iv_lyrics_next;
    private SeekBar sb_lyrics;
    private MusicReceiver musicReceiver;//服务对象
    //创建一个service连接对象，并定义好连接时所执行的任务（耗时的需要在子线程执行）
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            //歌单非空判断
            if(list.size()>0){
                upDateUI(musicBinder.getPlayDuration(),musicBinder.getPlaySong(),musicBinder.getPlaySinger());
            }
            //判断绑定后歌曲是否真在播放，且做出相关ui更新
            if(!musicBinder.isPlaying()){
                iv_lyrics_startandpause.setImageResource(R.mipmap.start);
            }else{
                iv_lyrics_startandpause.setImageResource(R.mipmap.stopt);
            }
            //开启子线程持续更新进度条和播放时间(一秒更新一次)
            new Thread(){
                @Override
                public void run() {
                    while (true){
                        //回到主线程更新ui，网上有说如果不回到主线程应用可能报错，这个与机器有关
                        //本人的没有报错，只是影响另一个activity的OnClickMusicitemLisener方法调用
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sb_lyrics.setProgress(musicBinder.getPlayCurrentPosition());
                                tv_lyrics_playtime.setText(MusicUtils.formatTime(musicBinder.getPlayCurrentPosition()));
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_lyrics_layout);

        //沉浸效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }

        //初始化控件
        tv_lyrics_song = findViewById(R.id.tv_lyrics_song);
        tv_lyrics_singer = findViewById(R.id.tv_lyrics_singer);
        iv_lyrics_back = findViewById(R.id.iv_lyrics_back);
        tv_lyrics_alltime = findViewById(R.id.tv_lyrics_alltime);
        tv_lyrics_playtime = findViewById(R.id.tv_lyrics_playtime);
        iv_lyrics_startandpause = findViewById(R.id.iv_lyrics_startandpause);
        sb_lyrics = findViewById(R.id.sb_lyrics);
        iv_lyrics_last = findViewById(R.id.iv_lyrics_last);
        iv_lyrics_next = findViewById(R.id.iv_lyrics_next);

        //绑定MusicService
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

        //注册广播接受器
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.service.UPDATEUI");
        registerReceiver(musicReceiver,intentFilter);

        //进度条监听
        sb_lyrics.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                musicBinder.seekTo(progress);
            }
        });

        //控件监听
        iv_lyrics_back.setOnClickListener(this);
        iv_lyrics_startandpause.setOnClickListener(this);
        iv_lyrics_last.setOnClickListener(this);
        iv_lyrics_next.setOnClickListener(this);

    }

    /**
     * ui更新方法
     */
    private void upDateUI(int duration,String song,String singer){
        sb_lyrics.setMax(duration);
        tv_lyrics_song.setText(song);
        tv_lyrics_singer.setText(singer);
        tv_lyrics_alltime.setText(MusicUtils.formatTime(duration));
        if(musicBinder.isPlaying()){
            iv_lyrics_startandpause.setImageResource(R.mipmap.start);
        }else{
            iv_lyrics_startandpause.setImageResource(R.mipmap.stopt);
        }
    }



    /**
     * 提供跳转到此activity方法
     * @param context 跳转到此activity需要提供的参数
     */
    public static void startAction(Context context){
        Intent intent = new Intent(context,LyricsActivit.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_lyrics_back:
                finish();
                break;
            case R.id.iv_lyrics_startandpause:
                if(musicBinder.isPlaying()){
                    iv_lyrics_startandpause.setImageResource(R.mipmap.start);
                }else{
                    iv_lyrics_startandpause.setImageResource(R.mipmap.stopt);
                }
                musicBinder.pause();
                break;
            case R.id.iv_lyrics_last:
                musicBinder.last();
                break;
            case R.id.iv_lyrics_next:
                musicBinder.next();
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(musicReceiver);
    }

    /**
     * 广播接受器
     */
    public class MusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Song playSong = (Song) intent.getSerializableExtra("playsong");
            upDateUI(playSong.getDuration(),playSong.getSong(),playSong.getSinger());
        }
    }
}
