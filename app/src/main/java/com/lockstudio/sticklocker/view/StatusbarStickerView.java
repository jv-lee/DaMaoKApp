package com.lockstudio.sticklocker.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
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
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.StatusbarStickerInfo;
import com.lockstudio.sticklocker.util.DateTime;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.PluginStatusUtils;
import com.lockstudio.sticklocker.util.PluginStatusUtils.OnPluginSettingChange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * 单手对图片进行缩放，旋转，平移操作，详情请查看
 * 
 */
public class StatusbarStickerView extends View implements OnPluginSettingChange {
	/**
	 * 图片的最大缩放比例
	 */
	public static final float MAX_SCALE = 10.0f;

	/**
	 * 图片的最小缩放比例
	 */
	public static final float MIN_SCALE = 0.3f;

	/**
	 * 一些默认的常量
	 */
	public static final int DEFAULT_FRAME_PADDING = 8;
	public static final int DEFAULT_FRAME_COLOR = Color.GRAY;
	public static final float DEFAULT_SCALE = 1.0f;
	public static final float DEFAULT_DEGREE = 0;

	/**
	 * 用于旋转缩放的Bitmap
	 */
	private Bitmap mBitmap;

	/**
	 * SingleTouchView的中心点坐标，相对于其父类布局而言的
	 */
	private PointF mCenterPoint = new PointF();

	/**
	 * View的宽度和高度，随着图片的旋转而变化(不包括控制旋转，缩放图片的宽高)
	 */
	private int mViewWidth, mViewHeight;

	/**
	 * 图片的旋转角度
	 */
	private float mDegree = DEFAULT_DEGREE;

	/**
	 * 图片的缩放比例
	 */
	private float mScale = DEFAULT_SCALE;

	/**
	 * 用于缩放，旋转，平移的矩阵
	 */
	private Matrix matrix = new Matrix();

	/**
	 * SingleTouchView距离父类布局的左间距
	 */
	private int mViewPaddingLeft;

	/**
	 * SingleTouchView距离父类布局的上间距
	 */
	private int mViewPaddingTop;
	/**
	 * 初始状态
	 */
	private final int STATUS_INIT = 0;
	/**
	 * 当前所处的状态
	 */
	private int mStatus = STATUS_INIT;
	/**
	 * 拖动状态
	 */
	private final int STATUS_DRAG = 1;
	/**
	 * 删除
	 */
	private final int STATUS_DELETE = 3;

	/**
	 * 图片四个点坐标
	 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	/**
	 * 缩放，旋转图标的宽和高
	 */
	private int mDrawableWidth, mDrawableHeight;

	/**
	 * 用于缩放，旋转的图标
	 */
	private Drawable mDeleteDrawable;
	private Point mDeletePoint = new Point();

	/**
	 * 画外围框的Path
	 */
	private Path mPath = new Path();

	/**
	 * 画外围框的画笔
	 */
	private Paint mPaint;
	private Paint mTextPaint;
	private Paint mTextPaint1;
	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	private final int LEFT_BOTTOM = 0;

	/**
	 * 外边框与图片之间的间距, 单位是dip
	 */
	private int framePadding = DEFAULT_FRAME_PADDING;

	/**
	 * 外边框颜色
	 */
	private int frameColor = DEFAULT_FRAME_COLOR;

