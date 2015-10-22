package com.eric.zmappii.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.eric.zmappii.R;

import java.util.concurrent.RecursiveAction;

/**
 * Created by eric on 15/6/7.
 */
public class RoundImageView extends ImageView {

    private Context mContext;

    private int mBorderThickness = 0;
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;

    private int defaultColor = 0xFFFFFFFF;
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomeAttributes(attrs);

    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setCustomeAttributes(attrs);
    }

    private void setCustomeAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(
                R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a.getColor(
                R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(
                R.styleable.roundedimageview_border_inside_color, defaultColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;

        Bitmap b = ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        int radius = 0;
        if (mBorderInsideColor != defaultColor &&
                mBorderOutsideColor != defaultColor) { // 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ?
                    defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness /2,
                    mBorderOutsideColor);
        } else if (mBorderInsideColor != defaultColor &&
                mBorderOutsideColor == defaultColor) { // 定义画一个边框
            radius = (defaultWidth < defaultHeight ?
                    defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor &&
                mBorderOutsideColor != defaultColor) { // 画一个边框
            radius = (defaultWidth < defaultHeight ?
                    defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderOutsideColor);
        } else { // 没有边框
            radius = (defaultWidth < defaultHeight ?
                    defaultWidth : defaultHeight) / 2;
        }

        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);

        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius,
                defaultHeight / 2 - radius, null);
    }

    /**
     * 获取裁剪后的圆形图片
     * @param bitmap
     * @param radius
     * @return
     */
    private Bitmap getCroppedRoundBitmap(Bitmap bitmap, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();

        int squareWidth = 0;
        int squareHeight = 0;
        int x = 0;
        int y = 0;

        // 截取正方形图片
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            squareBitmap = Bitmap.createBitmap(bitmap, x, y,
                    squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bitmap, x, y,
                    squareWidth, squareHeight);
        } else {
            squareBitmap = bitmap;
        }

        // 图片大小适配
        if (squareBitmap.getWidth() != diameter ||
                squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }

        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);

        // 释放资源
        bitmap = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     * @param canvas
     * @param radius
     * @param color
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 去锯齿
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE); // 空心圆
        paint.setStrokeWidth(mBorderThickness);

        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2,
                radius, paint);
    }
}
