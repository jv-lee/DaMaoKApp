package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.ImagePasswordLockerInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.ChooseStickerUtils.OnImageSelectorListener;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

public class ImagePasswordLockView extends View implements OnImageSelectorListener {
	private Bitmap[] bitmaps = new Bitmap[12];
	private Bitmap defaultBitmap;
	private int mBitmapWidth;
	private int mBitmapHeight;
	private float mSquareWidth;
	private float mSquareHeight;
	private Animation mShakeAnim;
	private Vibrator mVibrator;
	private final Matrix mCircleMatrix = new Matrix();
	private float pointX[] = new float[12];
	private float pointY[] = new float[12];
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean pointInit = false;
	private boolean setPassword;
	private boolean setImage;
	private boolean mInputEnabled = true;
	private String password = "";
	private String confirmPassword = "";
	private ImagePasswordLockerInfo mPasswordLockerInfo;
	private OnLockSettingListener onLockSettingListener;
	private UnlockListener mUnlockListener;

	public static enum State {
		First, Confirm, ConfirmWrong, Done
	}

	private State mState = State.First;

	private float lastX = 0;
	private float lastY = 0;
	private int selectPosition = -1;
	private Path mSelectPath = new Path();
	private Paint mSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;

	/**
	 * 图片四个点坐标
	 */
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;

	private int offsetX;
	private int offsetY;
	private Point mDeletePoint = new Point();
	private Drawable mDeleteDrawable;
	private int mDrawableWidth, mDrawableHeight;

