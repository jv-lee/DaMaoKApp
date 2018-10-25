package com.yuan7.lockscreen.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/25.
 */

public class LabelEntity implements Serializable, MultiItemEntity {

    /**
     * hiapkId : 14
     * keywords : 美女漫画
     * figure : false
     * type : true
     * greatnumber : 1000
     * oldimgUrl : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/testupload/originalImage/1527173186473.jpg
     * smallImgUrl : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/testupload/thumbnail/1527173186473.jpg
     * focusImgUrl : http://yuan7ad.oss-cn-shenzhen.aliyuncs.com/testupload/clearImage/1527173186473.jpg
     * uploadTime : 2018-05-24 22:46:26
     * categories : [{"categoryId":1,"categoryName":"美女","acrossUrl":null,"verticalUrl":null},{"categoryId":2,"categoryName":"动漫","acrossUrl":null,"verticalUrl":null},{"categoryId":3,"categoryName":"风景","acrossUrl":null,"verticalUrl":null}]
     * labels : [{"labelId":1,"labelName":"最新","acrossUrl":null,"verticalUrl":null},{"labelId":2,"labelName":"最热","acrossUrl":null,"verticalUrl":null},{"labelId":3,"labelName":"推荐","acrossUrl":null,"verticalUrl":null}]
     */

    private int hiapkId;
    private String keywords;
    private boolean figure;
    private boolean type;
    private int greatnumber;
    private String oldimgUrl;
    private String smallImgUrl;
    private String focusImgUrl;
    private String uploadTime;
    private Type layout_type;

    public Object view;

    public static final int CONTENT = 0;
    public static final int AD = 1;

    public enum Type {
        CONTENT, AD
    }

    @Override
    public int getItemType() {
        if (layout_type != null) {
            if (layout_type.equals(Type.CONTENT)) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }

    public LabelEntity() {
    }

    public LabelEntity(Type type) {
        this.layout_type = type;
    }

    public int getHiapkId() {
        return hiapkId;
    }

    public void setHiapkId(int hiapkId) {
        this.hiapkId = hiapkId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public boolean isFigure() {
        return figure;
    }

    public void setFigure(boolean figure) {
        this.figure = figure;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
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

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }


}
