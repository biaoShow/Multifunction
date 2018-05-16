package com.example.biao.multifunction.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.util.OnClickWeatherItemListener;

import java.util.List;

/**
 * weather recyclerview 适配器
 * Created by biao on 2018/5/15.
 */

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<String> list;
    private Context context;
    private LayoutInflater mLayout;
    private OnClickWeatherItemListener onClickWeatherItemListener;

    public WeatherRecyclerViewAdapter(Context context,List<String> list){
        this.context = context;
        this.list = list;
        this.mLayout = LayoutInflater.from(context);
    }

    public void setOnClickWeatherItemListener(OnClickWeatherItemListener onClickWeatherItemListener){
        this.onClickWeatherItemListener = onClickWeatherItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         @SuppressLint("InflateParams") View view = mLayout.inflate(R.layout.weather_recyclerview_item,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ((MyViewHolder)holder).tv_weather_item.setText(list.get(position));
        ((MyViewHolder)holder).tv_weather_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickWeatherItemListener.onClickItem(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_weather_item;

        MyViewHolder(View itemView) {
            super(itemView);

            tv_weather_item = itemView.findViewById(R.id.tv_weather_item);
        }
    }
}
