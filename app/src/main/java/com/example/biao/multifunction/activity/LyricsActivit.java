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
import com.example.biao.multifunction.internetUtil.GetMap;
import com.example.biao.multifunction.internetUtil.RetrofitHelper;
import com.example.biao.multifunction.lrcviewlib.ILrcViewSeekListener;
import com.example.biao.multifunction.lrcviewlib.LrcDataBuilder;
import com.example.biao.multifunction.lrcviewlib.LrcRow;
import com.example.biao.multifunction.lrcviewlib.LrcView;
import com.example.biao.multifunction.model.LyricsBean;
import com.example.biao.multifunction.model.MusciBean;
import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.PublicFinalModel;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.popwindows.MyPopWindows;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.SharedPreferencesUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 歌词页面
 * Created by biao on 2018/5/3.
 */

public class LyricsActivit extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LyricsActivit";
    //    private static final String LRCBASEURL = "http://s.gecimi.com/lrc/";//歌词迷接口
    private static final String LRCBASEURL = "http://tingapi.ting.baidu.com";//歌词迷接口
    private static final String FROM = "android";
    private static final String FORMAT = "json";
    //解析歌词后排序
//    public static Comparator timeComparator = new Comparator() {
//        @Override
//        public int compare(Object o1, Object o2) {
//            return (((LyricsObjct) o1).time < ((LyricsObjct) o2).time ? -1 : (((LyricsObjct) o1).time == ((LyricsObjct) o2).time ? 0 : 1));
//        }
//    };
//    List<LyricsObjct> lyricsObjcts = new ArrayList<>();//歌词对象（一行歌词封装一个对象）
    @BindView(R.id.iv_play_pattern)
    ImageView ivPlayPattern;
    private LrcView lv_lyrics;
    protected String splaySong, splaySongSinger;//播放歌曲名称
    protected int splaySongDuration;//播放歌曲名称
    private boolean isCirculation = true;//歌词跟进度条更新判断（activity启动时开始，结束时停止）

    private List<Song> list = new ArrayList<>();//歌曲对象列表
    private MusicService.MusicBinder musicBinder;//服务对象
    private TextView tv_lyrics_song, tv_lyrics_singer, tv_lyrics_alltime, tv_lyrics_playtime;
    private ImageView iv_lyrics_back, iv_lyrics_startandpause, iv_lyrics_last, iv_lyrics_next;
    private SeekBar sb_lyrics;
    private MusicReceiver musicReceiver;//服务对象、

    private SharedPreferencesUtil sharedPreferencesUtil;
    private MyPopWindows myPopWindows;

