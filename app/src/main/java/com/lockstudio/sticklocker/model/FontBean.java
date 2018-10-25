package com.lockstudio.sticklocker.model;

public class FontBean {
//	"name": "卞白贤字体",
//    "shortname": "lingdiancuti",
//    "downloadurl": "http://static.opda.com/resource/font/chs/6c47a87413a10b2b3714010aff5f12fb.apk",
//    "showurl": "http://115.182.33.147/web/cache/content/13c76011484d40156feb8d456e7a8173_lingdiancuti.ttf",
	private boolean checked;
	private boolean downloaded;
	private boolean downloading;
	private String path;
	private String name;
	private String downloadurl;
	private String suoxie;
	private String showurl;
	private String showttf_path;
	private int progress;
	private String size;
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	public boolean isDownloading() {
		return downloading;
	}
	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}
	public boolean isDownloaded() {
		return downloaded;
	}
	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	public String getSuoxie() {
		return suoxie;
	}
	public void setSuoxie(String suoxie) {
		this.suoxie = suoxie;
	}
	public String getShowurl() {
		return showurl;
	}
	public void setShowurl(String showurl) {
		this.showurl = showurl;
	}
	public String getShowttf_path() {
		return showttf_path;
	}
	public void setShowttf_path(String showttf_path) {
		this.showttf_path = showttf_path;
	}
	@Override
	public String toString() {
		return "FontBean [checked=" + checked + ", path=" + path + ", name=" + name + ", downloadurl=" + downloadurl + ", suoxie=" + suoxie
				+ ", showurl=" + showurl + ", showttf_path=" + showttf_path + "]";
	}
	
}
