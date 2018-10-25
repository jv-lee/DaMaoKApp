package com.yuan7.lockscreen.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.yuan7.lockscreen.constant.DBConstant;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/30.
 */
@Entity(tableName = DBConstant.LABEL_TABLE_NAME)
public class LabelDB implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hiapkId;
    private int type;
    private int greatnumber;
    private String oldimgUrl;
    private String smallImgUrl;
    private String focusImgUrl;
    private String localPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHiapkId() {
        return hiapkId;
    }

    public void setHiapkId(int hiapkId) {
        this.hiapkId = hiapkId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGreatnumber() {
        return greatnumber;
    }

    public void setGreatnumber(int greatnumber) {
        this.greatnumber = greatnumber;
    }

    public String getOldimgUrl() {
        return oldimgUrl;
    }

    public void setOldimgUrl(String oldimgUrl) {
        this.oldimgUrl = oldimgUrl;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public String getFocusImgUrl() {
        return focusImgUrl;
    }

    public void setFocusImgUrl(String focusImgUrl) {
        this.focusImgUrl = focusImgUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
