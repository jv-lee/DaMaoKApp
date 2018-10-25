package com.yuan7.lockscreen.model.entity;

/**
 * Created by Administrator on 2018/6/4.
 */

public class SearchEntity {

    /**
     * searchId : 16
     * searchName : 向日葵
     * isActivate : 0
     */

    private int searchId;
    private String searchName;
    private int isActivate;

    public SearchEntity() {
    }

    public SearchEntity(int searchId, String searchName, int isActivate) {
        this.searchId = searchId;
        this.searchName = searchName;
        this.isActivate = isActivate;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public int getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(int isActivate) {
        this.isActivate = isActivate;
    }
}
