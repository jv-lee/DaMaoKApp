package com.yuan7.lockscreen.constant;

import android.os.Environment;

/**
 * Created by Administrator on 2018/5/28.
 */

public interface Constants {

    int PAGE_NUMBER = 15; //分页 总数

    String LABEL = "label";
    String SCREEN = "screen";
    String CATEGORY_ID = "category_id";
    String CATEGORY_NAME = "category_name";

    int LABEL_NEW = 1; //标签 最新
    int LABEL_HOT = 2; //标签 最热
    int LABEL_RECOMMEND = 3; //标签 推荐

    int SCREEN_PORTRAIT = 1; //竖屏
    int SCREEN_LANDSCAPE = 0; //横屏

    String ACTIVITY_INTENT_ENTITY = "entity";
    String ACTIVITY_INTENT_PATH = "path";
    String ACTIVITY_INTENT_SEARCH = "search";

    String LOCK_PATH = "lock_path";

    String DIR_CONSTANT = Environment.getExternalStorageDirectory() + "/androidscreen";

    String GAME_ENABLE = "game_enable";
    String GAME_ENABLE_YQ = "game_enable_yq";
}
