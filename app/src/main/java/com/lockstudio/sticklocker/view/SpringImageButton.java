package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

/**
 * Created by Tommy on 15/4/16.
 */
public class SpringImageButton extends ImageButton {
    public SpringImageButton(Context context) {
        super(context);
    }

    public SpringImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpringImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void spring() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        scaleAnimation.setDuration(200);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setFillAfter(true);
        this.startAnimation(scaleAnimation);
    }
}
