package com.lockstudio.sticklocker.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PaperBean {
	private Bitmap mBitmap;
	private ArrayList<PaperBean> arrayList;
	private String image_png_url;
	private String abbr_png_url;
	private Bitmap image_png_bm;
	private Bitmap abbr_png_bm;
	private int id;
	private String title;
	private String desc;

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}

	public ArrayList<PaperBean> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<PaperBean> arrayList) {
		this.arrayList = arrayList;
	}

	public String getImage_png_url() {
		return image_png_url;
	}

	public void setImage_png_url(String image_png_url) {
		this.image_png_url = image_png_url;
	}

	public String getAbbr_png_url() {
		return abbr_png_url;
	}

	public void setAbbr_png_url(String abbr_png_url) {
		this.abbr_png_url = abbr_png_url;
	}

	public Bitmap getImage_png_bm() {
		return image_png_bm;
	}

	public void setImage_png_bm(Bitmap image_png_bm) {
		this.image_png_bm = image_png_bm;
	}

	public Bitmap getAbbr_png_bm() {
		return abbr_png_bm;
	}

	public void setAbbr_png_bm(Bitmap abbr_png_bm) {
		this.abbr_png_bm = abbr_png_bm;
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

}
