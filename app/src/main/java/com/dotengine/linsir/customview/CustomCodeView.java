package com.dotengine.linsir.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by linSir
 * date at 2017/4/7.
 * describe:自定义的自动生成验证码的view
 */

public class CustomCodeView extends View {

    private String mCodeViewText;
    private int mCodeViewColor;
    private int mCodeViewSize;

    private Rect mBound;
    private Paint mPaint;

    public CustomCodeView(Context context) {
        this(context,null);
    }

    public CustomCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CustomCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCodeView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {

                case R.styleable.CustomCodeView_titleText:
                    mCodeViewText = a.getString(attr);
                    break;

                case R.styleable.CustomCodeView_titleTextColor:
                    mCodeViewColor = a.getColor(attr, Color.BLUE);
                    break;

                case R.styleable.CustomCodeView_titleTextSize:
                    mCodeViewSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;

            }

        }
        a.recycle();
        mPaint = new Paint();
        mPaint.setTextSize(mCodeViewSize);
        mBound = new Rect();
        mPaint.getTextBounds(mCodeViewText, 0, mCodeViewText.length(), mBound);

        this.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                mCodeViewText = randomText();
                postInvalidate();
            }
        });


    }

    private String randomText() {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set) {
            sb.append("" + i);
        }
        return sb.toString();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;

            case MeasureSpec.AT_MOST:
                width = getPaddingLeft() + getPaddingRight() + mBound.width();
                break;
        }

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;

            case MeasureSpec.AT_MOST:
                height = getPaddingTop() + getPaddingBottom() + mBound.height();
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setColor(mCodeViewColor);
        canvas.drawText(mCodeViewText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}


































