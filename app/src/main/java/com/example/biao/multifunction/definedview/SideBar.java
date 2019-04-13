package com.example.biao.multifunction.definedview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.biao.multifunction.R;
import com.example.biao.multifunction.util.OnChooseLetterChangedListener;

/**
 * 自定义字母导航条
 * Created by benxiang on 2019/4/1.
 */

public class SideBar extends View {

    private Paint paint = new Paint();

    private int choose = -1;

    private boolean showBackground;
    public static String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private OnChooseLetterChangedListener onChooseLetterChangedListener;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBackground) {
            setBackgroundResource(R.drawable.bg_sidebar_press);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }
        int height = getHeight();
        int width = getWidth();
        //平均每个字母占的高度
        int singleHeight = height / letters.length;
        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(25);
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.sidebar_right_select));
                paint.setFakeBoldText(true);
            }
            float x = width / 2 - paint.measureText(letters[i]) / 2;
            float y = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], x, y, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        int c = (int) (y / getHeight() * letters.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBackground = true;
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        //获取触摸位置的字符
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        //获取触摸位置的字符
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBackground = false;
                choose = -1;
                if (onChooseLetterChangedListener != null) {
                    //手指离开
                    onChooseLetterChangedListener.onNoChooseLetter();
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnChooseLetterChangedListener onChooseLetterChangedListener) {
        this.onChooseLetterChangedListener = onChooseLetterChangedListener;
    }
}
