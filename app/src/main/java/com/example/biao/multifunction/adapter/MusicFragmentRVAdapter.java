package com.example.biao.multifunction.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.util.MusicUtils;
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
    private String playSongId = "";
    int playColor;
    int normalColor;
    private MusicReceiver musicReceiver;
    private Bitmap bitmap;
//    private RequestOptions options = new RequestOptions()
//            .placeholder(R.mipmap.music_logo)    //加载成功之前占位图
//            .error(R.mipmap.ic_launcher)    //加载错误之后的错误图
//            .override(100, 100)    //指定图片的尺寸
//            .fitCenter()   //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。是指其中一个满足即可不会一定铺满imageview）
//            .centerCrop()//指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的宽高都,大于等于ImageView的宽度，然后截取中间的显示。）
//            .skipMemoryCache(true)    //不使用内存缓存
//            .diskCacheStrategy(DiskCacheStrategy.ALL)    //缓存所有版本的图像
//            .diskCacheStrategy(DiskCacheStrategy.NONE)    //不使用硬盘本地缓存
//            .diskCacheStrategy(DiskCacheStrategy.DATA)    //只缓存原来分辨率的图片
//            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)    //只缓存最终的图片
//            ;

    public MusicFragmentRVAdapter(Context context, List<Song> list) {
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);

        //注册广播接受器
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.biao.service.UPDATEUI");
        context.registerReceiver(musicReceiver, intentFilter);
        playColor = context.getResources().getColor(R.color.sidebar_right_select);
        normalColor = context.getResources().getColor(R.color.item_song);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.music_recyclerview_item, null);
        return new MyViewHolder(view);
    }

    Handler handler = new Handler();

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        mHolder.item_mymusic_song.setText(list.get(position).getSong());
        mHolder.item_mymusic_singer.setText(list.get(position).getSinger());
//        mHolder.image_music_logo.setImageResource(R.mipmap.music_logo);
//        mHolder.image_music_logo.setTag(position);
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (position == (int) mHolder.image_music_logo.getTag()) {
////                    Glide.with(context).load((Bitmap) msg.obj).into(((MyViewHolder) holder).image_music_logo);
//                    mHolder.image_music_logo.setImageBitmap((Bitmap) msg.obj);
//                }
//            }
//        };

//        new AsyncTask() {
//            @Override
//            protected Bitmap doInBackground(Object[] objects) {
//                Bitmap bitmap = MusicUtils.setArtwork(list.get(position).getPath());
//                return bitmap;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                if (position == (int) ((MyViewHolder) holder).image_music_logo.getTag()) {
//                    ((MyViewHolder) holder).image_music_logo.setImageBitmap((Bitmap) o);
//                }
//            }
//
//        }.execute();
//        Glide.with(context).load(MusicUtils.setArtwork(list.get(position).getPath()))
//                .into(((MyViewHolder) holder).image_music_logo);
//        Glide.with(context).load(MusicUtils.getAlbumArt(list.get(position).getAlbumID()))
//                .into(((MyViewHolder) holder).image_music_logo);
//        Glide.with(context).load(MusicUtils.setArtwork(list.get(position).getPath()))
//                .apply(options) //加载成功前显示的图片
//                .into(((MyViewHolder) holder).image_music_logo);//在RequestBuilder 中使用自定义的ImageViewTarget

        //耗时任务子线程执行
//        MyApplication.bgTp.execute(new Runnable() {
//            @Override
//            public void run() {
//                bitmap = MusicUtils.setArtwork(list.get(position).getPath());
//                if (bitmap != null) {
//                    Message message = Message.obtain();
//                    message.obj = bitmap;
//                    handler.sendMessage(message);
//                }
//            }
//        });


        String strTime = MusicUtils.formatTime(list.get(position).getDuration());
        mHolder.item_mymusic_duration.setText(strTime);

//        playSong = SharedPreferencesUtil.getIntent(context).getString(PreferencesKep.PLAY_SONG);
        playSongId = SharedPreferencesUtil.getIntent(context).getString(PreferencesKep.PLAY_SONG_ID);
//        int playDuration = SharedPreferencesUtil.getIntent(context).getInt(PreferencesKep.PLAY_DURATION);
//        int playPosition = SharedPreferencesUtil.getIntent(context).getInt(PreferencesKep.PLAY_POSITION);
//        if (list.get(position).getSong().equals(playSong) && list.get(position).getDuration() == playDuration && playPosition == position) {
        if (list.get(position).getSongId().equals(playSongId)) {
            mHolder.item_mymusic_song.setTextColor(playColor);
            mHolder.item_mymusic_singer.setTextColor(playColor);
            mHolder.item_mymusic_duration.setTextColor(playColor);
        } else {
            mHolder.item_mymusic_song.setTextColor(normalColor);
            mHolder.item_mymusic_singer.setTextColor(normalColor);
            mHolder.item_mymusic_duration.setTextColor(normalColor);
        }
        mHolder.tv_start.setTag(position);
        mHolder.tv_start.setOnClickListener(onClickListener);
//        mHolder.tv_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickMusicitemLisener.onClickItem(position);
//            }
//        });
        mHolder.iv_item_code.setTag(position);
        mHolder.iv_item_code.setOnClickListener(onClickListener);
//        mHolder.iv_item_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickMusicCodeItemLisener.onClickCodeItem(position);
//            }
//        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof TextView) {
                onClickMusicitemLisener.onClickItem((int) view.getTag());
            } else {
                onClickMusicCodeItemLisener.onClickCodeItem((int) view.getTag());
            }
        }
    };


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
