package com.lockstudio.sticklocker.Interface;

public interface ImageDownloadListener {
	public void finish(String downloadUrl, String path);
	public void error(String downloadUrl);
	public void downloading(String downloadUrl, int size);
}