	/**
	 * 外边框线条粗细, 单位是 dip
	 */
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);

	/**
	 * 是否处于可以缩放，平移，旋转状态
	 */
	private boolean isEditable = true;
	private boolean isVisiable = true;

	/**
	 * 图片在旋转时x方向的偏移量
	 */
	private int offsetX;
	/**
	 * 图片在旋转时y方向的偏移量
	 */
	private int offsetY;

	private StatusbarStickerInfo mStatusbarStickerInfo;
	private OnRemoveStickerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private String text;
	private String text1;
	private String text2;
	private int textSize1;
	private int textSize2;
	private int width;

	private LinearLayout mController_container_layout;
	private View controllerView;
	private Context mContext;
	private PluginStatusUtils pluginStatusUtils;
	private Thread mThread;
	private boolean flag = true;
	private Bitmap lockBitmap;
	private int lockRes;
	private String statusString = "unknown";
	private boolean timerLoop = false;

	private static final int MSG_START_TIMER_LOOP = 300;
	private static final int UPDATE_MY_TV = 1;
	private Message message = null;
	private Typeface typeface;

	public StatusbarStickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}

	public StatusbarStickerView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public StatusbarStickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(frameColor);
		mPaint.setStrokeWidth(frameWidth);
		mPaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mPaint.setPathEffect(effects);

		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
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

			mStatusbarStickerInfo.x = mViewPaddingLeft + mDrawableWidth / 2 + framePadding;
			mStatusbarStickerInfo.y = mViewPaddingTop + mDrawableHeight / 2 + framePadding;

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
			mPath.reset();
			mPath.moveTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			mPath.lineTo(mRBPoint.x, mRBPoint.y);
			mPath.lineTo(mLBPoint.x, mLBPoint.y);
			mPath.lineTo(mLTPoint.x, mLTPoint.y);
			// mPath.lineTo(mRTPoint.x, mRTPoint.y);
			canvas.drawPath(mPath, mPaint);

			mDeleteDrawable.setBounds((mRBPoint.x - mDeletePoint.x) / 2, mDeletePoint.y - mDrawableHeight / 2, mDrawableWidth / 2 + mDeletePoint.x
					+ (mRBPoint.x - mDeletePoint.x) / 2, mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);
		}
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
		int bitmapHeight = (int) (mBitmap.getHeight() * mScale);
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, mDegree);

		// 设置缩放比例
		matrix.setScale(mScale, mScale);
		// 绕着图片中心进行旋转
		matrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
		// 设置画该图片的起始点
		matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);

		mStatusbarStickerInfo.angle = (int) mDegree;
		mStatusbarStickerInfo.textSize = (int) (textSize1 * mScale);
		mStatusbarStickerInfo.text = text;
		mStatusbarStickerInfo.textRes = lockRes;
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isEditable) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mStatus = JudgeStatus(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:

			if (mStatus == STATUS_DELETE && isVisiable && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mStatusbarStickerInfo, this);
				isEditable = false;
				mStatus = STATUS_INIT;
				return true;
			}
			mStatus = STATUS_INIT;

			if (!isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}
			showControllerView();
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
		Point lt = new Point(left, top);
		Point rt = new Point(right, top);
		Point rb = new Point(right, bottom);
		Point lb = new Point(left, bottom);
		Point cp = new Point((left + right) / 2, (top + bottom) / 2);
		mLTPoint = obtainRoationPoint(cp, lt, degree);
		mRTPoint = obtainRoationPoint(cp, rt, degree);
		mRBPoint = obtainRoationPoint(cp, rb, degree);
		mLBPoint = obtainRoationPoint(cp, lb, degree);

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

		mDeletePoint = LocationToPoint(LEFT_BOTTOM);
	}

	/**
	 * 根据位置判断控制图标处于那个点
	 * 
	 * @return
	 */
	private Point LocationToPoint(int location) {
		switch (location) {
		case LEFT_BOTTOM:
			return mLBPoint;
		}
		return mLBPoint;
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
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		// 点击的点到控制旋转，缩放点的距离
		float distanceToDelete = distance4PointF(touchPoint, deletePointF);

		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		return STATUS_DRAG;

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
	 * 设置图片旋转角度
	 * 
	 * @param degree
	 */
	public void setImageDegree(float degree) {
		if (this.mDegree != degree) {
			this.mDegree = degree;
			transformDraw();
		}
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
		float disX = mRBPoint.x / 2 - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 监听电池变化
	 */
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int level = intent.getIntExtra("level", 0);
				int temperature = intent.getIntExtra("temperature", 0);
				int status = intent.getIntExtra("status", 0);

				switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					statusString = "unknown";
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					statusString = "charging";
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					statusString = "discharging";
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					statusString = "not charging";
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					statusString = "full";
					break;
				}

				text1 = String.valueOf(level) + "%";
				getNewBitMap();
			}
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				startTimer();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				stopTimer();
			}
		}
	};

	public void setStatusbarStickerInfo(StatusbarStickerInfo statusbarStickerInfo) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		this.mContext.registerReceiver(mBatInfoReceiver, filter);
		this.mStatusbarStickerInfo = statusbarStickerInfo;
		textSize1 = mStatusbarStickerInfo.textSize;
		text = mStatusbarStickerInfo.text;
		lockRes = mStatusbarStickerInfo.textRes;
		text1 = "10%";
		text2 = "10%";
		if (text == null || "".equals(text))
			text = "文字锁屏";
		if (!TextUtils.isEmpty(statusbarStickerInfo.font)) {
			typeface = Typeface.createFromFile(statusbarStickerInfo.font);
		}
		getNewBitMap();
		startTimer();
		Matrix matrix = new Matrix();
		matrix.postRotate(mStatusbarStickerInfo.angle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		mCenterPoint.x = mStatusbarStickerInfo.x + b.getWidth() / 2.0f;
		mCenterPoint.y = mStatusbarStickerInfo.y + b.getHeight() / 2.0f;
		mDegree = mStatusbarStickerInfo.angle;
		init();
		if (isVisiable) {
			showControllerView();
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_TIMER_LOOP:
				handler.removeMessages(MSG_START_TIMER_LOOP);
				text2 = DateTime.getTime();
				getNewBitMap();
				transformDraw();
				if (timerLoop) {
					handler.sendEmptyMessageDelayed(MSG_START_TIMER_LOOP, 1000);
				}
				break;

			default:
				break;
			}
			return false;
		}
	}) {
	};

	/**
	 * 实时更新时间
	 */
	private void startTimer() {
		// 实时更新时间
		if (timerLoop)
			return;
		timerLoop = true;
		handler.sendEmptyMessage(MSG_START_TIMER_LOOP);
	}

	private void stopTimer() {
		timerLoop = false;
	}

	/**
	 * 判断是否离开view
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == 8) {
			stopTimer();
			if (mBatInfoReceiver != null) {
				this.mContext.unregisterReceiver(mBatInfoReceiver);
				mBatInfoReceiver = null;
			}
		}
		if (visibility == 0) {
			startTimer();
		}
	}

	// 显示文字操作栏
	private void showControllerView() {
		if (controllerView == null) {
			pluginStatusUtils = new PluginStatusUtils(getContext());
			pluginStatusUtils.setOnPluginSettingChange(this);
			pluginStatusUtils.initSelectData(mStatusbarStickerInfo.font, mStatusbarStickerInfo.textColor, mStatusbarStickerInfo.shadowColor,mStatusbarStickerInfo.text);
			controllerView = pluginStatusUtils.getView();
		}
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.addView(controllerView);
		}
	}

	private void getNewBitMap() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mStatusbarStickerInfo.textColor);
		mTextPaint.setTextSize(textSize1);
		mTextPaint.setShadowLayer(5, 0, 0, mStatusbarStickerInfo.shadowColor);
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
		}

		width = LockApplication.getInstance().getConfig().getScreenWidth();
		int textWidth = (int) (mTextPaint.measureText(text));
		int textWidth1 = (int) (mTextPaint.measureText(text1));
		int textHeight = (int) (mTextPaint.measureText("测"));

		Bitmap batteryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.battery);
		int batteryWidth = batteryBitmap.getWidth();
		int height = batteryWidth > textHeight ? batteryWidth : textHeight;

		mBitmap = Bitmap.createBitmap(width + DensityUtil.dip2px(mContext, 5), height + DensityUtil.dip2px(mContext, 5), Config.ARGB_8888);
		Rect targetRect = new Rect(DensityUtil.dip2px(mContext, 5), DensityUtil.dip2px(mContext, 5), width + DensityUtil.dip2px(mContext, 5), height
				+ DensityUtil.dip2px(mContext, 5));
		Canvas canvas = new Canvas(mBitmap);
		FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
		int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text, textWidth / 2 + DensityUtil.dip2px(mContext, 5), baseline - DensityUtil.dip2px(mContext, 5) / 2, mTextPaint);
		canvas.drawText(text1, width - textWidth1 / 2 - DensityUtil.dip2px(mContext, 5), baseline - DensityUtil.dip2px(mContext, 5) / 2, mTextPaint);
		canvas.drawText(text2, targetRect.centerX(), baseline - DensityUtil.dip2px(mContext, 5) / 2, mTextPaint);

		int bitmapline = (targetRect.bottom - targetRect.top - batteryWidth) / 2;
		if (statusString.equals("charging")) {
			canvas.drawBitmap(batteryBitmap, width - textWidth1 - DensityUtil.dip2px(mContext, 10) - batteryWidth, bitmapline, mTextPaint);
		}
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
	public void change(String fontPath, String fontUrl, String text, int color, int shadowColor) {
		if (TextUtils.isEmpty(fontPath)) {
			typeface = null;
		} else {
			if (!fontPath.equals(mStatusbarStickerInfo.font)) {
				mStatusbarStickerInfo.font = fontPath;
				typeface = Typeface.createFromFile(mStatusbarStickerInfo.font);
			}
		}

		if (color == shadowColor) {
			shadowColor = 0x00000000;
		}
		if (text != null) {
			mStatusbarStickerInfo.text = text;
			this.text = text;
		}
		mStatusbarStickerInfo.textColor = color;
		mStatusbarStickerInfo.font = fontPath;
		mStatusbarStickerInfo.shadowColor = shadowColor;
		mStatusbarStickerInfo.fontUrl = fontUrl;

		getNewBitMap();
		transformDraw();
	}

}
