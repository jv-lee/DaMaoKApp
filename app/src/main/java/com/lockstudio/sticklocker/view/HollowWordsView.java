package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveStickerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.model.HollowWordsStickerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.PluginChooseWordUtils;
import com.lockstudio.sticklocker.util.PluginChooseWordUtils.OnTextChangeListener;
import com.lockstudio.sticklocker.util.PluginHollowWordsUtils;
import com.lockstudio.sticklocker.util.PluginHollowWordsUtils.OnPluginSettingChange;
import com.lockstudio.sticklocker.view.HollowWordsDialog.OnEditTextOkClickListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 镂空字插件
 * 
 * @author 庄宏岩
 * 
 */
public class HollowWordsView extends View implements OnPluginSettingChange {
	public static float MAX_SCALE = 2.0f;
	public static final float MIN_SCALE = 0.6f;

	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	private Bitmap mBitmap;

	private PointF mCenterPoint = new PointF();
	private int mViewWidth, mViewHeight;

	private float mUpScale = 1.0f;
	private float mDownScale = 1.0f;
	private Matrix matrix = new Matrix();
	private int mViewPaddingLeft;
	private int mViewPaddingTop;

	/* 上下左右四个点 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	/* 操作按钮的点 */
	private Point mDeletePoint = new Point();
	private Point mEditPoint = new Point();

	private Drawable mDeleteDrawable, mEditDrawable;
	private int mDrawableWidth, mDrawableHeight;

