package com.example.biao.multifunction.definedview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.example.biao.multifunction.R;

import java.io.InputStream;

/**
 * 自定义view  圆形头像
 * Created by biao on 2018/5/1.
 */

public class CircleImageView extends View {

    private Paint mPaint = new Paint(); //画笔
    private float mRadius; //圆形图片的半径
    private Drawable msrc;//图片
    private Context context;
    private Matrix matrix;
    private Bitmap bitmap;
    private  BitmapShader mBitmapShader;

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);

        msrc = ta.getDrawable(R.styleable.CircleImageView_src);
        mRadius = ta.getDimension(R.styleable.CircleImageView_radius,30);

        bitmap = drawableToBitmap(msrc);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix = new Matrix();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            width = (int) mRadius*2;
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            height = (int) mRadius*2;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算缩放比例
         float mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight(), bitmap.getWidth());

        matrix.setScale(mScale, mScale);
        mBitmapShader.setLocalMatrix(matrix);


        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
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
            @SuppressLint("ResourceType") InputStream in = rec.openRawResource(R.mipmap.ic_launcher);
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }
    }

    /**
     * 获取圆形图片方法
     * @param bitmap
     * @param pixels
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mPaint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        mPaint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawCircle(x / 2, x / 2, x / 2, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, mPaint);
        return output;


    }

    /**
     * 提供使用类方法设置图片
     * @param bitmap 设置的图片
     */
    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
