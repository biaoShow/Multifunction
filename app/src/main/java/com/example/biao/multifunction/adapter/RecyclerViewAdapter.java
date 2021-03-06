package com.example.biao.multifunction.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.model.VideoInfo;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.RecyclerViewItemOnClickListener;

import java.util.List;

/**
 * recyclerview 适配器
 * Created by ZeQiang Fang on 2018/6/8.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater mLayout;
    private List<VideoInfo> list;
    private RecyclerViewItemOnClickListener recyclerViewItemOnClickListener;

    public void setItemOnClick(RecyclerViewItemOnClickListener recyclerViewItemOnClickListener) {
        this.recyclerViewItemOnClickListener = recyclerViewItemOnClickListener;
    }

    public RecyclerViewAdapter(Context context, List<VideoInfo> list) {
        this.context = context;
        this.mLayout = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.video_recyclerview_item, null);
        return new MyViewHolder(view);
    }

    Bitmap bitmap;
    Handler handler;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                ((MyViewHolder) holder).iv_image.setImageBitmap((Bitmap) msg.obj);
//            }
//        };
        ((MyViewHolder) holder).tv_video_name.setText(list.get(position).getName());
        ((MyViewHolder) holder).tv_video_time.setText(MusicUtils.formatTime(list.get(position).getTime()));
//        //异步加载视频截图
//        MyApplication.bgTp.execute(new Runnable() {
//            @Override
//            public void run() {
//                bitmap = GetLocalVieoInfo.getVideoThumbnail(list.get(position).getImagePath());
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                handler.sendMessage(message);
//            }
//        });
//        ((MyViewHolder)holder).iv_image.setImageBitmap(GetLocalVieoInfo.getVideoThumbnail(list.get(position).getImagePath()));
        ((MyViewHolder) holder).relativeLayout.setTag(position);
        ((MyViewHolder) holder).relativeLayout.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            recyclerViewItemOnClickListener.onClickItem((int) view.getTag());
        }
    };

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_video_name;
        TextView tv_video_time;
        ImageView iv_image;
        RelativeLayout relativeLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            tv_video_name = itemView.findViewById(R.id.tv_video_name);
            tv_video_time = itemView.findViewById(R.id.tv_video_time);
            iv_image = itemView.findViewById(R.id.iv_image);
            relativeLayout = itemView.findViewById(R.id.rl_item);
        }
    }
}
