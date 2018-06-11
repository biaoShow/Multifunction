package com.example.biao.multifunction.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.activity.PlayActivity;
import com.example.biao.multifunction.adapter.RecyclerViewAdapter;
import com.example.biao.multifunction.model.VideoInfo;
import com.example.biao.multifunction.util.GetLocalVieoInfo;
import com.example.biao.multifunction.util.RecyclerViewItemOnClickListener;

import java.util.List;

/**
 * 视频播放功能
 * Created by biao on 2018/5/2.
 */

public class VideoFragment extends Fragment {

    private RecyclerView rv_Video_line;
    private List<VideoInfo> list;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment_layout,container,false);

        list = GetLocalVieoInfo.getVideoFromSDCard(getActivity());
        rv_Video_line = view.findViewById(R.id.rv_Video_line);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(),list);
        rv_Video_line.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_Video_line.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setItemOnClick(new RecyclerViewItemOnClickListener() {
            @Override
            public void onClickItem(int position) {
                Intent intent = new Intent(getActivity(),PlayActivity.class);
                intent.putExtra("playPath",list.get(position).getPath());
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
