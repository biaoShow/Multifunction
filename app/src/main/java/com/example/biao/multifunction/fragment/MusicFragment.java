package com.example.biao.multifunction.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.activity.LyricsActivit;
import com.example.biao.multifunction.adapter.MusicFragmentRVAdapter;
import com.example.biao.multifunction.definedview.SideBar;
import com.example.biao.multifunction.dialog.MusicCodeDialog;
import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.model.Song;
import com.example.biao.multifunction.service.MusicService;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.OnChooseLetterChangedListener;
import com.example.biao.multifunction.util.OnClickMusicCodeItemLisener;
import com.example.biao.multifunction.util.OnClickMusicitemLisener;
import com.example.biao.multifunction.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 音乐播放功能fragment
 * Created by biao on 2018/5/2.
 */

public class MusicFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    Unbinder unbinder;
    @BindView(R.id.et_search)
    EditText etSearch;
    private List<Song> list;//获取歌单
    private Map<String, Integer> letterMap;//获取歌单
    private MusicFragmentRVAdapter musicFragmentRVAdapter = null;//fragment适配器
    private RecyclerView recyclerView;
    private View civ_music;
    private ImageView iv_play_location;
    private SideBar sideBar;
    private TextView tv_hint;
    private MusicService.MusicBinder musicBinder;//音乐播放service
    private Intent intent;
    private ObjectAnimator objectAnimator;//旋转动画对象
    private RotateBroadcast rotateBroadcast;
    private IntentFilter intentFilter;
    //创建一个服务连接对象
    private ServiceConnection connection;

    private SharedPreferencesUtil sharedPreferencesUtil;
    private List<Song> searchSongList = new ArrayList<>();
    private boolean isfrist = true;//判断是否第一次进入

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = MusicUtils.getMusicData(MyApplication.getContext());
        searchSongList = list;
        letterMap = MusicUtils.getLetterMap(MyApplication.getContext());

        //加载适配器
        musicFragmentRVAdapter = new MusicFragmentRVAdapter(MyApplication.getContext(), list);
        sharedPreferencesUtil = SharedPreferencesUtil.getIntent(getActivity());

        //绑定播放服务
        intent = new Intent(MyApplication.getContext(), MusicService.class);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.music_fragment_layout, container, false);

        //初始化控件
        civ_music = view.findViewById(R.id.civ_music);
        iv_play_location = view.findViewById(R.id.iv_play_location);
        recyclerView = view.findViewById(R.id.musicfragment_recycerview);
        sideBar = (SideBar) view.findViewById(R.id.sideBar);
        tv_hint = view.findViewById(R.id.tv_hint);

        initOA();//初始化旋转动画对象
        initBroadcast();//注册广播

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicBinder = (MusicService.MusicBinder) service;//获取到一个服务对象
                musicBinder.setList(list);
                if (musicBinder.isPlaying()) {
                    objectAnimator.start();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        MyApplication.getContext().startService(intent);
        MyApplication.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(musicFragmentRVAdapter);
        scrollToPosition(MusicUtils.songGetListPosition(list, sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG),
                sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION)), "first");

        iv_play_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToPosition(MusicUtils.songGetListPosition(list, sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG),
                        sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION)), "location");
            }
        });

        sideBar.setOnTouchingLetterChangedListener(new OnChooseLetterChangedListener() {
            @Override
            public void onChooseLetter(String s) {
                if (!tv_hint.isShown()) {
                    tv_hint.setVisibility(View.VISIBLE);
                }
                tv_hint.setText(s);
                selectRecyclerView(s);
            }

            @Override
            public void onNoChooseLetter() {
                tv_hint.setVisibility(View.GONE);
            }
        });

        //设置监听事件
        civ_music.setOnClickListener(this);
        unbinder = ButterKnife.bind(this, view);

        //监听是否获取焦点
        etSearch.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
