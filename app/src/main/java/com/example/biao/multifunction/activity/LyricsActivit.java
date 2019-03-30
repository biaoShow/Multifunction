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
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.definedview.LrcView;
import com.example.biao.multifunction.model.LyricsJson;
import com.example.biao.multifunction.model.LyricsObjct;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.MusicUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Created by biao on 2018/5/3.
 */

public class LyricsActivit extends BaseActivity implements View.OnClickListener{

    //解析歌词后排序
    public static Comparator timeComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return (((LyricsObjct) o1).time < ((LyricsObjct) o2).time ? -1 : (((LyricsObjct) o1).time == ((LyricsObjct) o2).time ? 0 : 1));
        }
    };
    List<LyricsObjct> lyricsObjcts = new ArrayList<>();//歌词对象（一行歌词封装一个对象）
    private LrcView lv_lyrics;
    protected String splaySong;//播放歌曲名称
    private boolean isCirculation = true;//歌词跟进度条更新判断（activity启动时开始，结束时停止）
    private List<LyricsJson> lyricsList = new ArrayList<>();//获取到下载歌词的网址

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
            splaySong = musicBinder.getPlaySong();
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
                    while (isCirculation){
                        //回到主线程更新ui，网上有说如果不回到主线程应用可能报错，这个与机器有关
                        //本人的没有报错，只是影响另一个activity的OnClickMusicitemLisener方法调用
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sb_lyrics.setProgress(musicBinder.getPlayCurrentPosition());
                                tv_lyrics_playtime.setText(MusicUtils.formatTime(musicBinder.getPlayCurrentPosition()));
                                lv_lyrics.setIndex(lrcIndex());
                            }
                        });
                        try {
                            Thread.sleep(500);
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
        lv_lyrics = findViewById(R.id.lv_lyrics);

        lv_lyrics.setAnimation(AnimationUtils.loadAnimation(LyricsActivit.this,R.anim.animation_lyrics_activity));//歌词加载动画

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
     * 歌曲信息ui更新方法
     */
    private void upDateUI(int duration,String song,String singer){
        sb_lyrics.setMax(duration);
        tv_lyrics_song.setText(song);
        tv_lyrics_singer.setText(singer);
        Log.i("歌词分钟数：",duration+"-----"+MusicUtils.formatTime(duration));
        tv_lyrics_alltime.setText(MusicUtils.formatTime(duration));
        if(musicBinder.isPlaying()){
            iv_lyrics_startandpause.setImageResource(R.mipmap.start);
        }else{
            iv_lyrics_startandpause.setImageResource(R.mipmap.stopt);
        }
        splaySong = song;
        lyricsObjcts.clear();
        updateSongLyrics();
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
        isCirculation = false;
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

    /**
     * 查找本地是否存在歌词文件，不存在则下载并保存
     */
    public void updateSongLyrics(){
        if (!readSDLyrics(splaySong)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveLyrics(splaySong);
                    readSDLyrics(splaySong);

                }
            }).start();
        }
        lv_lyrics.setmLrcList(lyricsObjcts);
    }
    /**
     * 根据时间获取歌词显示的索引值
     *
     * @return 返回高亮的歌词索引值
     */
    public int lrcIndex() {
        int index = 0;
        int currentTime = musicBinder.getPlayCurrentPosition();
        int duration = musicBinder.getPlayDuration();
        if (currentTime < duration) {
            for (int i = 0; i < lyricsObjcts.size(); i++) {
                if (i < lyricsObjcts.size() - 1) {
                    if (currentTime < lyricsObjcts.get(i).time && i == 0) {
                        index = i;
                    }
                    if (currentTime > lyricsObjcts.get(i).time && currentTime < lyricsObjcts.get(i + 1).time) {
                        index = i;
                    }
                }
                if (i == lyricsObjcts.size() - 1 && currentTime > lyricsObjcts.get(i).time) {
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * 解析Json文件
     *
     * @param jsonDate 需要解析的数据源
     */
    private void JSONWithJSON(String jsonDate) {
        Gson gson = new Gson();
        lyricsList = gson.fromJson(jsonDate, new TypeToken<List<LyricsJson>>() {
        }.getType());
    }

    /**
     * 网络获取歌词
     *
     * @param songName 需要回去歌词下载地址的歌名
     */
    private String sendRequest(String songName) {
        String stringLyrics = null;
        //一、先根据歌名获取下载歌词地址
        HttpURLConnection connection;
        try {
            URL url = new URL("http://geci.me/api/lyric/" + songName);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
//            Log.i("XXXXXXXXXX",connection.getResponseCode()+"");
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                InputStream in = connection.getInputStream();
                //对获取到的输入流读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                in.close();
                connection.disconnect();
                String str = builder.toString();
                if (str.contains("[") && str.contains("]")) {
                    String str1 = str.substring(str.indexOf('['), str.indexOf("]") + 1);
                    JSONWithJSON(str1);//引用GSON解析
                }


                //二、根据所获取地址，下载歌词
                if (lyricsList.size() > 0) {
                    URL url1 = new URL(lyricsList.get(0).getLrc());
                    HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                    connection1.setRequestMethod("GET");
                    connection1.setReadTimeout(8000);
                    connection1.setConnectTimeout(8000);
//                Log.i("XXXXXXXXXX",connection.getResponseCode()+"");
                    if (HttpURLConnection.HTTP_OK == connection1.getResponseCode()) {
                        InputStream in1 = connection1.getInputStream();
                        //对获取到的输入流读取
                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
                        StringBuilder builder1 = new StringBuilder();
                        String line1;
                        while ((line1 = reader1.readLine()) != null) {
                            builder1.append(line1 + "\n");
                        }
                        in1.close();
                        connection1.disconnect();
                        stringLyrics = builder1.toString();
                        Log.i("SecondActivity", stringLyrics);
                    }
                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LyricsActivit.this, "网络请求失败！1", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("XXXXXXXXXX", "网络请求失败！2");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("XXXXXXXXXX", "网络请求失败！3");
        }

        return stringLyrics;

    }

    /**
     * 保存歌词到本地SD卡
     *
     * @return 返回是否保存成功
     */
    private void saveLyrics(String song) {
        String stringLyrics = sendRequest(song);
        if (stringLyrics != null) {
            String state = Environment.getExternalStorageState();
            //判断是否为挂载状态
            //未挂载
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LyricsActivit.this, "内存卡未挂载!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                //已挂载
                //获取储存地址
                File floder = new File(Environment.getExternalStorageDirectory(), "URLTest");
                //判断地址是否存在
                if (!floder.exists()) {
                    //不存在，则创建
                    boolean s = floder.mkdirs();
                    if (s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LyricsActivit.this, "创建文件夹成功！", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LyricsActivit.this, "创建文件夹失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                //创建文件名为song.lrc文件
                File file = new File(floder, song + ".lrc");
                FileOutputStream out = null;
                //创建输出流
                try {
                    out = new FileOutputStream(file);
                    InputStream input = new ByteArrayInputStream(stringLyrics.getBytes());
                    byte[] buffer = new byte[4 * 1024];
                    try {
                        while (input.read(buffer) != -1) {
                            out.write(buffer);
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert out != null;
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 查找本地SD卡歌词
     */
    private boolean readSDLyrics(String song) {
        boolean result = false;
        try {
            String sd = Environment.getExternalStorageDirectory() + "/URLTest/";
            String fileName = sd + song + ".lrc";
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String lyrics;
            try {
                //封装歌词（每一行歌词封装一个LyricsObject对象）（由于获取歌词时候返回歌词样式比较多，所以帅选条件比较复杂）
                while ((lyrics = br.readLine()) != null) {
                    if (lyrics.trim().contains("[ti") || lyrics.contains("[ar") || lyrics.contains("[al") || lyrics.contains("[by")) {
                        String content = lyrics.substring(lyrics.indexOf("[") + 1, lyrics.indexOf("]"));
//                        Log.i("readSDLyrics", content);
                        LyricsObjct lyricsObjct = new LyricsObjct(null, 0, content);
                        lyricsObjcts.add(lyricsObjct);
                        result = true;
                    } else if (lyrics.contains("[") && (lyrics.trim().startsWith("[0")||lyrics.trim().startsWith("[1")||
                            lyrics.trim().startsWith("[2")||lyrics.trim().startsWith("[3")||lyrics.trim().startsWith("[4")||
                            lyrics.trim().startsWith("[5"))) {
                        int last = lyrics.lastIndexOf("]");
                        if (lyrics.trim().length() > last + 1) {
                            String content = lyrics.substring(last + 1, lyrics.length());
                            String times = lyrics.substring(lyrics.indexOf("["), last);
                            String timesNew = times.replace("[", "^").replace("]", "^");
                            String[] timeArray = timesNew.split("\\^");
                            for (String str : timeArray) {
                                if (str.trim().length() == 0) {
                                    continue;
                                }
                                LyricsObjct lyricsObjct = new LyricsObjct(str, stringTiemToLongTiemg(str), content);
//                            Log.i("readSDLyrics", str + ":" + content);
                                lyricsObjcts.add(lyricsObjct);
                                result = true;
                            }

                        }


                    }

//                    Log.i("readSDLyrics",lyrics.trim()+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
//            respose_text.setText("本地无此歌词！");
        }
        //把歌词封装后的排序（按时间先后排序）
        Collections.sort(lyricsObjcts, timeComparator);
        return result;
    }

    /**
     * 将时间字符串[00:00.00]转化为long类型数据
     *
     * @param strTime 要转换格式的内容
     * @return 返回转换后long类型数据
     */
    private long stringTiemToLongTiemg(String strTime) {
        long h = 0;
        long m = 0;
        long s = 0;
        if (strTime.contains(".")) {
            strTime = strTime.replace(".", ":");
        }
        String[] times = strTime.split(":");
        for (int i = 0; i < times.length; i++) {
            if (i == 0) {
                h = Long.valueOf(times[0]) * 60 * 1000;
            } else if (i == 1) {
                m = Long.valueOf(times[1]) * 1000;
            } else if (i == 2) {
                s = Long.valueOf(times[2]) * 10;
            }
        }

        return h + m + s;
    }
}
