package com.lockstudio.sticklocker.viewpage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 自定义广告 viewpager. 防止和父view的viewpager冲突
 * @author 庄宏岩
 *
 */
public class AdViewPager extends ViewPager implements OnGestureListener {
	private GestureDetector mDetector;
	public AdViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		GestureDetector detector = new GestureDetector(context, this);
		mDetector = detector;
	}

	public AdViewPager(Context context) {
		super(context);
		GestureDetector detector = new GestureDetector(context, this);
		mDetector = detector;
	}

	
	public GestureDetector getGestureDetector() {
        return mDetector;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(listener != null) {
            listener.onSingleClickListener(getCurrentItem());
        }
        return true;
    }
    
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return false;
    }
    
    private onSingleClickListener listener;
    
    public interface onSingleClickListener {
        void onSingleClickListener(int position);
    }
    
    public void setOnSingleClickListener(onSingleClickListener listener) {
        this.listener = listener;
    }
}
