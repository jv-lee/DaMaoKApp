package com.yuan7.lockscreen.model.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/29.
 */

public class CategoryEntity implements Serializable{

    private int categoryId;
    private String categoryName;
    private String acrossUrl;
    private String verticalUrl;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAcrossUrl() {
        return acrossUrl;
    }

    public void setAcrossUrl(String acrossUrl) {
        this.acrossUrl = acrossUrl;
    }

    public String getVerticalUrl() {
        return verticalUrl;
    }

    public void setVerticalUrl(String verticalUrl) {
        this.verticalUrl = verticalUrl;
    }
}
