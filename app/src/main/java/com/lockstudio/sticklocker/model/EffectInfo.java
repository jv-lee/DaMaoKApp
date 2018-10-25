package com.lockstudio.sticklocker.model;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.GPUImageFilterTools.FilterType;

public class EffectInfo {
	private FilterType filterType;
	private String effectName;
	private int effectImage;
	private boolean selected;
	private GPUImageFilter gpuImageFilter;
	private int progress;
	
	
	
	
	public FilterType getFilterType() {
		return filterType;
	}
	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public GPUImageFilter getGpuImageFilter() {
		return gpuImageFilter;
	}
	public void setGpuImageFilter(GPUImageFilter gpuImageFilter) {
		this.gpuImageFilter = gpuImageFilter;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getEffectName() {
		return effectName;
	}
	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}
	public int getEffectImage() {
		return effectImage;
	}
	public void setEffectImage(int effectImage) {
		this.effectImage = effectImage;
	}
	
	
	
}
