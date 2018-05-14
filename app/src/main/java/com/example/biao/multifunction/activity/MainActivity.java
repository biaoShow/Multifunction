package com.example.biao.multifunction.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.adapter.FragmentAdapter;
import com.example.biao.multifunction.adapter.LeftRecylerviewAdapter;
import com.example.biao.multifunction.definedview.MyView;
import com.example.biao.multifunction.fragment.MusicFragment;
import com.example.biao.multifunction.fragment.NavigationFragment;
import com.example.biao.multifunction.fragment.VideoFragment;
import com.example.biao.multifunction.fragment.WeatherFragment;
import com.example.biao.multifunction.util.MusicUtils;
import com.example.biao.multifunction.util.MyApplication;
import com.example.biao.multifunction.util.OnClickLeftRLItemListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private SlidingMenu mSlidingMenu;//第三方库SlidingMenu对象
    private FragmentAdapter fragmentAdapter;//fragment 适配器
    private MusicFragment musicFragment;
    private VideoFragment videoFragment;
    private WeatherFragment weatherFragment;
    private NavigationFragment navigationFragment;
    private ArrayList<Fragment> fragments = new ArrayList<>();//fragment 集合
    private ArrayList<MyView> myViews = new ArrayList<>();//自定义MyView类型控件数组
    private MyView mv_music, mv_video, mv_navigation, mv_weather;
    private ViewPager viewPager;

    //菜单栏对象和控件
    private RecyclerView left_recyclerview;
    private LeftRecylerviewAdapter leftRecylerviewAdapter;
    private List<String> list = new ArrayList<>();
    private TextView tv_back;

    //title 布局控件
    private TextView tv_title;
    private View civ_portrait;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.left_menu);


        //android 6.0及以上权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)  //可写
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MyApplication.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MyApplication.getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED) {
                //申请ACCESS_FINE_LOCATION权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                MusicUtils.getMusicData(MyApplication.getContext());
            }
        }

        //activit_main布局控件初始化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mv_music = (MyView) findViewById(R.id.mv_music);
        mv_video = (MyView) findViewById(R.id.mv_video);
        mv_navigation = (MyView) findViewById(R.id.mv_navigation);
        mv_weather = (MyView) findViewById(R.id.mv_weather);

        //title_layout布局控件初始化
        tv_title = (TextView) findViewById(R.id.tv_title);
        civ_portrait = findViewById(R.id.civ_portrait);

        setList();//初始化泛型list数组

        //创建SlidingMenu对象和配置
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setBehindWidth(800);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setMenu(R.layout.left_menu);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setOffsetFadeDegree(0.4f);//左划时剩余部分变暗

        //将fragment添加到适配器
        myViews.add(mv_music);
        myViews.add(mv_video);
        myViews.add(mv_navigation);
        myViews.add(mv_weather);
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
        civ_portrait.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    MusicUtils.getMusicData(MyApplication.getContext());
                }else{
                    Toast.makeText(MyApplication.getContext(),"您已拒绝了权限！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        //将viewPage添加到适配器显示
        //放在此处，可避免首次进入应用出现空白列表
        viewPager.setAdapter(fragmentAdapter);

        //默认选中第一个Fragment
        setMenuSelector(0);
        mv_music.setImageResource(R.mipmap.selectedmusic);

        //为控件设置监听事件
        mv_music.setOnClickListener(this);
        mv_video.setOnClickListener(this);
        mv_navigation.setOnClickListener(this);
        mv_weather.setOnClickListener(this);


        //左菜单栏控件初始化
        View view = mSlidingMenu.getMenu();//获取左拉菜单栏view对象

        tv_back = view.findViewById(R.id.tv_back);


        tv_back.setOnClickListener(this);
        left_recyclerview = view.findViewById(R.id.left_recyclerview);
        leftRecylerviewAdapter = new LeftRecylerviewAdapter(this,list);
        left_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        left_recyclerview.setAdapter(leftRecylerviewAdapter);

        leftRecylerviewAdapter.setOnClickLeftRLItemListener(new OnClickLeftRLItemListener() {
            @Override
            public void onClickItem(int position) {
                if(1<position && position<6){
                    setMenuSelector(position-2);
                    mSlidingMenu.toggle();
                }else{
                    Toast.makeText(MainActivity.this,"功能暂未实现",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 选中指定的菜单项并显示对应的Fragment
     *
     * @param index 选择到的项目
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setMenuSelector(int index) {
        reSetSelected();
        resetTextColor();
        myViews.get(index).setTextColor(0xff4876FF);
        if (index == 0) {
            mv_music.setImageResource(R.mipmap.selectedmusic);
            tv_title.setText("音乐");
        } else if (index == 1) {
            mv_video.setImageResource(R.mipmap.video_selected);
            tv_title.setText("视频");
        } else if (index == 2) {
            mv_navigation.setImageResource(R.mipmap.navigation_selected);
            tv_title.setText("导航");
        } else if (index == 3) {
            mv_weather.setImageResource(R.mipmap.weather_selected);
            tv_title.setText("天气");
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.tv_back:
                musicFragment.stopService();
                finish();
                break;
            case R.id.civ_portrait:
                mSlidingMenu.toggle();
                break;
            default:
                break;
        }
    }

    /**
     * 重置底部tab按键文字颜色和图片颜色
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resetTextColor() {
        mv_music.setTextColor(0xff888888);
        mv_video.setTextColor(0xff888888);
        mv_navigation.setTextColor(0xff888888);
        mv_weather.setTextColor(0xff888888);

        mv_music.setImageResource(R.mipmap.unselectmusci);
        mv_video.setImageResource(R.mipmap.video_unselect);
        mv_navigation.setImageResource(R.mipmap.navigation_unselect);
        mv_weather.setImageResource(R.mipmap.weather_unselect);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    private void setList(){
        list.add("会员注册");
        list.add("会员中心");
        list.add("音乐播放器");
        list.add("视频播放器");
        list.add("定位导航");
        list.add("天气预报");
        list.add("关于我们");
    }

}