	private int framePadding = 8;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);
	private Path mFramePath = new Path();
	private Paint mFramePaint;

	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 2;
	private int mStatus = STATUS_INIT;

	private boolean isEditable = true;
	private boolean isVisiable = true;

	private PointF mCenterPoint = new PointF();
	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();
	private int mViewPaddingLeft;
	private int mViewPaddingTop;
	private int mViewWidth, mViewHeight;

	private OnRemoveLockerViewListener mOnRemoveViewListener;
	private OnUpdateViewListener mOnUpdateViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private LayoutParams mContainerLayoutParams;
	private LinearLayout mController_container_layout;
	private View controllerView;
	private PasswordIndView mPasswordIndView;
	private Context mContext;
	private int defaultImageSize;

	public ImagePasswordLockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
	}

	public ImagePasswordLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ImagePasswordLockView(Context context) {
		super(context);
		this.mContext = context;
	}

	public void init(Context mContext) {
		if (setImage) {
			ChooseStickerUtils chooseStickerUtils = new ChooseStickerUtils(mContext, ChooseStickerUtils.FROM_LOCKER_IMAGEPASSWORD);
			chooseStickerUtils.setOnImageSelectorListener(this);
			controllerView = chooseStickerUtils.getView();
		}

		defaultImageSize = (int) ((mContext.getResources().getDimension(R.dimen.lock_patternview_width)) / 5);
		defaultBitmap = DrawableUtils.scaleTo(DrawableUtils.getBitmap(mContext, R.drawable.default_love_image), defaultImageSize, defaultImageSize);
		mBitmapWidth = defaultBitmap.getWidth();
		mBitmapHeight = defaultBitmap.getHeight();
		for (int i = 0; i < bitmaps.length; i++) {
			Bitmap bitmap = mPasswordLockerInfo.getBitmaps()[i];
			if (bitmap != null) {
				bitmaps[i] = DrawableUtils.scaleTo(bitmap, defaultImageSize, defaultImageSize);
			} else {
				bitmaps[i] = defaultBitmap;
			}
		}
		for (Bitmap bitmap : bitmaps) {
			mBitmapWidth = Math.max(mBitmapWidth, bitmap.getWidth());
			mBitmapHeight = Math.max(mBitmapHeight, bitmap.getHeight());
		}
		mPaint.setTextSize(DensityUtil.dip2px(mContext, 15));
		mPaint.setColor(Color.RED);
		mShakeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_x);
		mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}

		mDrawableWidth = mDeleteDrawable.getIntrinsicWidth();
		mDrawableHeight = mDeleteDrawable.getIntrinsicHeight();

		mFramePaint = new Paint();
		mFramePaint.setAntiAlias(true);
		mFramePaint.setColor(frameColor);
		mFramePaint.setStrokeWidth(frameWidth);
		mFramePaint.setStyle(Style.STROKE);
		PathEffect effects = new DashPathEffect(new float[] { 15, 15, 15, 15 }, 1);
		mFramePaint.setPathEffect(effects);

		mSelectPaint = new Paint();
		mSelectPaint.setAntiAlias(true);
		mSelectPaint.setColor(Color.GRAY);
		mSelectPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
		mSelectPaint.setStyle(Style.STROKE);
		PathEffect effects2 = new DashPathEffect(new float[] { 8, 8, 8, 8 }, 1);
		mSelectPaint.setPathEffect(effects2);

		if (mViewWidth > 0 && !pointInit) {
			mSquareWidth = mViewWidth / 3f;
			mSquareHeight = mViewHeight / 4f;

			int offsizeX = mDrawableWidth / 2 + framePadding;
			int offsizeY = mDrawableHeight / 2 + framePadding;

			for (int i = 0; i < bitmaps.length; i++) {
				float x = i % 3 * mSquareWidth + mSquareWidth / 2 + offsizeX;
				float y = i / 3 * mSquareHeight + mSquareHeight / 2 + offsizeY;
				pointX[i] = x;
				pointY[i] = y;
			}
			pointInit = true;
			selectPosition = 0;
		}

		transformDraw();
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = mPasswordLockerInfo.getWidth();
		int bitmapHeight = mPasswordLockerInfo.getHeight();
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding);

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		adjustLayout();
		if (pointInit) {

			for (int i = 0; i < 12; i++) {
				float centerX = pointX[i];
				float centerY = pointY[i];

				int offsetX = (int) (mSquareWidth / 2);
				int offsetY = (int) (mSquareHeight / 2);
				drawCircle(canvas, (int) (centerX - offsetX), (int) (centerY - offsetY), i);
				if (setPassword) {
					int textWidth = (int) mPaint.measureText("0");
					canvas.drawText(i + "", centerX - textWidth / 2, centerY + textWidth / 2, mPaint);
				}
			}

			// 处于可编辑状态才画边框和控制图标
			if (isEditable && isVisiable) {
				mFramePath.reset();
				mFramePath.moveTo(mLTPoint.x, mLTPoint.y);
				mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
				mFramePath.lineTo(mRBPoint.x, mRBPoint.y);
				mFramePath.lineTo(mLBPoint.x, mLBPoint.y);
				mFramePath.lineTo(mLTPoint.x, mLTPoint.y);
				mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
				canvas.drawPath(mFramePath, mFramePaint);

				mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
						mDeletePoint.y + mDrawableHeight / 2);
				mDeleteDrawable.draw(canvas);
			}

			if (isEditable && selectPosition >= 0) {
				float centerX = pointX[selectPosition];
				float centerY = pointY[selectPosition];

				// int offsetX = (int) (mSquareWidth / 2);
				int offsetY = (int) (mSquareHeight / 2);
				int offsetX = offsetY;

				mSelectPath.reset();
				mSelectPath.moveTo(centerX - offsetX, centerY - offsetY);
				mSelectPath.lineTo(centerX + offsetX, centerY - offsetY);
				mSelectPath.lineTo(centerX + offsetX, centerY + offsetY);
				mSelectPath.lineTo(centerX - offsetX, centerY + offsetY);
				mSelectPath.lineTo(centerX - offsetX, centerY - offsetY);
				canvas.drawPath(mSelectPath, mSelectPaint);
			}
		}

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
			mPasswordLockerInfo.setY(mViewPaddingTop + mDrawableHeight / 2 + framePadding);

			layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
		}
	}

	/**
	 * 获取四个点和View的大小
	 */
	private void computeRect(int left, int top, int right, int bottom) {
		Point lt = new Point(left, top);
		Point rt = new Point(right, top);
		Point rb = new Point(right, bottom);
		Point lb = new Point(left, bottom);
		mLTPoint = lt;
		mRTPoint = rt;
		mRBPoint = rb;
		mLBPoint = lb;

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
	}

	/**
	 * 根据位置判断控制图标处于那个点
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
	 */
	public int getMaxValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(list.size() - 1);
	}

	/**
	 * 获取变长参数最大的值
	 */
	public int getMinValue(Integer... array) {
		List<Integer> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(0);
	}

	/**
	 * Whether this circle is part of the pattern.
	 */
	private void drawCircle(Canvas canvas, int leftX, int topY, int position) {
		Bitmap outerCircle = bitmaps[position];

		final int width = mBitmapWidth;
		final int height = mBitmapHeight;

		final float squareWidth = mSquareWidth;
		final float squareHeight = mSquareHeight;

		int offsetX = (int) ((squareWidth - width) / 2f);
		int offsetY = (int) ((squareHeight - height) / 2f);

		// Allow circles to shrink if the view is too small to hold them.
		float sx = Math.min(mSquareWidth / mBitmapWidth, 1.0f);
		float sy = Math.min(mSquareHeight / mBitmapHeight, 1.0f);

		mCircleMatrix.setTranslate(leftX + offsetX, topY + offsetY);
		mCircleMatrix.preTranslate(mBitmapWidth / 2, mBitmapHeight / 2);
		mCircleMatrix.preScale(sx, sy);
		mCircleMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);

		canvas.drawBitmap(outerCircle, mCircleMatrix, mPaint);
	}

	/**
	 * The call back interface for detecting patterns entered by the user.
	 */
	public interface OnLockSettingListener {
		void updateState(State state, String password);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mInputEnabled) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastX = event.getRawX();
				lastY = event.getRawY();
				mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
				mStatus = JudgeStatus(event.getX(), event.getY());
				return true;
			case MotionEvent.ACTION_MOVE:
				mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
				if (isEditable && mStatus == STATUS_DRAG) {
					// 屏幕边界的判断
					if (getTop() <= 0) {
						if (mCurMovePointF.y - mPreMovePointF.y < 0) {
							mPreMovePointF.set(mCurMovePointF);
							return false;
						}
					}

					if (getBottom() >= DeviceInfoUtils.getDeviceHeight(mContext)) {
						if (mCurMovePointF.y - mPreMovePointF.y > 0) {
							mPreMovePointF.set(mCurMovePointF);
							return false;
						}
					}

					// mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
					mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;
					adjustLayout();
					mPreMovePointF.set(mCurMovePointF);
				}
				return true;
			case MotionEvent.ACTION_UP:

				if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
					mOnRemoveViewListener.removeView(mPasswordLockerInfo, this);
					isEditable = false;
					return true;
				}
				mStatus = STATUS_INIT;

				if (setImage && !isVisiable) {
					mOnFocuseChangeListener.focuseChange(this);
				}

				float newX = event.getRawX();
				float newY = event.getRawY();
				if (Math.abs(newX - lastX) <= 10 && Math.abs(newY - lastY) <= 10) {
					float x = event.getX();
					float y = event.getY();
					final int position = checkBitmapPosition(x, y);
					if (position != -1) {
						String p = position+"";
						if (Long.parseLong(p) == 10) {
							p = "a";
						} else if (Long.parseLong(p) == 11) {
							p = "b";
						}
						if (setPassword) {
							if (mState == State.First) {
								password += p;
								mPasswordIndView.setInputPassLength(password.length());
								if (password.length() == 4) {
									mState = State.Confirm;
									mPasswordIndView.setInputPassLength(confirmPassword.length());
									setInputEnabel(false);
									handler.sendEmptyMessageDelayed(2, 100);
								}
							} else if (mState == State.Confirm || mState == State.ConfirmWrong) {
								confirmPassword += p;
								mPasswordIndView.setInputPassLength(confirmPassword.length());
								if (confirmPassword.length() == 4) {
									if (confirmPassword.equals(password)) {
										mState = State.Done;
										setInputEnabel(false);
										onLockSettingListener.updateState(mState, confirmPassword);
									} else {
										mPasswordIndView.startAnimation(mShakeAnim);
										mVibrator.vibrate(150);
										mState = State.ConfirmWrong;
										confirmPassword = "";
										setInputEnabel(false);
										handler.sendEmptyMessageDelayed(3, 300);
										onLockSettingListener.updateState(mState, confirmPassword);
									}
								}
							}

						} else if (setImage) {
							selectPosition = position;
							invalidate();
							showControllerView();
						} else {
							password += p;
							mPasswordIndView.setInputPassLength(password.length());
							if (password.length() == 4) {
								if (new LoveLockUtils(getContext()).checkPassword(password)) {
									setInputEnabel(false);
									handler.sendEmptyMessageDelayed(1, 100);
								} else {
									startAnimation(mShakeAnim);
									mPasswordIndView.startAnimation(mShakeAnim);
									mVibrator.vibrate(150);
									password = "";
									setInputEnabel(false);
									handler.sendEmptyMessageDelayed(0, 300);
								}
							}
						}

					}
				}
				return true;
			}
			return false;
		}
		return false;
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mPasswordIndView.setInputPassLength(password.length());
				setInputEnabel(true);
				break;
			case 1:
				setInputEnabel(true);
				mUnlockListener.OnUnlockSuccess();
				break;
			case 2:
				setInputEnabel(true);
				onLockSettingListener.updateState(mState, "");
				break;
			case 3:
				mPasswordIndView.setInputPassLength(confirmPassword.length());
				setInputEnabel(true);
				break;
			default:
				break;
			}
			return false;
		}
	});

	private int checkBitmapPosition(float x, float y) {
		float offsetX = mSquareWidth / 2;
		float offsetY = mSquareHeight / 2;
		for (int i = 0; i < 12; i++) {
			float px = pointX[i];
			float py = pointY[i];

			if (x > px - offsetX && x < px + offsetX && y > py - offsetY && y < py + offsetY) {
				return i;
			}

		}

		return -1;
	}

	private int resolveMeasured(int measureSpec, int desired) {
		int result = 0;
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.UNSPECIFIED:
			result = desired;
			break;
		case MeasureSpec.AT_MOST:
			result = Math.max(specSize, desired);
			break;
		case MeasureSpec.EXACTLY:
		default:
			result = specSize;
		}
		return result;
	}

	@Override
	protected int getSuggestedMinimumWidth() {
		return 3 * mBitmapWidth;
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		return 4 * mBitmapHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int minimumWidth = getSuggestedMinimumWidth();
		final int minimumHeight = getSuggestedMinimumHeight();
		int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
		int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
		setMeasuredDimension(viewWidth, viewHeight);
	}

	public void setPassword(boolean b) {
		setPassword = b;
	}

	public void setImage(boolean b) {
		setImage = b;
	}

	public void setInputEnabel(boolean mInputEnabled) {
		this.mInputEnabled = mInputEnabled;
	}

	public void updateImage() {
		for (int i = 0; i < bitmaps.length; i++) {
			Bitmap bitmap = mPasswordLockerInfo.getBitmaps()[i];
			if (bitmap != null) {
				bitmaps[i] = DrawableUtils.scaleTo(bitmap, defaultImageSize, defaultImageSize);
			} else {
				bitmaps[i] = defaultBitmap;
			}
		}
		mBitmapWidth = 0;
		mBitmapHeight = 0;
		for (Bitmap bitmap : bitmaps) {
			mBitmapWidth = Math.max(mBitmapWidth, bitmap.getWidth());
			mBitmapHeight = Math.max(mBitmapHeight, bitmap.getHeight());
		}
		invalidate();
	}

	public void updateState(State state) {
		this.mState = state;
		password = "";
		confirmPassword = "";
		mPasswordIndView.setInputPassLength(0);
		setInputEnabel(true);
	}

	public void setPasswordIndView(PasswordIndView passwordIndView) {
		this.mPasswordIndView = passwordIndView;
	}

	public void setOnLockSettingListener(OnLockSettingListener onLockSettingListener) {
		this.onLockSettingListener = onLockSettingListener;
	}

	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}

	/**
	 * 设置是否处于可平移状态
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		selectPosition = -1;
		invalidate();
	}

	/**
	 * 设置是否显示绘制操作按钮
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		selectPosition = -1;
		invalidate();
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);

		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		return STATUS_DRAG;

	}

	/**
	 * 两个点之间的距离
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	private void showControllerView() {
		if (controllerView != null && controllerView.getParent() == null) {
			LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_LOCKER_IMAGEPASSWORD);
			mController_container_layout.addView(controllerView);
		}
	}

	public void setPasswordLockerInfo(ImagePasswordLockerInfo passwordLockerInfo) {
		this.mPasswordLockerInfo = passwordLockerInfo;
		mCenterPoint.x = mPasswordLockerInfo.getX() + mPasswordLockerInfo.getWidth() / 2;
		mCenterPoint.y = mPasswordLockerInfo.getY() + mPasswordLockerInfo.getHeight() / 2;
		mViewWidth = mPasswordLockerInfo.getWidth();
		mViewHeight = mPasswordLockerInfo.getHeight();
		init(getContext());
		if (isVisiable) {
			showControllerView();
		}
	}

	public void setOnRemoveViewListener(OnRemoveLockerViewListener onRemoveViewListener) {
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

	public int getTopMargin() {
		return mViewPaddingTop;
	}

	@Override
	public void selectImage(String imageUrl) {
		if (selectPosition != -1) {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
			if (bitmap != null) {
				mPasswordLockerInfo.getBitmaps()[selectPosition] = bitmap;
				updateImage();
			}
		}

	}

	public void selectImage(Bitmap bitmap) {
		if (selectPosition != -1) {
			if (bitmap != null) {
				mPasswordLockerInfo.getBitmaps()[selectPosition] = bitmap;
				updateImage();
			}
		}
	}
}