//    private LrcView

    //创建一个service连接对象，并定义好连接时所执行的任务（耗时的需要在子线程执行）
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
//            splaySong = musicBinder.getPlaySong();
            splaySong = sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG);
            splaySongSinger = sharedPreferencesUtil.getString(PreferencesKep.PLAY_SINGER);
            splaySongDuration = sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION);
            //歌单非空判断
            if (list.size() == 0) {
                list = MusicUtils.getMusicData(LyricsActivit.this);
            }
            upDateUI(splaySongDuration, splaySong, splaySongSinger);
            //开启子线程持续更新进度条和播放时间(500毫秒更新一次)
            new Thread() {
                @Override
                public void run() {
                    while (isCirculation) {
                        //回到主线程更新ui，网上有说如果不回到主线程应用可能报错，这个与机器有关
                        //本人的没有报错，只是影响另一个activity的OnClickMusicitemLisener方法调用
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sb_lyrics.setProgress(musicBinder.getPlayCurrentPosition());
                                tv_lyrics_playtime.setText(MusicUtils.formatTime(musicBinder.getPlayCurrentPosition()));
                                lv_lyrics.smoothScrollToTime(musicBinder.getPlayCurrentPosition(), false);
//                                lv_lyrics.setIndex(lrcIndex());
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
        ButterKnife.bind(this);

        //沉浸效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }
        initView();
        initData();
        initLrcView();
    }

    private void initView() {
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

        //控件监听
        iv_lyrics_back.setOnClickListener(this);
        iv_lyrics_startandpause.setOnClickListener(this);
        iv_lyrics_last.setOnClickListener(this);
        iv_lyrics_next.setOnClickListener(this);
    }

    private void initData() {
        lv_lyrics.setAnimation(AnimationUtils.loadAnimation(LyricsActivit.this, R.anim.animation_lyrics_activity));//歌词加载动画

        sharedPreferencesUtil = SharedPreferencesUtil.getIntent(this);
        myPopWindows = new MyPopWindows(this);
        switch (sharedPreferencesUtil.getInt(PreferencesKep.PLAY_PATTERN)) {
            case PublicFinalModel.PLAY_CIRCULATION:
                ivPlayPattern.setImageResource(R.mipmap.circulation);
                break;
            case PublicFinalModel.PLAY_RANDOM:
                ivPlayPattern.setImageResource(R.mipmap.random);
                break;
            case PublicFinalModel.PLAY_SINGLE:
                ivPlayPattern.setImageResource(R.mipmap.single);
                break;
        }
        myPopWindows.setOnItemClickListener(new MyPopWindows.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                myPopWindows.dismiss();
                switch (v.getId()) {
                    case R.id.ll_circulation:
                        ivPlayPattern.setImageResource(R.mipmap.circulation);
                        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_PATTERN, PublicFinalModel.PLAY_CIRCULATION);
                        break;
                    case R.id.ll_random:
                        ivPlayPattern.setImageResource(R.mipmap.random);
                        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_PATTERN, PublicFinalModel.PLAY_RANDOM);
                        break;
                    case R.id.ll_single:
                        ivPlayPattern.setImageResource(R.mipmap.single);
                        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_PATTERN, PublicFinalModel.PLAY_SINGLE);
                        break;
                }
            }
        });

        //绑定MusicService
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //注册广播接受器
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.service.UPDATEUI");
        registerReceiver(musicReceiver, intentFilter);
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
    }

    /**
     * 歌曲信息ui更新方法
     */
    private void upDateUI(int duration, String song, String singer) {
        sb_lyrics.setMax(duration);
        tv_lyrics_song.setText(song);
        tv_lyrics_singer.setText(singer);
        Log.i("歌词分钟数：", duration + "-----" + MusicUtils.formatTime(duration));
        tv_lyrics_alltime.setText(MusicUtils.formatTime(duration));
        if (musicBinder.isPlaying()) {
            iv_lyrics_startandpause.setImageResource(R.mipmap.stopt);
        } else {
            iv_lyrics_startandpause.setImageResource(R.mipmap.start);
        }
        splaySong = song;
        lv_lyrics.setLrcData(null);
        lv_lyrics.setNoDataMessage("加载中...");
//        lyricsObjcts.clear();
        updateSongLyrics();
    }


    /**
     * 提供跳转到此activity方法
     *
     * @param context 跳转到此activity需要提供的参数
     */
    public static void startAction(Context context) {
        Intent intent = new Intent(context, LyricsActivit.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_lyrics_back:
                finish();
                break;
            case R.id.iv_lyrics_startandpause:
                if (musicBinder.isPlaying()) {
                    iv_lyrics_startandpause.setImageResource(R.mipmap.start);
                } else {
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
    protected void onResume() {
        super.onResume();
        if (lv_lyrics != null && null != musicBinder) {
            lv_lyrics.smoothScrollToTime(musicBinder.getPlayCurrentPosition(), true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(musicReceiver);
        isCirculation = false;
    }

    @OnClick(R.id.iv_play_pattern)
    public void onViewClickedLrc(View view) {
        myPopWindows.showMyPopWindows(view);
//        myPopWindows.showAtLocation(view,
//                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 广播接受器
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Song playSong = (Song) intent.getSerializableExtra("playsong");
            upDateUI(playSong.getDuration(), playSong.getSong(), playSong.getSinger());
//            lv_lyrics.setNoDataMessage("加载中...");
        }
    }

    /**
     * 初始化歌词
     */
    private void initLrcView() {
        //init the lrcView
        lv_lyrics.getLrcSetting()
                .setTimeTextSize(30)//时间字体大小
                .setSelectLineColor(Color.parseColor("#ffffff"))//选中线颜色
                .setSelectLineTextSize(25)//选中线大小
                .setHeightRowColor(Color.parseColor("#ccFFFF00"))//高亮字体颜色
                .setNormalRowTextSize(50)//正常行字体大小
                .setHeightLightRowTextSize(50)//高亮行字体大小
                .setTrySelectRowTextSize(50)//尝试选中行字体大小
                .setTimeTextColor(Color.parseColor("#ffffff"))//时间字体颜色
                .setTrySelectRowColor(Color.parseColor("#55ffffff"));//尝试选中字体颜色
        lv_lyrics.commitLrcSettings();//提交设置

        lv_lyrics.setLrcViewSeekListener(new ILrcViewSeekListener() {
            @Override
            public void onSeek(LrcRow currentLrcRow, long CurrentSelectedRowTime) {
                //在这里执行播放器控制器控制播放器跳转到指定时间
                musicBinder.seekTo((int) CurrentSelectedRowTime);
                //播放器播放时，时间更新后调用这个时歌词数据更新到当前对应的歌词，播放器一般时间更新以秒为频率更新
                lv_lyrics.smoothScrollToTime(CurrentSelectedRowTime, false);//传递的数据是播放器的时间格式转化为long数据
            }
        });
    }

    /**
     * 查找本地是否存在歌词文件，不存在则下载并保存
     */
    public void updateSongLyrics() {
        if (!readSDLyrics(splaySong)) {
            sendRequest(splaySong.trim(), splaySongSinger.trim());
        }
//        lv_lyrics.setmLrcList(lyricsObjcts);
    }

//    /**
//     * 根据时间获取歌词显示的索引值
//     *
//     * @return 返回高亮的歌词索引值
//     */
//    public int lrcIndex() {
//        int index = 0;
//        int currentTime = musicBinder.getPlayCurrentPosition();
//        int duration = musicBinder.getPlayDuration();
//        if (currentTime < duration) {
//            for (int i = 0; i < lyricsObjcts.size(); i++) {
//                if (i < lyricsObjcts.size() - 1) {
//                    if (currentTime < lyricsObjcts.get(i).time && i == 0) {
//                        index = i;
//                    }
//                    if (currentTime > lyricsObjcts.get(i).time && currentTime < lyricsObjcts.get(i + 1).time) {
//                        index = i;
//                    }
//                }
//                if (i == lyricsObjcts.size() - 1 && currentTime > lyricsObjcts.get(i).time) {
//                    index = i;
//                }
//            }
//        }
//        return index;
//    }


    /**
     * 网络获取歌词
     *
     * @param songName 需要回去歌词下载地址的歌名
     */
    private void sendRequest(final String songName, final String songSinger) {
        //一、先根据歌名获取下载歌词地址
//        RetrofitHelper.getInstance(null).getInternetInterface()
//                .getLyricsUrl(songName)
//                .subscribeOn(Schedulers.io())//请求在io线程
//                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
//                .subscribe(new DefaultObserver<SongNameGetLyrics>() {
//                    @Override
//                    protected void onStart() {
//                        super.onStart();
////                        showLoadingDialog();
//                    }
//
//                    @Override
//                    public void onNext(SongNameGetLyrics songNameGetLyrics) {
//                        if (songNameGetLyrics.getCount() > 0) {
//                            getLyrics(songNameGetLyrics.getResult().get(0).getLrc(), songName);
//                        } else {
//                            Log.e("onNext", "暂无歌词");
//                            lv_lyrics.setNoDataMessage("暂无歌词");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
////                        hideLoadingDialog();
//                        lv_lyrics.setNoDataMessage("暂无歌词");
//                        Log.e("onError", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
////                        hideLoadingDialog();
//                    }
//                });
//    }
        RetrofitHelper.getInstance(LRCBASEURL).getInternetInterface()
                .getMusciDetails(GetMap.getMusciDetails(FROM,
                        "baidu.ting.search.catalogSug", FORMAT, songName))
                .subscribeOn(Schedulers.io())//请求在io线程
                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
                .subscribe(new DefaultObserver<MusciBean>() {

                    @Override
                    public void onNext(MusciBean musciBean) {
                        List<MusciBean.SongBean> songBeans = musciBean.getSong();
                        if (songBeans != null && songBeans.size() > 0) {
                            for (MusciBean.SongBean s : songBeans) {
                                if (s.getSongname().equals(songName) && (s.getSongname().equals(songSinger)
                                        || s.getArtistname().contains(songSinger) || songSinger.contains(s.getArtistname()))) {
                                    getLyrics(s.getSongid(), songName);
                                    return;
                                }
                            }
                            for (MusciBean.SongBean s : songBeans) {
                                if (s.getSongname().equals(songName)) {
                                    getLyrics(s.getSongid(), songName);
                                    return;
                                }
                            }
                            getLyrics(songBeans.get(0).getSongid(), songName);
                        } else {
                            Log.e("onNext", "暂无歌词");
                            lv_lyrics.setNoDataMessage("暂无歌词");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        lv_lyrics.setNoDataMessage("暂无歌词");
                        Log.e("onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
//
//        //二、根据所获取地址，下载歌词
//        if (lyricsList.size() > 0) {
//            URL url1 = new URL(lyricsList.get(0).getLrc());
//            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
//            connection1.setRequestMethod("GET");
//            connection1.setReadTimeout(8000);
//            connection1.setConnectTimeout(8000);
////                Log.i("XXXXXXXXXX",connection.getResponseCode()+"");
//            if (HttpURLConnection.HTTP_OK == connection1.getResponseCode()) {
//                InputStream in1 = connection1.getInputStream();
//                //对获取到的输入流读取
//                BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
//                StringBuilder builder1 = new StringBuilder();
//                String line1;
//                while ((line1 = reader1.readLine()) != null) {
//                    builder1.append(line1 + "\n");
//                }
//                in1.close();
//                connection1.disconnect();
//                stringLyrics = builder1.toString();
//                Log.i("SecondActivity", stringLyrics);
//            }
//        }
//
//    } else
//
//    {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(LyricsActivit.this, "网络请求失败！1", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//} catch(MalformedURLException e){
//        e.printStackTrace();
//        Log.i("XXXXXXXXXX","网络请求失败！2");
//        }catch(IOException e){
//        e.printStackTrace();
//        Log.i("XXXXXXXXXX","网络请求失败！3");
//        }
//
//        return stringLyrics;
//
//}

    /**
     * 根据URL获取歌词，因为返回不是json数据，只能做特殊处理
     *
     * @return
     */
    private void getLyrics(final String songId, final String songName) {
        RetrofitHelper.getInstance(LRCBASEURL).getInternetInterface()
                .getLyrics(GetMap.getLyrics(FROM, "baidu.ting.song.lry", FORMAT, songId))
                .subscribeOn(Schedulers.io())//请求在io线程
                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
                .subscribe(new DefaultObserver<LyricsBean>() {
                    @Override
                    public void onNext(LyricsBean lyricsBean) {
//                        try {
//                            InputStream in1 = responseBody.byteStream();
//                            //对获取到的输入流读取
//                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
//                            StringBuilder builder1 = new StringBuilder();
//                            String line1;
//                            while ((line1 = reader1.readLine()) != null) {
//                                builder1.append(line1 + "\n");
//                            }
//                            in1.close();
//                            saveLyrics(builder1.toString(), songName);
                        if (null != lyricsBean && lyricsBean.getLrcContent() != null) {
                            saveLyrics(lyricsBean.getLrcContent(), songName);
                            Log.i("SecondActivity", lyricsBean.getLrcContent());
                        } else {
                            lv_lyrics.setNoDataMessage("暂无歌词");
                        }
//                        } catch (IOException e) {
//                            lv_lyrics.setNoDataMessage("暂无歌词");
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getLyrics_onError:" + e.getMessage());
                        lv_lyrics.setNoDataMessage("暂无歌词");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        String url1 = url.substring(url.indexOf("lrc/") + 4);
//        RetrofitHelper.getInstance(LRCBASEURL).getInternetInterface()
//                .downloadFile(url1)
//                .subscribeOn(Schedulers.io())//请求在io线程
//                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
//                .subscribe(new DefaultObserver<ResponseBody>() {
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            InputStream in1 = responseBody.byteStream();
//                            //对获取到的输入流读取
//                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
//                            StringBuilder builder1 = new StringBuilder();
//                            String line1;
//                            while ((line1 = reader1.readLine()) != null) {
//                                builder1.append(line1 + "\n");
//                            }
//                            in1.close();
//                            saveLyrics(builder1.toString(), songName);
//                            Log.i("SecondActivity", builder1.toString());
//                        } catch (IOException e) {
//                            lv_lyrics.setNoDataMessage("暂无歌词");
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "getLyrics_onError:" + e.getMessage());
//                        lv_lyrics.setNoDataMessage("暂无歌词");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url1 = new URL(url);
//                    HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
//                    connection1.setRequestMethod("GET");
//                    connection1.setReadTimeout(8000);
//                    connection1.setConnectTimeout(8000);
////                Log.i("XXXXXXXXXX",connection.getResponseCode()+"");
//                    if (HttpURLConnection.HTTP_OK == connection1.getResponseCode()) {
//                        InputStream in1 = connection1.getInputStream();
//                        //对获取到的输入流读取
//                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
//                        StringBuilder builder1 = new StringBuilder();
//                        String line1;
//                        while ((line1 = reader1.readLine()) != null) {
//                            builder1.append(line1 + "\n");
//                        }
//                        in1.close();
//                        connection1.disconnect();
//                        saveLyrics(builder1.toString(), songName);
//                        Log.i("SecondActivity", builder1.toString());
//                    }
//                } catch (IOException e) {
//                    lv_lyrics.setNoDataMessage("暂无歌词");
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    /**
     * 保存歌词到本地SD卡
     *
     * @return 返回是否保存成功
     */

    private void saveLyrics(String stringLyrics, String song) {
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
                File floder = new File(Environment.getExternalStorageDirectory(), "MusicLyrics");
                //判断地址是否存在
                if (!floder.exists()) {
                    //不存在，则创建
                    boolean s = floder.mkdirs();
                    if (s) {
                        Log.i("LyricsActivity", "创建文件夹成功！");
                    } else {
                        Log.e("LyricsActivity", "创建文件夹失败！");
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
                        readSDLyrics(splaySong);
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
//        boolean result = false;
//        try {
        String sd = Environment.getExternalStorageDirectory() + "/MusicLyrics/";
        String fileName = sd + song + ".lrc";
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String lyrics;

        List<LrcRow> lrcRows = new LrcDataBuilder().Build(file);
        if (lrcRows.size() <= 0) {
            lv_lyrics.setNoDataMessage("暂无歌词");
        }
        lv_lyrics.setLrcData(lrcRows);
//            try {
//                //封装歌词（每一行歌词封装一个LyricsObject对象）（由于获取歌词时候返回歌词样式比较多，所以筛选条件比较复杂）
//                while ((lyrics = br.readLine()) != null) {
//                    if (lyrics.trim().contains("[ti") || lyrics.contains("[ar") || lyrics.contains("[al") || lyrics.contains("[by")) {
//                        String content = lyrics.substring(lyrics.indexOf("[") + 1, lyrics.indexOf("]"));
////                        Log.i("readSDLyrics", content);
//                        LyricsObjct lyricsObjct = new LyricsObjct(null, 0, content);
//                        lyricsObjcts.add(lyricsObjct);
//                        result = true;
//                    } else if (lyrics.contains("[") && (lyrics.trim().startsWith("[0") || lyrics.trim().startsWith("[1") ||
//                            lyrics.trim().startsWith("[2") || lyrics.trim().startsWith("[3") || lyrics.trim().startsWith("[4") ||
//                            lyrics.trim().startsWith("[5"))) {
//                        int last = lyrics.lastIndexOf("]");
//                        if (lyrics.trim().length() > last + 1) {
//                            String content = lyrics.substring(last + 1, lyrics.length());
//                            String times = lyrics.substring(lyrics.indexOf("["), last);
//                            String timesNew = times.replace("[", "^").replace("]", "^");
//                            String[] timeArray = timesNew.split("\\^");
//                            for (String str : timeArray) {
//                                if (str.trim().length() == 0) {
//                                    continue;
//                                }
//                                LyricsObjct lyricsObjct = new LyricsObjct(str, stringTiemToLongTiemg(str), content);
////                            Log.i("readSDLyrics", str + ":" + content);
//                                lyricsObjcts.add(lyricsObjct);
//                                result = true;
//                            }
//
//                        }
//
//
//                    }
//
////                    Log.i("readSDLyrics",lyrics.trim()+"\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = false;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            result = false;
////            respose_text.setText("本地无此歌词！");
//        }
//        //把歌词封装后的排序（按时间先后排序）
//        Collections.sort(lyricsObjcts, timeComparator);
        return true;
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
            if (i == 0 && isTime(times[0])) {
                h = Long.valueOf(times[0]) * 60 * 1000;
            } else if (i == 1 && isTime(times[1])) {
                m = Long.valueOf(times[1]) * 1000;
            } else if (i == 2 && isTime(times[2])) {
                s = Long.valueOf(times[2]) * 10;
            }
        }
        return h + m + s;
    }

    private boolean isTime(String s) {
        String s1 = s.substring(0, 1);
        if (s1.equals("0") || s1.equals("1") || s1.equals("2") || s1.equals("3") || s1.equals("4") || s1.equals("5") ||
                s1.equals("6") || s1.equals("7") || s1.equals("8") || s1.equals("9")) {
            return true;
        } else {
            return false;
        }
    }
}

