package com.example.biao.multifunction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.biao.multifunction.R;
import java.util.List;

/**
 * 左侧菜单栏recyclerview适配器
 * Created by biao on 2018/5/3.
 */

public class LeftRecylerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> list;
    private LayoutInflater mLayout;

    public LeftRecylerviewAdapter(Context context,List<String> list){
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayout.inflate(R.layout.left_recyclerview_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder)holder).tv_left_itemtext.setText(list.get(position));
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
