package com.yuan7.lockscreen.base.binding;

import android.view.View;
import android.widget.ImageView;

import com.yuan7.lockscreen.helper.glide.GlideUtils;


/**
 * Created by Administrator on 2018/5/22.
 */

public class BindingAdapter {
    @android.databinding.BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @android.databinding.BindingAdapter("visibleInvisible")
    public static void showHideCommand(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @android.databinding.BindingAdapter("imgUrl")
    public static void setPath(View view, String path) {
        GlideUtils.loadImage(path, (ImageView) view);
    }

    @android.databinding.BindingAdapter("onClick")
    public static void setOnclick(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(onClickListener);
    }
}
