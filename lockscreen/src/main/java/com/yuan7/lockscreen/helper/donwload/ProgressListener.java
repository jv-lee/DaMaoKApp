package com.yuan7.lockscreen.helper.donwload;

/**
 * Created by Administrator on 2018/5/28.
 */

public interface ProgressListener {
    void onProgress(long progress, long total, boolean done);
}
