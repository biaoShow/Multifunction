package com.example.biao.multifunction.popwindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.biao.multifunction.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by benxiang on 2019/4/18.
 */

public class MyPopWindows extends PopupWindow implements View.OnClickListener {

    LinearLayout llCirculation;
    LinearLayout llRandom;
    LinearLayout llSingle;
    LinearLayout llPopwindows;
    private View myPopView;
    private OnItemClickListener onItemClickListener;

    public MyPopWindows(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        myPopView = layoutInflater.inflate(R.layout.popwindows_layout, null);

        llCirculation = myPopView.findViewById(R.id.ll_circulation);
        llRandom = myPopView.findViewById(R.id.ll_random);
        llSingle = myPopView.findViewById(R.id.ll_single);
        llPopwindows = myPopView.findViewById(R.id.ll_popwindows);
        llCirculation.setOnClickListener(this);
        llRandom.setOnClickListener(this);
        llSingle.setOnClickListener(this);
        setPopupWindow();
    }

    /**
     * 显示屏popWindows
     *
     * @param parent
     */
    public void showMyPopWindows(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, -400);
//            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    private void setPopupWindow() {
        this.setContentView(myPopView);// 设置View
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
//        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        myPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = llPopwindows.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void onClick(View view) {
        onItemClickListener.onItemClick(view);
    }


    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(View v);
    }
}
