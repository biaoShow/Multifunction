package com.example.biao.multifunction.adapter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.activity.LyricsActivit;
import com.example.biao.multifunction.definedview.SideBar;
import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.GetLocalVieoInfo;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.OnClickMusicCodeItemLisener;
import com.example.biao.multifunction.util.OnClickMusicitemLisener;
import com.example.biao.multifunction.util.SharedPreferencesUtil;

import java.util.List;

/**
 * 音乐播放器recyclerview 适配器
 * Created by biao on 2018/5/3.
 */

public class MusicFragmentRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Song> list;
    private LayoutInflater mLayout;
    private OnClickMusicitemLisener onClickMusicitemLisener;
    private OnClickMusicCodeItemLisener onClickMusicCodeItemLisener;
    private String playSong = "";
    private MusicReceiver musicReceiver;

    public MusicFragmentRVAdapter(Context context, List<Song> list) {
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);

        //注册广播接受器
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.service.UPDATEUI");
        context.registerReceiver(musicReceiver, intentFilter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.music_recyclerview_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ((MyViewHolder) holder).image_music_logo.setImageBitmap((Bitmap) msg.obj);
            }
        };
        ((MyViewHolder) holder).item_mymusic_song.setText(list.get(position).getSong());
        ((MyViewHolder) holder).item_mymusic_singer.setText(list.get(position).getSinger());
        //异步加载视频截图
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = MusicUtils.getAlbumArt(list.get(position).getAlbumID());
                Message message = new Message();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();

        int time = list.get(position).getDuration();
        String strTime = MusicUtils.formatTime(time);
        ((MyViewHolder) holder).item_mymusic_duration.setText(strTime);

        playSong = SharedPreferencesUtil.getIntent(context).getString(PreferencesKep.PLAY_SONG);
        int playDuration = SharedPreferencesUtil.getIntent(context).getInt(PreferencesKep.PLAY_DURATION);
        int playPosition = SharedPreferencesUtil.getIntent(context).getInt(PreferencesKep.PLAY_POSITION);
        if (list.get(position).getSong().equals(playSong) && list.get(position).getDuration() == playDuration && playPosition == position) {
            ((MyViewHolder) holder).item_mymusic_song.setTextColor(context.getResources().getColor(R.color.sidebar_right_select));
            ((MyViewHolder) holder).item_mymusic_singer.setTextColor(context.getResources().getColor(R.color.sidebar_right_select));
            ((MyViewHolder) holder).item_mymusic_duration.setTextColor(context.getResources().getColor(R.color.sidebar_right_select));
        } else {
            ((MyViewHolder) holder).item_mymusic_song.setTextColor(context.getResources().getColor(R.color.item_song));
            ((MyViewHolder) holder).item_mymusic_singer.setTextColor(context.getResources().getColor(R.color.item_singer_and_time));
            ((MyViewHolder) holder).item_mymusic_duration.setTextColor(context.getResources().getColor(R.color.item_singer_and_time));
        }


        ((MyViewHolder) holder).tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMusicitemLisener.onClickItem(position);
            }
        });
        ((MyViewHolder) holder).iv_item_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMusicCodeItemLisener.onClickCodeItem(position);
            }
        });

    }


    //暴露一个设置监听item监听方法
    public void setOnClickMusicitemLisener(OnClickMusicitemLisener onClickMusicitemLisener) {
        this.onClickMusicitemLisener = onClickMusicitemLisener;
    }

    //暴露一个设置监听itemCode的方法
    public void setOnClickMusicCodeItemLisener(OnClickMusicCodeItemLisener onClickMusicCodeItemLisener) {
        this.onClickMusicCodeItemLisener = onClickMusicCodeItemLisener;
    }

    public void setList(List<Song> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_mymusic_song, item_mymusic_singer, item_mymusic_duration, tv_start;
        ImageView iv_item_code, image_music_logo;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_mymusic_song = itemView.findViewById(R.id.item_mymusic_song);
            item_mymusic_singer = itemView.findViewById(R.id.item_mymusic_singer);
            item_mymusic_duration = itemView.findViewById(R.id.item_mymusic_duration);
            tv_start = itemView.findViewById(R.id.tv_start);
            iv_item_code = itemView.findViewById(R.id.iv_item_code);
            image_music_logo = itemView.findViewById(R.id.image_music_logo);
        }
    }

    /**
     * 广播接受器
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int play_position = intent.getIntExtra("play_position", 0);
            int old_play_position = intent.getIntExtra("old_play_position", 0);
            notifyItemChanged(old_play_position);
            notifyItemChanged(play_position);
//            notifyDataSetChanged();
        }
    }

}
