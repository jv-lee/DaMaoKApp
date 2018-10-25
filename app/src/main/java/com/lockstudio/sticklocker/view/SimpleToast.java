package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lockstudio.sticklocker.util.DeviceInfoUtils;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/19.
 */
public class SimpleToast extends Toast {


    public SimpleToast(Context context) {
        super(context);
    }


    public static Toast makeText(Context context, CharSequence text, int duration) {
        SimpleToast toast = new SimpleToast(context);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_simple, null);
        TextView tv = (TextView)v.findViewById(R.id.toast_simple_message);
        tv.setText(text);

        toast.setDuration(duration);
        toast.setView(v);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int h = DeviceInfoUtils.getDeviceHeight2(context);
        h = h * 7 / 10 - (v.getMeasuredHeight() / 2);
        toast.setGravity(Gravity.TOP, 0, h);
        return toast;
    }

    public static Toast makeText(Context context, int resId, int duration) {
        return makeText(context, context.getResources().getText(resId), duration);
    }
}
