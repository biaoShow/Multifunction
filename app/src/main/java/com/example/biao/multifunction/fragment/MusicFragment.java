package com.example.biao.multifunction.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.biao.multifunction.R;
import com.example.biao.multifunction.activity.LyricsActivit;
import com.example.biao.multifunction.adapter.MusicFragmentRVAdapter;
import com.example.biao.multifunction.dialog.MusicCodeDialog;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.OnClickMusicCodeItemLisener;
import com.example.biao.multifunction.util.OnClickMusicitemLisener;
import java.util.List;

/**
 * 音乐播放功能fragment
 * Created by biao on 2018/5/2.
 */

public class MusicFragment extends Fragment implements View.OnClickListener{

    private List<Song> list = MusicUtils.list;//获取歌单
    private MusicFragmentRVAdapter musicFragmentRVAdapter;//fragment适配器
    private RecyclerView recyclerView;
    private View civ_music;
    private MusicService.MusicBinder musicBinder;//音乐播放service
    private Intent intent;
    private MusicCodeDialog musicCodeDialog;
    //创建一个服务连接对象
    private ServiceConnection connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;//获取到一个服务对象
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment_layout, container, false);

        //初始化控件
        civ_music = view.findViewById(R.id.civ_music);
        recyclerView = view.findViewById(R.id.musicfragment_recycerview);

        //加载适配器
        musicFragmentRVAdapter = new MusicFragmentRVAdapter(MyApplication.getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        recyclerView.setAdapter(musicFragmentRVAdapter);

        //帮你定播放服务
        intent = new Intent(MyApplication.getContext(),MusicService.class);
        MyApplication.getContext().startService(intent);
        MyApplication.getContext().bindService(intent,connection, Context.BIND_AUTO_CREATE);

        //调用适配器方法，重写onClickItem获得点击item，做出相应操作/响应
        musicFragmentRVAdapter.setOnClickMusicitemLisener(new OnClickMusicitemLisener() {
            @Override
            public void onClickItem(int position) {
                musicBinder.play(position);
            }
        });

        musicFragmentRVAdapter.setOnClickMusicCodeItemLisener(new OnClickMusicCodeItemLisener() {
            @Override
            public void onClickCodeItem(int position) {
                showDialog(position);//显示浮层dialog
            }
        });

        //设置监听事件
        civ_music.setOnClickListener(this);

        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civ_music:
                LyricsActivit.startAction(getActivity());
                break;
                default:
                    break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getContext().unbindService(connection);//解绑服务
    }

    /**
     * 停止服务方法，提供给其他类控件使用，可以实现停止播放音乐
     */
    public void stopService(){
        MyApplication.getContext().unbindService(connection);
        MyApplication.getContext().stopService(intent);

    }

    /**
     * 显示dialog
     * @param position
     */
    private void showDialog(int position){
        MusicCodeDialog selectDialog = new MusicCodeDialog(getActivity(),position);
        selectDialog.show();
        //获取对话框当前的参数值
        android.view.WindowManager.LayoutParams p = selectDialog.getWindow().getAttributes();
        p.height = 900; //高度设置
        p.width = 950; //宽度设置
        //设置dialog生效
        selectDialog.getWindow().setAttributes(p);
    }
}
