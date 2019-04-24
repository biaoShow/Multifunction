package com.example.biao.multifunction.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.adapter.FragmentAdapter;
import com.example.biao.multifunction.adapter.LeftRecylerviewAdapter;
import com.example.biao.multifunction.definedview.CircleImageView;
import com.example.biao.multifunction.definedview.MyView;
import com.example.biao.multifunction.fragment.MusicFragment;
import com.example.biao.multifunction.fragment.NavigationFragment;
import com.example.biao.multifunction.fragment.VideoFragment;
import com.example.biao.multifunction.fragment.WeatherFragment;
import com.example.biao.multifunction.model.PreferencesKep;
import com.example.biao.multifunction.util.ActivityCollecter;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.OnClickLeftRLItemListener;
import com.example.biao.multifunction.util.SharedPreferencesUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.civ_portrait)
    CircleImageView civPortrait;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_weather_search)
    ImageView ivWeatherSearch;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.mv_music)
    MyView mvMusic;
    @BindView(R.id.mv_video)
    MyView mvVideo;
    @BindView(R.id.mv_navigation)
    MyView mvNavigation;
    @BindView(R.id.mv_weather)
    MyView mvWeather;

    private SlidingMenu mSlidingMenu;//第三方库SlidingMenu对象
    private FragmentAdapter fragmentAdapter;//fragment 适配器
    private MusicFragment musicFragment;
    private VideoFragment videoFragment;
    private WeatherFragment weatherFragment;
    private NavigationFragment navigationFragment;
    private ArrayList<Fragment> fragments = new ArrayList<>();//fragment 集合
    private ArrayList<MyView> myViews = new ArrayList<>();//自定义MyView类型控件数组
    private boolean isFirst = true;//判断是否第一次进入

    //菜单栏对象和控件
    private RecyclerView left_recyclerview;
    private LeftRecylerviewAdapter leftRecylerviewAdapter;
    private List<String> list = new ArrayList<>();
    private TextView tv_back;

    private int nowType = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTranslucentStatus();//设置状态栏为透明
        setBehindContentView(R.layout.left_menu);
        ActivityCollecter.addActivity(this);

        applyForPermission();
        initData();
    }

    private void initData() {
        //创建SlidingMenu对象和配置
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setBehindWidth(800);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setMenu(R.layout.left_menu);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setOffsetFadeDegree(0.4f);//左划时剩余部分变暗

        setList();//初始化泛型list数组

        //将fragment添加到适配器
        myViews.add(mvMusic);
        myViews.add(mvVideo);
        myViews.add(mvNavigation);
        myViews.add(mvWeather);
        musicFragment = new MusicFragment();
        videoFragment = new VideoFragment();
        navigationFragment = new NavigationFragment();
        weatherFragment = new WeatherFragment();
        fragments.add(musicFragment);
        fragments.add(videoFragment);
        fragments.add(navigationFragment);
        fragments.add(weatherFragment);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

        viewPager.setOnPageChangeListener(this);

        if (isFirst) {
            viewPager.setAdapter(fragmentAdapter);
            setMenuSelector(0);
            isFirst = false;
        }

        //左菜单栏控件初始化
        View view = mSlidingMenu.getMenu();//获取左拉菜单栏view对象
        tv_back = view.findViewById(R.id.tv_back);

        tv_back.setOnClickListener(this);
        left_recyclerview = view.findViewById(R.id.left_recyclerview);
        leftRecylerviewAdapter = new LeftRecylerviewAdapter(this, list);
        left_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        left_recyclerview.setAdapter(leftRecylerviewAdapter);

        leftRecylerviewAdapter.setOnClickLeftRLItemListener(new OnClickLeftRLItemListener() {
            @Override
            public void onClickItem(int position) {
                if (1 < position && position < 6) {
                    setMenuSelector(position - 2);
                    mSlidingMenu.toggle();
                } else {
                    Toast.makeText(MainActivity.this, "功能暂未实现", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * android 6.0及以上权限申请
     */
    private void applyForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)  //可写
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MyApplication.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_FINE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }


    /**
     * 设置状态栏
     */
    private void initTranslucentStatus() {
        //4.4 全透明状态栏（有的机子是过渡形式的透明）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //5.0 全透明实现
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);// calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MyApplication.getContext(), "您已拒绝了权限！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollecter.removeActivity(this);
        //退出前要把position修改为所有歌单所在位置
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getIntent(this);
        sharedPreferencesUtil.putInt(PreferencesKep.PLAY_POSITION, MusicUtils.songGetListPosition(
                MusicUtils.getMusicData(this), sharedPreferencesUtil.getString(
                        PreferencesKep.PLAY_SONG), sharedPreferencesUtil.getInt(PreferencesKep.PLAY_DURATION)));
    }

    /**
     * 选中指定的菜单项并显示对应的Fragment
     *
     * @param index 选择到的项目
     */
    private void setMenuSelector(int index) {
        nowType = index;
        reSetSelected();
        resetTextColor();
        myViews.get(index).setTextColor(0xff0493f9);
        if (index == 0) {
            mvMusic.setImageResource(R.mipmap.selectedmusic);
            tvTitle.setText("音乐");
            ivWeatherSearch.setVisibility(View.VISIBLE);
        } else if (index == 1) {
            mvVideo.setImageResource(R.mipmap.video_selected);
            tvTitle.setText("视频");
            ivWeatherSearch.setVisibility(View.GONE);
        } else if (index == 2) {
            mvNavigation.setImageResource(R.mipmap.navigation_selected);
            tvTitle.setText("导航");
            ivWeatherSearch.setVisibility(View.GONE);
        } else if (index == 3) {
            mvWeather.setImageResource(R.mipmap.weather_selected);
            tvTitle.setText("天气");
            ivWeatherSearch.setVisibility(View.VISIBLE);
        }
        myViews.get(index).setSelected(true);
        viewPager.setCurrentItem(index);
    }

    /**
     * 重置底部菜单所有ImageView和TextView为未选中状态
     */
    private void reSetSelected() {
        for (int i = 0; i < myViews.size(); i++) {
            myViews.get(i).setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                musicFragment.stopService();
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 重置底部tab按键文字颜色和图片颜色
     */

    private void resetTextColor() {
        mvMusic.setTextColor(0xff888888);
        mvVideo.setTextColor(0xff888888);
        mvNavigation.setTextColor(0xff888888);
        mvWeather.setTextColor(0xff888888);

        mvMusic.setImageResource(R.mipmap.unselectmusci);
        mvVideo.setImageResource(R.mipmap.video_unselect);
        mvNavigation.setImageResource(R.mipmap.navigation_unselect);
        mvWeather.setImageResource(R.mipmap.weather_unselect);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setMenuSelector(position);
        // 当天条目是0的时候，设置可以在任意位置拖拽出SlidingMenu
        if (position == 0) {
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            // 当在其他位置的时候，设置不可以拖拽出来(SlidingMenu.TOUCHMODE_NONE)
            // 或只有在边缘位置才可以拖拽出来TOUCHMODE_MARGIN
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置recyclerview item参数
     */
    private void setList() {
        list.add("会员注册");
        list.add("会员中心");
        list.add("音乐播放器");
        list.add("视频播放器");
        list.add("定位导航");
        list.add("天气预报");
        list.add("关于我们");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick({R.id.civ_portrait, R.id.iv_weather_search, R.id.mv_music, R.id.mv_video, R.id.mv_navigation, R.id.mv_weather})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.civ_portrait:
                mSlidingMenu.toggle();
                break;
            case R.id.iv_weather_search:
                if (nowType == 3) {
                    startActivity(new Intent(this, SearchWeatherActivity.class));
                } else if (nowType == 0) {
                    musicFragment.setSearchView();
                }
                break;
            case R.id.mv_music:
                setMenuSelector(0);
                break;
            case R.id.mv_video:
                setMenuSelector(1);
                break;
            case R.id.mv_navigation:
                setMenuSelector(2);
                break;
            case R.id.mv_weather:
                setMenuSelector(3);
                break;
        }
    }
}
