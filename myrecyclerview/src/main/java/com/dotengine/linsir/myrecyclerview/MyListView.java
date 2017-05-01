package com.dotengine.linsir.myrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 *  Created by linSir 
 *  date at 2017/5/1.
 *  describe: 自定义的recyclerView，主要是实现可以侧滑删除
 */

public class MyListView extends ListView {

    private static final String TAG = MyListView.class.getSimpleName();

    private int touchSlop;  //用户滑动的最小距离
    private boolean isSliding;  //是否相应滑动
    private int xDown;  //按下的x坐标
    private int yDown;  //按下的y坐标
    private int xMove;  //手指移动时x的坐标
    private int yMove;  //手指移动是y的坐标
    private LayoutInflater mInflater;   //一个layoutInflater
    private PopupWindow mPopupWindow;   //弹出一个用于展示的popupWindow
    private int mPopupWindowHeight;     //该展示的popupWindow的高度
    private int mPopupWindowWidth;      //该展示的popupWindow的宽度

    private TextView delete;    //侧滑后删除的按钮
    private DeleteClickListener mListener;  //点击删除后回调的接口
    private View mCurrentView;  //当前展示删除按钮的view
    private int mCurrentViewPos;    //当前展示删除按钮的view的位置(下标)

    /**
     * 该自定义view的构造方法
     */
    public MyListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);   //一个Inflater
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();    //最小的滑动距离

        View view = mInflater.inflate(R.layout.delete_btn, null);   //找到删除按钮的view
        delete = (TextView) view.findViewById(R.id.delete);     //找到删除按钮的控件


        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);    //弹出的popupWindow

        mPopupWindow.getContentView().measure(0, 0);    //初始化
        mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight(); //获取到该view的高度
        mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();   //获取到该view的宽度


    }

    /**
     * 触摸时间的派发
     */
    @Override public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:   //action_down 即点击事件，这个时候需要关闭popupWindow
                xDown = x;
                yDown = y;

                if (mPopupWindow.isShowing()) {
                    dismissPopWindow();
                    return false;
                }

                mCurrentViewPos = pointToPosition(xDown, yDown);    //根据x,y坐标获取到自己的下标
                View view = getChildAt(mCurrentViewPos - getFirstVisiblePosition());//当前可见view的小标减去第一个可见的view的下标就可以找到当前的这个view
                mCurrentView = view;

                break;

            case MotionEvent.ACTION_MOVE:   //当发生移动时间的时候
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;

                if (xMove < xDown && Math.abs(dx) > touchSlop && Math.abs(dy) < touchSlop) { //判断向左滑动，并且滑动了一定距离
                    isSliding = true;   //满足这个条件就符合了打开的popupWindow的条件
                }
                break;
        }

        return super.dispatchTouchEvent(ev);

    }


    @Override public boolean onTouchEvent(MotionEvent ev) {

        if (mCurrentView == null) {     //判断当前的view不存在之后，则直接return不进行处理这次时间
            return false;
        }

        int action = ev.getAction();

        if (isSliding) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int[] location = new int[2];
                    mCurrentView.getLocationOnScreen(location);
                    mPopupWindow.update();

                    delete.setHeight(getMeasuredHeight()/getChildCount());   //计算出来每一个条目的高度

                    mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,
                            location[0] + mCurrentView.getWidth(), location[1] + mCurrentView.getHeight() / 2
                                    - mPopupWindowHeight );     //设置显示的位置

                    delete.setOnClickListener(new OnClickListener() {
                        @Override public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onClickDelete(mCurrentViewPos);
                                mPopupWindow.dismiss();
                            }
                        }
                    });

                    break;

                case MotionEvent.ACTION_UP:
                    isSliding = false;

                    break;
            }


            return true;
        }
        return super.onTouchEvent(ev);

    }


    private void dismissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

    }


    public void setDelButtonClickListener(DeleteClickListener listener) {
        mListener = listener;
    }

}





































