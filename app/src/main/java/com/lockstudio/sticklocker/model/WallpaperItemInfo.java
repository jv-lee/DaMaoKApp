package com.lockstudio.sticklocker.model;

/**
 * Created by Tommy on 15/3/29.
 */
public class WallpaperItemInfo {

    private int id;
    private String title;
    private String desc;
    private int color = 0xff343434;
    private String imageUrl;
    private boolean dm_Bannner_flag;

    public boolean isDm_Bannner_flag() {
		return dm_Bannner_flag;
	}

	public void setDm_Bannner_flag(boolean dm_Bannner_flag) {
		this.dm_Bannner_flag = dm_Bannner_flag;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