//                    showOrHide(getContext());
                    show_keyboard_from(getContext(), v);
                } else {
                    // 失去焦点
                    hide_keyboard_from(getContext(), v);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TextChangedListener", "onTextChanged");
                List<Song> searchList = new ArrayList<>();//搜索到的歌单
                List<Song> songList = MusicUtils.getMusicData(getContext());//先刷新一下全部歌单
                String searchChar = s.toString();//输入的内容
                if (!"".equals(searchChar)) {
                    if (songList.size() > 0) {
                        for (Song song : songList) {
                            if (song.getSong().contains(searchChar) || song.getSinger().contains(searchChar)) {
                                searchList.add(song);
                            }
                        }
                    }
                } else {
                    searchList.addAll(songList);
                }
                musicFragmentRVAdapter.setList(searchList);
                musicBinder.setList(searchList);
                searchSongList = searchList;
                sharedPreferencesUtil.putInt(PreferencesKep.PLAY_POSITION, MusicUtils.songGetListPosition(
                        searchList, sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG),
                        sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION)));//歌曲列表改变同时记录播放的Position也要改变
                musicFragmentRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("TextChangedListener", "afterTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("TextChangedListener", "beforeTextChanged");
            }
        });
        return view;
    }

    private void selectRecyclerView(String s) {
        if (s.equals("#")) {
            recyclerView.scrollToPosition(0);
        } else {
            if (letterMap.containsKey(s)) {
                recyclerView.scrollToPosition(letterMap.get(s));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        musicFragmentRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (musicBinder != null) {
            if (musicBinder.isPlaying()) {
                objectAnimator.resume();
            }
        }
        Log.i("MusicFragment", "----------onResume----------");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (musicBinder != null) {
            if (musicBinder.isPlaying()) {
                objectAnimator.pause();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_music:
                if (list.size() > 0) {
                    LyricsActivit.startAction(getActivity());
                } else {
                    Toast.makeText(getActivity(), "暂无歌曲", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rotateBroadcast) {
            getActivity().unregisterReceiver(rotateBroadcast);
        }
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(rotateBroadcast);
        list.clear();
        MyApplication.getContext().unbindService(connection);//解绑服务
        Log.i("MusicFragment", "----------onDestroy----------");
    }

    /**
     * reccleview活动到指定的位置
     */
    public void scrollToPosition(int position, String type) {
        int firstItem, lastItem;
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int toY = -((height - dip2px(getActivity(), 130)) / 2);
        recyclerView.scrollToPosition(position);
        //拿到当前屏幕可见的第一个position跟最后一个postion
        if (type.equals("first") && position < (list.size() - 9)) {
            if (isfrist) {
                recyclerView.smoothScrollBy(0, toY);
                isfrist = false;
            }
        } else if (type.equals("location")) {
            firstItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
            lastItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
            if (lastItem < position || position < firstItem) {
                if (lastItem < position) {
                    toY = -toY;
                }
                recyclerView.smoothScrollBy(0, toY);
            }
        } else if (type.equals("cancel")) {
            lastItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
            if (lastItem == -1 && position < (list.size() - 9)) {
                recyclerView.smoothScrollBy(0, toY);
                return;
            }
            if (position > 9 && position > lastItem) {
                recyclerView.smoothScrollBy(0, -(toY + 200));
            }
        }
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 停止服务方法，提供给其他类控件使用，可以实现停止播放音乐
     */
    public void stopService() {
        MyApplication.getContext().unbindService(connection);
        MyApplication.getContext().stopService(intent);

    }

    /**
     * 显示dialog
     *
     * @param position 歌单List下标
     */
    private void showDialog(int position) {
        MusicCodeDialog selectDialog = new MusicCodeDialog(getActivity(), position, searchSongList);
        selectDialog.show();
        //获取对话框当前的参数值
        WindowManager.LayoutParams p = selectDialog.getWindow().getAttributes();
//        p.height = 900; //高度设置
//        p.width = 950; //宽度设置
        //设置弹出透明底色，解决直角问题
        Window window = selectDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置dialog生效
        selectDialog.getWindow().setAttributes(p);
    }


    /**
     * 初始化旋转动画
     */
    private void initOA() {
        objectAnimator = ObjectAnimator.ofFloat(civ_music, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    }

    /**
     * 注册广播
     */
    private void initBroadcast() {
        intentFilter = new IntentFilter();
        rotateBroadcast = new RotateBroadcast();
        intentFilter.addAction("com.biao.Music_Broadcast");
        getActivity().registerReceiver(rotateBroadcast, intentFilter);
    }

    /**
     * 设置搜索view的显示
     */
    public void setSearchView() {
        llSearch.setVisibility(View.VISIBLE);
        etSearch.setFocusable(true);
        etSearch.setFocusableInTouchMode(true);
        etSearch.requestFocus();
    }

    @OnClick({R.id.tv_search_cancel, R.id.ll_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_cancel:
                llSearch.setVisibility(View.GONE);
                etSearch.setText("");
                list = MusicUtils.getMusicData(getActivity());
                musicFragmentRVAdapter.setList(list);
                musicBinder.setList(list);
                musicFragmentRVAdapter.notifyDataSetChanged();
                int getListPosition = MusicUtils.songGetListPosition(list, sharedPreferencesUtil.getString(PreferencesKep.PLAY_SONG),
                        sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION));
                sharedPreferencesUtil.putInt(PreferencesKep.PLAY_POSITION, getListPosition);
                scrollToPosition(getListPosition, "cancel");
                musicBinder.setPlayPosition(getListPosition);
                break;
            case R.id.ll_search:
                break;
        }
    }

    /**
     * 接收播放暂停或者停止的状态广播
     */
    class RotateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.biao.Music_Broadcast".equals(intent.getAction())) {
                int status = intent.getIntExtra("status", -1);
                switch (status) {
                    case 0:
                        objectAnimator.start();
                        break;
                    case 1:
                        objectAnimator.pause();
                        break;
                    case 2:
                        objectAnimator.resume();
                        break;
                    default:
//                        objectAnimator.end();
                        break;
                }
            }
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param view
     */
    public void show_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    //如果输入法在窗口上已经显示，则隐藏，反之则显示
//    public static void showOrHide(Context context) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//    }

}
