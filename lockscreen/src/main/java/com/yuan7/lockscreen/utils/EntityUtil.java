package com.yuan7.lockscreen.utils;

import com.yuan7.lockscreen.model.entity.*;

/**
 * Created by Administrator on 2018/5/30.
 */

public class EntityUtil {
    public static LabelDB LabelEntityToDB(LabelEntity entity, String path) {
        LabelDB db = new LabelDB();
        db.setHiapkId(entity.getHiapkId());
        db.setFocusImgUrl(entity.getFocusImgUrl());
        db.setGreatnumber(entity.getGreatnumber());
        db.setOldimgUrl(entity.getOldimgUrl());
        db.setSmallImgUrl(entity.getSmallImgUrl());
        db.setType(entity.isType() ? 1 : 0);
        db.setLocalPath(path);
        return db;
    }
}
