package com.example.biao.multifunction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.OnClickMusicCodeItemLisener;
import com.example.biao.multifunction.util.OnClickMusicitemLisener;

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

    public MusicFragmentRVAdapter(Context context,List<Song> list){
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.music_recyclerview_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder)holder).item_mymusic_song.setText(list.get(position).getSong());
        ((MyViewHolder)holder).item_mymusic_singer.setText(list.get(position).getSinger());
        int time = list.get(position).getDuration();
        String strTime = MusicUtils.formatTime(time);
        ((MyViewHolder)holder).item_mymusic_duration.setText(strTime);

        ((MyViewHolder)holder).tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMusicitemLisener.onClickItem(position);
            }
        });
        ((MyViewHolder)holder).iv_item_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMusicCodeItemLisener.onClickCodeItem(position);
            }
        });

    }

    //暴露一个设置监听item监听方法
    public void setOnClickMusicitemLisener(OnClickMusicitemLisener onClickMusicitemLisener){
        this.onClickMusicitemLisener = onClickMusicitemLisener;
    }
    //暴露一个设置监听itemCode的方法
    public void setOnClickMusicCodeItemLisener(OnClickMusicCodeItemLisener onClickMusicCodeItemLisener){
        this.onClickMusicCodeItemLisener = onClickMusicCodeItemLisener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView item_mymusic_song,item_mymusic_singer,item_mymusic_duration,tv_start;
        ImageView iv_item_code;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_mymusic_song = itemView.findViewById(R.id.item_mymusic_song);
            item_mymusic_singer = itemView.findViewById(R.id.item_mymusic_singer);
            item_mymusic_duration = itemView.findViewById(R.id.item_mymusic_duration);
            tv_start = itemView.findViewById(R.id.tv_start);
            iv_item_code = itemView.findViewById(R.id.iv_item_code);

        }
    }
}
