package com.yuan7.lockscreen.helper.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Administrator on 2018/5/25.
 */

public class GlideUtils {

    private volatile static GlideUtils mInstance = null;

    private static Context mContext = null;

    private static RequestOptions optionsCommand = null;
    private static DrawableTransitionOptions optionsDrawable = null;

    private GlideUtils() {
        initOptions();
    }

    public static GlideUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (GlideUtils.class) {
                if (mInstance == null) {
                    mContext = context;
                    mInstance = new GlideUtils();
                }
            }
        }
        return mInstance;
    }

    private void initOptions() {
        //初始化普通加载
        if (optionsCommand == null) {
            optionsCommand = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
        }

        //初始化动画加载
        if (optionsDrawable == null) {
            optionsDrawable = new DrawableTransitionOptions();
            optionsDrawable.crossFade();
        }
    }

    public void loadImages(Object path, ImageView imageView) {
        Glide.with(mContext)
                .load(path)
//                .transition(optionsDrawable)
                .apply(optionsCommand)
                .into(imageView);
    }

    public static void loadImage(Object path, ImageView imageView) {
        Glide.with(mContext)
                .load(path)
//                .transition(optionsDrawable)
                .apply(optionsCommand)
                .into(imageView);
    }

}
