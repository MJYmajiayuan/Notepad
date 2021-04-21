package com.android.notepad.ui.edit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.notepad.R;

public class CircleView extends View {

    private Paint mPaint;
    private int mPaintColor;
    private float mRadius;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleView,
                0, 0
        );
        try {
            mPaintColor = a.getInteger(R.styleable.CircleView_paintColor, Color.WHITE);
            mRadius = a.getDimension(R.styleable.CircleView_radius, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        mPaint.setColor(mPaintColor);
        canvas.drawCircle(
                (float) width / 2,
                (float) height / 2,
                mRadius,
                mPaint);
    }

    public void selected() {
        mRadius *= 1.5;
    }

    /**
     * 获取当前画笔颜色
     * @return 颜色
     */
    public int getColor() {
        return mPaintColor;
    }

    /**
     * 获取当前圆圈半径以设置画笔粗细
     * @return
     */
    public int getWeight() {
        return (int) mRadius;
    }
}
