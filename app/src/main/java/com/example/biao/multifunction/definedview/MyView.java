package com.example.biao.multifunction.definedview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.example.biao.multifunction.R;

import java.io.InputStream;


/**
 * 自定义view 上面一个图片，下面一个textview 文本
 * text 文本内容
 * textecolor 文本颜色
 * textsize 文本大小
 * mybackground view的底色
 * imgwidth 图片宽度
 * imgheight 图片高度
 * img_and_text_distance 图片与文字的间距
 * mysrc 填充图片内容
 */
public class MyView extends View {

    private Context context;
    private Paint mPaintWidth = new Paint();//画笔
    private Paint mPaintHeight = new Paint();//画笔
    private Rect mBoundWidth = new Rect();
    private Bitmap bitmap;

    private String text;
    private int textColor, mybackground;
    private float textSize, imgWidth, imgHeight, img_and_text_distance,viewXr,viewYr;
    private Drawable mysrc;

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyView);

        text = ta.getString(R.styleable.MyView_text);
        textColor = ta.getColor(R.styleable.MyView_textColor, Color.BLACK);
        textSize = ta.getDimension(R.styleable.MyView_textSize, 5);
        mybackground = ta.getColor(R.styleable.MyView_mybackground, Color.WHITE);
        imgWidth = ta.getDimension(R.styleable.MyView_imgWidth, 100);
        imgHeight = ta.getDimension(R.styleable.MyView_imgHeight, 100);
        img_and_text_distance = ta.getDimension(R.styleable.MyView_img_and_text_distance, 20);
        viewXr = ta.getDimension(R.styleable.MyView_viewXr,0);
        viewYr = ta.getDimension(R.styleable.MyView_viewYr,0);

        mysrc = ta.getDrawable(R.styleable.MyView_mysrc);
        bitmap = drawableToBitmap(mysrc);
        ta.recycle();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;
        //text 非空处理
        if(text==null){
            text = "TextView";
        }
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            mPaintWidth.setTextSize(textSize);
            //设置位置的宽度（长度）
            mPaintWidth.getTextBounds(text, 0, text.length(), mBoundWidth);
            float textWidth = mBoundWidth.width();
            //获取文字长度和图片框度的最大值
            float max = Math.max(textWidth,imgWidth);
            //设置绘制的宽度
            int desired = (int) (getPaddingLeft() + max + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            mPaintHeight.setTextSize(textSize);
            //设置文本的长度
            mPaintHeight.getTextBounds(text, 0, text.length(), mBoundWidth);
            //获取文本的高度
            float textHeight = mBoundWidth.height();
            //设置绘制的高度
            int desired = (int) (getPaddingTop() + imgHeight+textHeight+img_and_text_distance
                    + getPaddingBottom());
            height = desired;
        }
        setMeasuredDimension(width, height);//设置宽高

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制矩形框
        mPaintWidth.setTextSize(textSize);
        mPaintWidth.setColor(mybackground);
        //设置画图起始位置和大小
        RectF rectF = new RectF(0,0,getMeasuredWidth(),getMeasuredHeight());
        //设置圆角半径并绘制图
        canvas.drawRoundRect(rectF,viewXr,viewYr,mPaintWidth);

        //绘制文字
        mPaintWidth.setColor(textColor);
        //非空处理
        if(text==null){
            text = "TextView";
        }
        //设置绘制文字长度
        mPaintWidth.getTextBounds(text, 0, text.length(), mBoundWidth);
        //设置绘制文字起始位置并绘制文字
        canvas.drawText(text, getWidth() / 2 - mBoundWidth.width() / 2,
                imgHeight + mBoundWidth.height() + img_and_text_distance, mPaintWidth);

        //绘制图片
        // 将画布坐标系移动到画布上中央
        canvas.translate(getWidth() / 2, 0);

        // 指定图片绘制区域
        @SuppressLint("DrawAllocation")
        Rect src = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        // 指定图片在屏幕上显示的区域
        @SuppressLint("DrawAllocation")
        Rect dst = new Rect(-(int) imgWidth / 2, getPaddingTop(), (int) imgWidth / 2,
                (int) imgHeight + getPaddingTop());

        // 绘制图片
            canvas.drawBitmap(bitmap, src, dst, null);
    }

    /**
     * Drawable图片转化为Bitmap方法
     * @param mysrc Drawable类型图片
     * @return 返回Btimap类型图片
     */

    public Bitmap drawableToBitmap(Drawable mysrc) {
        Bitmap bitmap = null;
        if (mysrc != null) {
            bitmap = Bitmap.createBitmap(mysrc.getIntrinsicWidth(), mysrc.getIntrinsicHeight(), mysrc.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            mysrc.setBounds(0, 0, mysrc.getIntrinsicWidth(), mysrc.getIntrinsicHeight());
            mysrc.draw(canvas);
            return bitmap;
        } else {
            Resources rec = getResources();
            @SuppressLint("ResourceType")
            InputStream in = rec.openRawResource(R.mipmap.ic_launcher);
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }
    }

    /**
     * 提供使用类方法设置图片
     *
     */
    @SuppressLint("NewApi")
    public void setImageResource(int src) {
        this.mysrc = context.getDrawable(src);
        bitmap = drawableToBitmap(mysrc);
    }

    /**
     * 提供java 代码设置文办颜色方法
     * @param textColor
     */
    public void setTextColor(int textColor){
        this.textColor = textColor;
    }
}
