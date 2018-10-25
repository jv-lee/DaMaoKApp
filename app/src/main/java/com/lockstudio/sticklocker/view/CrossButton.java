package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tommy on 15/3/12.
 */
public class CrossButton extends View {

    private int backgroundColor = Color.parseColor("#e60012");
    private int lineColor = Color.parseColor("#ffffff");

    private Paint paint;
    private RectF rH = new RectF();
    private RectF rV = new RectF();


    public CrossButton(Context context) {
        super(context);
    }

    public CrossButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public CrossButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int base = (width > height ? height : width);
        int radius = base / 2;
        int lW = base / 30;

        rH.set(width/4, radius-lW, width*3/4, radius+lW);
        rV.set(radius-lW, height/4, radius+lW, height*3/4);

        paint.setColor(backgroundColor);
        canvas.drawCircle(radius, radius, radius, paint);

        paint.setColor(lineColor);
        canvas.drawRoundRect(rH, lW/2, lW/2, paint);
        canvas.drawRoundRect(rV, lW/2, lW/2, paint);
    }

    private void setup(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
}
