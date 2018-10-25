package com.lockstudio.sticklocker.model;

import cn.opda.android.activity.R;


/**
 * 文字密码锁
 * 
 * @author 庄宏岩
 * 
 */
public class WordPasswordLockerInfo extends LockerInfo {
	private String[] words = { "昔", "少", "时", "与", "卿", "离", "若", "相", "逢", "定", "不", "弃" };
	private String font;
	private int alpha = 255;
	private int textColor = 0xffffffff;
	private int shadowColor = 0x00ffffff;
	private float textScale = 1.0f;
	private float textSize = 25.0f;
	private int shapeId = R.drawable.word_shape_yuan;
	private String shapeName = "yuan";
	private float shapeScale = 1.3f;
	
	
	public int getShapeId() {
		return shapeId;
	}

	public void setShapeId(int shapeId) {
		this.shapeId = shapeId;
	}

	public String getShapeName() {
		return shapeName;
	}

	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}

	public float getShapeScale() {
		return shapeScale;
	}

	public void setShapeScale(float shapeScale) {
		this.shapeScale = shapeScale;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getTextScale() {
		return textScale;
	}

	public void setTextScale(float textScale) {
		this.textScale = textScale;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(int shadowColor) {
		this.shadowColor = shadowColor;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String[] getWords() {
		return words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

}
