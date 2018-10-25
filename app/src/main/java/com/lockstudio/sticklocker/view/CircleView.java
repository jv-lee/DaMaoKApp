package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/18.
 */
public class CircleView extends View {

    private int spotColor = Color.parseColor("#e60012");
    private RectF spotRect = new RectF();

    private Paint paint;

    public CircleView(Context context) {
        super(context);
        setup(null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    protected void setup(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleView);
            spotColor = typedArray.getColor(R.styleable.CircleView_circleColor, spotColor);
            typedArray.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int spotSize = min(getWidth(), getHeight());
        final float spotR = spotSize * 0.5f;
        spotRect.set((getWidth() - spotSize) * 0.5f, (getHeight() - spotSize) * 0.5f, spotSize, spotSize);
        paint.setColor(spotColor);
        canvas.drawRoundRect(spotRect, spotR, spotR, paint);
    }

    private int min(int value1, int value2) {
        return Math.min(value1, value2);
    }
}
