package com.yuan7.lockscreen.utils;

import java.io.File;

/**
 * Created by Administrator on 2018/6/5.
 */

public class FileUtil {

    public static void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
