package com.example.biao.multifunction.definedview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.example.biao.multifunction.model.LyricsObjct;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义歌词类
 * Created by biao on 2018/5/9.
 */

public class LrcView extends AppCompatTextView {
    private float width;                   //歌词视图宽度
    private float height;                 //歌词视图高度
    private Paint currentPaint;          //当前画笔对象
    private Paint notCurrentPaint;      //非当前画笔对象
    private float textHeight = 100;      //文本高度
    private float textMaxSize = 60;
    private float textSize = 50;        //文本大小
    private int index = 0;//list集合下标
    private int index_lrc = 0;//记录歌词移到到哪一行
    private List<LyricsObjct> infos = new ArrayList<>();//歌词信息
    private Scroller scroller;

    public void setmLrcList(List<LyricsObjct> infos) {
        this.infos = infos;
    }

    public LrcView(Context context) {
        super(context);
        init(context);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        setFocusable(true);     //设置可对焦
        //显示歌词部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式
        currentPaint.setColor(Color.argb(210, 255, 0, 0));
        currentPaint.setTextSize(textMaxSize);
        currentPaint.setTypeface(Typeface.SERIF);
        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        notCurrentPaint.setColor(Color.argb(150, 125, 125, 125));
        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);

        scroller = new Scroller(context);
    }

    /**
     * 绘画歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        try {
            setText("");

            if (infos.size() == 0) {
                canvas.drawText("暂无歌词", width / 2, height / 2 + 18, notCurrentPaint);
                return;
            }
//
//            canvas.drawText(infos.get(index).content, width / 2, height / 2, currentPaint);
//
//            float tempY = height / 2;
//            //画出本句之前的句子
//            for (int i = index - 1; i >= 0; i--) {
//                //向上推移
//                tempY = tempY - textHeight;
//                canvas.drawText(infos.get(i).content, width / 2, tempY, notCurrentPaint);
//            }
            float tempY = height / 2;
//            画出本句之后的句子
            for (int i = 0; i < infos.size(); i++) {
                //往下推移
                tempY = tempY + textHeight;
                if (i == index) {
                    canvas.drawText(infos.get(i).content, width / 2, tempY, currentPaint);
                } else {
                    canvas.drawText(infos.get(i).content, width / 2, tempY, notCurrentPaint);
                }
            }
        } catch (Exception e) {
            canvas.drawText("暂无歌词", width / 2, height / 2 + 18, notCurrentPaint);
        }

    }

    /**
     * 当view大小改变的时候调用的方法
     */

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setIndex(int index) {
        if (this.index != index) {
            Log.i("index:" + index + "--index_lrc:" + index_lrc, "");
            scroller.startScroll(0, getScrollY(), 0, (int) (textHeight * (index - index_lrc)), 1500);
            this.index = index;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            index_lrc = this.index;
            invalidate();
        }
    }


    /**
     * 广播接受器
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            index = 0;
            index_lrc = 0;
        }
    }
}
