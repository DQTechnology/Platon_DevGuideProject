package com.platon.aton.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class ViewPagerSlide extends ViewPager {
    //是否可以进行滑动
    private boolean isSlide = false;

    public ViewPagerSlide(Context context) {
        super(context);
    }

    public ViewPagerSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSlide){
            return super.onInterceptTouchEvent(ev);
        }
        return isSlide;
    }

    public void setSlide(boolean slide) {
        isSlide = slide;
    }
}
