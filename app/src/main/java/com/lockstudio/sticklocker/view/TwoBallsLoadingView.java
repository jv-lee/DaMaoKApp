package com.lockstudio.sticklocker.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by Tommy on 15/2/5.
 */
public class TwoBallsLoadingView extends RelativeLayout {

    private Context mContext;
    private boolean showed = false;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TwoBallsLoadingView(Context context) {
        super(context);

        initView(context);
    }

    public TwoBallsLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public TwoBallsLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!showed) {
            int width = getWidth();
            int height = getHeight();

            LayoutParams layoutParams = new LayoutParams(height, height);
            Ball ball_l = new Ball(mContext);
            ball_l.setLayoutParams(layoutParams);
            ball_l.setColor(Color.rgb(0xff, 0xc8, 0));

            Ball ball_r = new Ball(mContext);
            ball_r.setLayoutParams(layoutParams);
            ball_r.setColor(Color.rgb(0xff, 0x31, 0x23));


            TranslateAnimation left2right = new TranslateAnimation(0, width - height, 0, 0);
            left2right.setInterpolator(new AccelerateDecelerateInterpolator());
            left2right.setRepeatMode(Animation.REVERSE);
            left2right.setRepeatCount(-1);
            left2right.setDuration(400);


            TranslateAnimation right2left = new TranslateAnimation(width - height, 0, 0, 0);
            right2left.setInterpolator(new AccelerateDecelerateInterpolator());
            right2left.setRepeatMode(Animation.REVERSE);
            right2left.setRepeatCount(-1);
            right2left.setDuration(400);

            ball_l.startAnimation(left2right);
            ball_r.startAnimation(right2left);

            addView(ball_l);
            addView(ball_r);

            showed = true;
        }
    }

    /**
     * Called when the window containing has change its visibility
     * (between {@link #GONE}, {@link #INVISIBLE}, and {@link #VISIBLE}).  Note
     * that this tells you whether or not your window is being made visible
     * to the window manager; this does <em>not</em> tell you whether or not
     * your window is obscured by other windows on the screen, even if it
     * is itself visible.
     *
     * @param visibility The new visibility of the window.
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            removeAllViews();
            showed = false;
        }
    }

    class Ball extends View {
        private Paint paint = new Paint();
        private int color;

        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        public Ball(Context context) {
            super(context);
        }

        /**
         * Implement this to do your drawing.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int centre = getWidth() / 2; //获取圆心的x坐标
            int radius = getWidth() / 2; //圆环的半径
            paint.setColor(color); //设置圆环的颜色
            paint.setStyle(Paint.Style.FILL_AND_STROKE); //设置空心
            paint.setStrokeWidth(0); //设置圆环的宽度
            paint.setAntiAlias(true);  //消除锯齿
            canvas.drawCircle(centre, centre, radius, paint); //画出圆环
        }

        public void setColor(int c) {
            color = c;
        }
    }
}

