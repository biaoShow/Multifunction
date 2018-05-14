package com.example.biao.multifunction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.biao.multifunction.R;
import com.example.biao.multifunction.util.OnClickLeftRLItemListener;

import java.util.List;

/**
 * 左侧菜单栏recyclerview适配器
 * Created by biao on 2018/5/3.
 */

public class LeftRecylerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> list;//菜单栏item内容List
    private LayoutInflater mLayout;
    private OnClickLeftRLItemListener leftRLItemListener;//item点击监听

    public LeftRecylerviewAdapter(Context context,List<String> list){
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);
    }

    //暴露设置监听接口方法
    public void setOnClickLeftRLItemListener(OnClickLeftRLItemListener leftRLItemListener){
        this.leftRLItemListener = leftRLItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.left_recyclerview_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder)holder).tv_left_itemtext.setText(list.get(position));
        ((MyViewHolder)holder).tv_left_itemtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRLItemListener.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_left_itemtext;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_left_itemtext = itemView.findViewById(R.id.tv_left_itemtext);

        }
    }


}