	private Path mFramePath = new Path();
	private Paint mFramePaint;
	private TextPaint mUpTextPaint, mDownTextPaint;

	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 3;
	public static final int STATUS_EDIT = 5;
	private int mStatus = STATUS_INIT;
	private int framePadding = 8;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);

	private boolean isEditable = true;
	private boolean isVisiable = true;

	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();

	private int offsetX;
	private int offsetY;

	private HollowWordsStickerInfo mHollowWordsStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String upText, downText;
	private int upTextSize, downTextSize;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginHollowWordsUtils pluginHollowWordsUtils;
	private float lastX = 0;
	private float lastY = 0;
	private Typeface upTypeface, downTypeface;

	public HollowWordsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public HollowWordsView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public HollowWordsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	private void init() {
		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		mFramePaint.setColor(frameColor);
		mFramePaint.setStrokeWidth(frameWidth);
		mFramePaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mFramePaint.setPathEffect(effects);
		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}

		if (mEditDrawable == null) {
			mEditDrawable = getContext().getResources().getDrawable(R.drawable.diy_edit);
		}

		mDrawableWidth = mDeleteDrawable.getIntrinsicWidth();
		mDrawableHeight = mDeleteDrawable.getIntrinsicHeight();

		transformDraw();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 调整View的大小，位置
	 */
	public void adjustLayout() {
		int actualWidth = mViewWidth + mDrawableWidth;
		int actualHeight = mViewHeight + mDrawableHeight;

		int newPaddingLeft = (int) (mCenterPoint.x - actualWidth / 2);
		int newPaddingTop = (int) (mCenterPoint.y - actualHeight / 2);

		if (mViewPaddingLeft != newPaddingLeft || mViewPaddingTop != newPaddingTop) {
			mViewPaddingLeft = newPaddingLeft;
			mViewPaddingTop = newPaddingTop;

			mContainerLayoutParams.leftMargin = mViewPaddingLeft;
			mContainerLayoutParams.topMargin = mViewPaddingTop;
			mContainerLayoutParams.width = actualWidth;
			mContainerLayoutParams.height = actualHeight;
			mOnUpdateViewListener.updateView(this, mContainerLayoutParams);

			mHollowWordsStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mHollowWordsStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;

			layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 每次draw之前调整View的位置和大小
		adjustLayout();

		super.onDraw(canvas);

		if (mBitmap == null)
			return;
		canvas.drawBitmap(mBitmap, matrix, null);

		// 处于可编辑状态才画边框和控制图标
		if (isEditable && isVisiable) {
			mFramePath.reset();
			mFramePath.moveTo(mLTPoint.x, mLTPoint.y);
			mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
			mFramePath.lineTo(mRBPoint.x, mRBPoint.y);
			mFramePath.lineTo(mLBPoint.x, mLBPoint.y);
			mFramePath.lineTo(mLTPoint.x, mLTPoint.y);
			canvas.drawPath(mFramePath, mFramePaint);

			mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
					mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);

			mEditDrawable.setBounds(mEditPoint.x - mDrawableWidth / 2, mEditPoint.y - mDrawableHeight / 2, mEditPoint.x + mDrawableWidth / 2, mEditPoint.y
					+ mDrawableHeight / 2);
			mEditDrawable.draw(canvas);
		}
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = (int) (mBitmap.getWidth());
		int bitmapHeight = (int) (mBitmap.getHeight());
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, 0);

		// 设置缩放比例
		matrix.setScale(1.0f, 1.0f);

		// 设置画该图片的起始点
		matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);
		mHollowWordsStickerInfo.upTextSize = (int) (upTextSize * mUpScale);
		mHollowWordsStickerInfo.downTextSize = (int) (downTextSize * mDownScale);
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isEditable) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getRawX();
			lastY = event.getRawY();
			mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);

			mStatus = JudgeStatus(event.getX(), event.getY());

			break;
		case MotionEvent.ACTION_UP:

			if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mHollowWordsStickerInfo, this);
				isEditable = false;
				mStatus = STATUS_INIT;
				return true;
			}
			if (isVisiable && mStatus == STATUS_EDIT && mStatus == JudgeStatus(event.getX(), event.getY())) {
				showEditView();
				mStatus = STATUS_INIT;
				return true;
			}
			mStatus = STATUS_INIT;

			if (isVisiable) {
				float newX = event.getRawX();
				float newY = event.getRawY();
				if (Math.abs(newX - lastX) <= 10 && Math.abs(newY - lastY) <= 10) {
					showControllerView();
				}
			}
			if (!isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);

			if (mStatus == STATUS_DRAG) {
				// 修改中心点
				mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
				mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;

				adjustLayout();
			}

			mPreMovePointF.set(mCurMovePointF);
			break;
		}
		return true;
	}

	/**
	 * 获取四个点和View的大小
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param degree
	 */
	private void computeRect(int left, int top, int right, int bottom, float degree) {

		float mDegree = degree;
		if (mDegree <= 5 && mDegree >= -5) {
			mDegree = 0;
		}

		Point lt = new Point(left, top);
		Point rt = new Point(right, top);
		Point rb = new Point(right, bottom);
		Point lb = new Point(left, bottom);
		Point cp = new Point((left + right) / 2, (top + bottom) / 2);
		mLTPoint = obtainRoationPoint(cp, lt, mDegree);
		mRTPoint = obtainRoationPoint(cp, rt, mDegree);
		mRBPoint = obtainRoationPoint(cp, rb, mDegree);
		mLBPoint = obtainRoationPoint(cp, lb, mDegree);

		// 计算X坐标最大的值和最小的值
		int maxCoordinateX = getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);
		int minCoordinateX = getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);

		mViewWidth = maxCoordinateX - minCoordinateX;

		// 计算Y坐标最大的值和最小的值
		int maxCoordinateY = getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);
		int minCoordinateY = getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);

		mViewHeight = maxCoordinateY - minCoordinateY;

		// View中心点的坐标
		Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);

		offsetX = mViewWidth / 2 - viewCenterPoint.x;
		offsetY = mViewHeight / 2 - viewCenterPoint.y;

		int halfDrawableWidth = mDrawableWidth / 2;
		int halfDrawableHeight = mDrawableHeight / 2;

		// 将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
		mLTPoint.x += (offsetX + halfDrawableWidth);
		mRTPoint.x += (offsetX + halfDrawableWidth);
		mRBPoint.x += (offsetX + halfDrawableWidth);
		mLBPoint.x += (offsetX + halfDrawableWidth);

		// 将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
		mLTPoint.y += (offsetY + halfDrawableHeight);
		mRTPoint.y += (offsetY + halfDrawableHeight);
		mRBPoint.y += (offsetY + halfDrawableHeight);
		mLBPoint.y += (offsetY + halfDrawableHeight);

		mDeletePoint = LocationToPoint(LEFT_TOP);
		mEditPoint = LocationToPoint(RIGHT_TOP);
	}

	/**
	 * 根据位置判断控制图标处于那个点
	 * 
	 * @return
	 */
	private Point LocationToPoint(int location) {
		switch (location) {
		case LEFT_TOP:
			return mLTPoint;
		case RIGHT_TOP:
			return mRTPoint;
		case RIGHT_BOTTOM:
			return mRBPoint;
		case LEFT_BOTTOM:
			return mLBPoint;
		}
		return mLTPoint;
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public int getMaxValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(list.size() - 1);
	}

	/**
	 * 获取变长参数最大的值
	 * 
	 * @param array
	 * @return
	 */
	public int getMinValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(0);
	}

	/**
	 * 获取旋转某个角度之后的点
	 * 
	 * @param viewCenter
	 * @param source
	 * @param degree
	 * @return
	 */
	public static Point obtainRoationPoint(Point center, Point source, float degree) {
		// 两者之间的距离
		Point disPoint = new Point();
		disPoint.x = source.x - center.x;
		disPoint.y = source.y - center.y;

		// 没旋转之前的弧度
		double originRadian = 0;

		// 没旋转之前的角度
		double originDegree = 0;

		// 旋转之后的角度
		double resultDegree = 0;

		// 旋转之后的弧度
		double resultRadian = 0;

		// 经过旋转之后点的坐标
		Point resultPoint = new Point();

		double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y * disPoint.y);
		if (disPoint.x == 0 && disPoint.y == 0) {
			return center;
			// 第一象限
		} else if (disPoint.x >= 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.y / distance);

			// 第二象限
		} else if (disPoint.x < 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.x) / distance);
			originRadian = originRadian + Math.PI / 2;

			// 第三象限
		} else if (disPoint.x < 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.y) / distance);
			originRadian = originRadian + Math.PI;
		} else if (disPoint.x >= 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.x / distance);
			originRadian = originRadian + Math.PI * 3 / 2;
		}

		// 弧度换算成角度
		originDegree = radianToDegree(originRadian);
		resultDegree = originDegree + degree;

		// 角度转弧度
		resultRadian = degreeToRadian(resultDegree);

		resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
		resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
		resultPoint.x += center.x;
		resultPoint.y += center.y;

		return resultPoint;
	}

	/**
	 * 弧度换算成角度
	 * 
	 * @return
	 */
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	/**
	 * 角度换算成弧度
	 * 
	 * @param degree
	 * @return
	 */
	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180;
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF exitPointF = new PointF(mEditPoint);
		// 点击的点到控制旋转，缩放点的距离
		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToEdit = distance4PointF(touchPoint, exitPointF);

		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToEdit < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_EDIT;
		}
		return STATUS_DRAG;

	}

	/**
	 * 设置是否处于可缩放，平移，旋转状态
	 * 
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		invalidate();
	}

	/**
	 * 设置是否显示绘制操作按钮
	 * 
	 * @param isVisiable
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		invalidate();
	}

	/**
	 * 两个点之间的距离
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	public void setHollowWordsStickerInfo(HollowWordsStickerInfo hollowWordsStickerInfo) {
		this.mHollowWordsStickerInfo = hollowWordsStickerInfo;
		upTextSize = DensityUtil.dip2px(mContext, 14);
		downTextSize = DensityUtil.dip2px(mContext, 51);

		mUpScale = mHollowWordsStickerInfo.upTextSize * 1.0f / upTextSize;
		mDownScale = mHollowWordsStickerInfo.downTextSize * 1.0f / downTextSize;
		if (!TextUtils.isEmpty(mHollowWordsStickerInfo.upFont)) {
			upTypeface = Typeface.createFromFile(mHollowWordsStickerInfo.upFont);
		}
		if (!TextUtils.isEmpty(mHollowWordsStickerInfo.downFont)) {
			downTypeface = Typeface.createFromFile(mHollowWordsStickerInfo.downFont);
		}
		upText = mHollowWordsStickerInfo.upText;
		downText = mHollowWordsStickerInfo.downText;

		getHollowWordsBitmap();
		mCenterPoint.x = mHollowWordsStickerInfo.x + mBitmap.getWidth() / 2.0f;
		mCenterPoint.y = mHollowWordsStickerInfo.y + mBitmap.getHeight() / 2.0f;
		init();
		if (isVisiable) {
			// showControllerView();
			showChooseWordView();
		}
	}

	private void showControllerView() {
		if (controllerView == null) {
			pluginHollowWordsUtils = new PluginHollowWordsUtils(getContext());
			pluginHollowWordsUtils.setOnPluginSettingChange(this);
			pluginHollowWordsUtils.setOffset(mHollowWordsStickerInfo.offset);
			pluginHollowWordsUtils.setScale(MAX_SCALE, mUpScale, MAX_SCALE, mDownScale, true);
			pluginHollowWordsUtils.setFontPath(mHollowWordsStickerInfo.upFont, mHollowWordsStickerInfo.downFont, true);
			pluginHollowWordsUtils.setTextColor(mHollowWordsStickerInfo.upTextColor, mHollowWordsStickerInfo.downTextColor, true);
			pluginHollowWordsUtils.setTextShadowColor(mHollowWordsStickerInfo.upShadowColor, mHollowWordsStickerInfo.downShadowColor, true);
			pluginHollowWordsUtils.setAlpha(mHollowWordsStickerInfo.upAlpha, mHollowWordsStickerInfo.downAlpha);
			controllerView = pluginHollowWordsUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.removeAllViews();
			mController_container_layout.addView(controllerView);
		}
	}

	private void showChooseWordView() {
		PluginChooseWordUtils pluginChooseWordUtils = new PluginChooseWordUtils(mContext);
		pluginChooseWordUtils.setOnTextChangeListener(new OnTextChangeListener() {

			@Override
			public void textChange(String text) {
				if ("自定义字中字".equals(text)) {
					mController_container_layout.removeAllViews();
					showEditView();
				} else {
					if (text.contains("，")) {
						downText = text.substring(0, text.indexOf("，"));
						upText = text.substring(text.indexOf("，") + 1, text.length());
						mHollowWordsStickerInfo.downText = downText;
						mHollowWordsStickerInfo.upText = upText;
						getHollowWordsBitmap();
						transformDraw();
					}

				}

			}
		});
		mController_container_layout.addView(pluginChooseWordUtils.getView());
	}

	private void showEditView() {
		mController_container_layout.removeAllViews();
		HollowWordsDialog hollowWordsDialog = new HollowWordsDialog(mContext);
		hollowWordsDialog.setEditTextOkClickListener(new OnEditTextOkClickListener() {

			@Override
			public void OnEditTextOkClick(String upString, String downString) {
				upText = upString;
				downText = downString;
				mHollowWordsStickerInfo.downText = downText;
				mHollowWordsStickerInfo.upText = upText;
				getHollowWordsBitmap();
				transformDraw();
			}
		});
		hollowWordsDialog.show(upText, downText);
	}

	private void getHollowWordsBitmap() {
		mUpTextPaint = new TextPaint();
		mUpTextPaint.setAntiAlias(true);
		mUpTextPaint.setColor(mHollowWordsStickerInfo.upTextColor);
		mUpTextPaint.setTextSize(upTextSize * mUpScale);
		mUpTextPaint.setAlpha(mHollowWordsStickerInfo.upAlpha);
		mUpTextPaint.setShadowLayer(5, 0, 0, mHollowWordsStickerInfo.upShadowColor);

		if (upTypeface != null) {
			mUpTextPaint.setTypeface(upTypeface);
		}

		mDownTextPaint = new TextPaint();
		mDownTextPaint.setAntiAlias(true);
		mDownTextPaint.setColor(mHollowWordsStickerInfo.downTextColor);
		mDownTextPaint.setTextSize(downTextSize * mDownScale);
		mDownTextPaint.setAlpha(mHollowWordsStickerInfo.downAlpha);
		mDownTextPaint.setShadowLayer(5, 0, 0, mHollowWordsStickerInfo.downShadowColor);

		if (downTypeface != null) {
			mDownTextPaint.setTypeface(downTypeface);
		}

		int upWidth = (int) (mUpTextPaint.measureText(upText));
		int upHeight = (int) (mUpTextPaint.measureText("测"));

		int downWidth = (int) (mDownTextPaint.measureText(downText));
		int downHeight = (int) (mDownTextPaint.measureText("测"));

		int width = Math.max(upWidth, downWidth);
		int height = Math.max(upHeight, downHeight);

		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Bitmap upBitmap = Bitmap.createBitmap(upWidth, upHeight, Config.ARGB_8888);
		Bitmap downBitmap = Bitmap.createBitmap(downWidth, downHeight, Config.ARGB_8888);

		Rect upRect = new Rect(0, 0, upWidth, upHeight);
		Canvas upCanvas = new Canvas(upBitmap);
		FontMetricsInt upFontMetrics = mUpTextPaint.getFontMetricsInt();
		int upBaseline = upRect.top + (upRect.bottom - upRect.top - upFontMetrics.bottom + upFontMetrics.top) / 2 - upFontMetrics.top;
		mUpTextPaint.setTextAlign(Paint.Align.CENTER);
		upCanvas.drawText(upText, upRect.centerX(), upBaseline, mUpTextPaint);

		Rect downRect = new Rect(0, 0, downWidth, downHeight);
		Canvas downCanvas = new Canvas(downBitmap);
		FontMetricsInt downFontMetrics = mDownTextPaint.getFontMetricsInt();
		int downBaseline = downRect.top + (downRect.bottom - downRect.top - downFontMetrics.bottom + downFontMetrics.top) / 2 - downFontMetrics.top;
		mDownTextPaint.setTextAlign(Paint.Align.CENTER);
		downCanvas.clipRect(0, (downHeight - upHeight) * mHollowWordsStickerInfo.offset - 5, downWidth, (downHeight - upHeight)
				* mHollowWordsStickerInfo.offset + upHeight + 5, Op.XOR);
		downCanvas.drawText(downText, downRect.centerX(), downBaseline, mDownTextPaint);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Canvas canvas = new Canvas(mBitmap);
		canvas.drawBitmap(downBitmap, (width - downWidth) / 2, (height - downHeight) / 2, paint);
		canvas.drawBitmap(upBitmap, (width - upWidth) / 2, (height - upHeight) * mHollowWordsStickerInfo.offset, paint);

	}

	public void setOnRemoveViewListener(OnRemoveStickerViewListener onRemoveViewListener) {
		this.mOnRemoveViewListener = onRemoveViewListener;
	}

	public void setOnUpdateViewListener(OnUpdateViewListener onUpdateViewListener) {
		this.mOnUpdateViewListener = onUpdateViewListener;
	}

	public void setOnFocuseChangeListener(OnFocuseChangeListener onFocuseChangeListener) {
		this.mOnFocuseChangeListener = onFocuseChangeListener;
	}

	public void setContainerLayoutParams(LayoutParams containerLayoutParams) {
		this.mContainerLayoutParams = containerLayoutParams;
	}

	public void setControllerContainerLayout(LinearLayout controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	@Override
	public void changeFont(String upFontPath, String downFontPath) {
		mHollowWordsStickerInfo.upFont = upFontPath;
		if (TextUtils.isEmpty(upFontPath)) {
			upTypeface = null;
		} else {
			upTypeface = Typeface.createFromFile(mHollowWordsStickerInfo.upFont);
		}
		mHollowWordsStickerInfo.downFont = downFontPath;
		if (TextUtils.isEmpty(downFontPath)) {
			downTypeface = null;
		} else {
			downTypeface = Typeface.createFromFile(mHollowWordsStickerInfo.downFont);
		}

		getHollowWordsBitmap();
		transformDraw();

	}

	@Override
	public void changeSize(float upSize, float downSize) {
		mUpScale = upSize;
		mDownScale = downSize;
		getHollowWordsBitmap();
		transformDraw();
	}

	@Override
	public void changeColor(int upColor, int downColor) {
		mHollowWordsStickerInfo.upTextColor = upColor;
		mHollowWordsStickerInfo.downTextColor = downColor;
		getHollowWordsBitmap();
		transformDraw();

	}

	@Override
	public void changeShadowColor(int upShadowColor, int downShadowColor) {
		mHollowWordsStickerInfo.upShadowColor = upShadowColor;
		mHollowWordsStickerInfo.downShadowColor = downShadowColor;
		getHollowWordsBitmap();
		transformDraw();

	}

	@Override
	public void changeOffset(float offset) {
		mHollowWordsStickerInfo.offset = offset;
		getHollowWordsBitmap();
		transformDraw();

	}

	@Override
	public void changeAlpha(int upAlpha, int downAlpha) {
		mHollowWordsStickerInfo.upAlpha = upAlpha;
		mHollowWordsStickerInfo.downAlpha = downAlpha;
		getHollowWordsBitmap();
		transformDraw();
	}

}
