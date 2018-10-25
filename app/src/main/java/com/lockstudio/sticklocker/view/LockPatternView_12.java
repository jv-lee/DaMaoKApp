package com.lockstudio.sticklocker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.activity.SelectImageActivity;
import com.lockstudio.sticklocker.model.TwelvePatternLockerInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.MConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

/**
 * Displays and detects the user's unlock attempt, which is a drag of a finger
 * across 9 regions of the screen.
 * 
 * Is also capable of displaying a static pattern in "in progress", "wrong" or
 * "correct" states.
 * 
 * @author way
 */
public class LockPatternView_12 extends View implements ChooseStickerUtils.OnImageSelectorListener {
	private static final boolean PROFILE_DRAWING = false;
	private boolean mDrawingProfilingStarted = false;
	private Paint mPaint = new Paint();
	private Paint mPathPaint = new Paint();
	private Context mContext;

	private OnPatternListener mOnPatternListener;
	private UnlockListener mUnlockListener;
	private ArrayList<Cell> mPattern = new ArrayList<Cell>(12);
	private boolean[][] mPatternDrawLookup = new boolean[4][3];
	private float mInProgressX = -1;
	private float mInProgressY = -1;

	private DisplayMode mPatternDisplayMode = DisplayMode.Correct;
	private boolean mInputEnabled = true;
	private boolean mInStealthMode = false;
	private boolean mEnableHapticFeedback = true;
	private boolean mPatternInProgress = false;

	private float mDiameterFactor = 0.10f;
	private final int mStrokeAlpha = 128;
	private float mHitFactor = 0.6f;

	private float mSquareWidth;
	private float mSquareHeight;

	private final Path mCurrentPath = new Path();
	private final Rect mInvalidate = new Rect();

	private int mBitmapWidth;
	private int mBitmapHeight;

	private final Matrix mCircleMatrix = new Matrix();
	private Bitmap[] bitmaps = new Bitmap[12];

	/**
	 * Represents a cell in the 3 X 3 matrix of the unlock pattern view.
	 */
	public static class Cell {
		int row;
		int column;

		// keep # objects limited to 12
		static Cell[][] sCells = new Cell[4][3];
		static {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++) {
					sCells[i][j] = new Cell(i, j);
				}
			}
		}

		/**
		 * @param row
		 *            The row of the cell.
		 * @param column
		 *            The column of the cell.
		 */
		private Cell(int row, int column) {
			checkRange(row, column);
			this.row = row;
			this.column = column;
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}

		/**
		 * @param row
		 *            The row of the cell.
		 * @param column
		 *            The column of the cell.
		 */
		public static synchronized Cell of(int row, int column) {
			checkRange(row, column);
			return sCells[row][column];
		}

		private static void checkRange(int row, int column) {
			if (row < 0 || row > 3) {
				throw new IllegalArgumentException("row must be in range 0-3");
			}
			if (column < 0 || column > 2) {
				throw new IllegalArgumentException("column must be in range 0-2");
			}
		}

		public String toString() {
			return "(row=" + row + ",clmn=" + column + ")";
		}
	}

	/**
	 * How to display the current pattern.
	 */
	public enum DisplayMode {

		/**
		 * The pattern drawn is correct (i.e draw it in a friendly color)
		 */
		Correct,

		/**
		 * Animate the pattern (for demo, and help).
		 */
		Animate,

		/**
		 * The pattern is wrong (i.e draw a foreboding color)
		 */
		Wrong
	}

	/**
	 * The call back interface for detecting patterns entered by the user.
	 */
	public static interface OnPatternListener {

		/**
		 * A new pattern has begun.
		 */
		void onPatternStart();

		/**
		 * The pattern was cleared.
		 */
		void onPatternCleared();

		/**
		 * The user extended the pattern currently being drawn by one cell.
		 * 
		 * @param pattern
		 *            The pattern with newly added cell.
		 */
		void onPatternCellAdded(List<Cell> pattern);

		/**
		 * A pattern was detected from the user.
		 * 
		 * @param pattern
		 *            The pattern.
		 */
		void onPatternDetected(List<Cell> pattern);
	}

	private Bitmap emptyBitmap;
	private Bitmap defaultBitmap;
	private Animation mShakeAnim;
	private Vibrator mVibrator;

	private boolean setImage;

	private float lastX = 0;
	private float lastY = 0;
	private Cell selectCell = null;
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
	private TwelvePatternLockerInfo mTwelvePatternLockerInfo;
	private int defaultImageSize;

	public LockPatternView_12(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public LockPatternView_12(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void init() {
//		if(setImage){
//			ChooseStickerUtils chooseStickerUtils = new ChooseStickerUtils(mContext, ChooseStickerUtils.FROM_LOCKER_TWELVE);
//			chooseStickerUtils.setOnImageSelectorListener(this);
//			controllerView = chooseStickerUtils.getView();
//		}

		setClickable(true);

		mPathPaint.setAntiAlias(true);
		mPathPaint.setDither(true);
		mPathPaint.setColor(Color.YELLOW);
		mPathPaint.setAlpha(mStrokeAlpha);
		mPathPaint.setStyle(Style.STROKE);
		mPathPaint.setStrokeJoin(Paint.Join.ROUND);
		mPathPaint.setStrokeCap(Paint.Cap.ROUND);

		defaultImageSize = (int) ((mContext.getResources().getDimension(R.dimen.lock_patternview_width)) / 4.5);
		emptyBitmap = DrawableUtils.scaleTo(getBitmapFor(R.drawable.lock_pattern_empty), defaultImageSize, defaultImageSize);
		defaultBitmap = DrawableUtils.scaleTo(getBitmapFor(R.drawable.lock_pattern_default_12), defaultImageSize, defaultImageSize);
		for (int i = 0; i < bitmaps.length; i++) {
			Bitmap bitmap = mTwelvePatternLockerInfo.getBitmaps()[i];
			if (bitmap != null) {
				bitmaps[i] = DrawableUtils.scaleTo(bitmap, defaultImageSize, defaultImageSize);
			} else {
				bitmaps[i] = emptyBitmap;
			}
		}

		// bitmaps have the size of the largest bitmap in this group
		for (Bitmap bitmap : bitmaps) {
			mBitmapWidth = Math.max(mBitmapWidth, bitmap.getWidth());
			mBitmapHeight = Math.max(mBitmapHeight, bitmap.getHeight());
		}

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

		transformDraw();
	}

	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw() {
		int bitmapWidth = mTwelvePatternLockerInfo.getWidth();
		int bitmapHeight = mTwelvePatternLockerInfo.getHeight();
		computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding);

		invalidate();
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
			mTwelvePatternLockerInfo.setY(mViewPaddingTop + mDrawableHeight / 2 + framePadding);

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

	private Bitmap getBitmapFor(int resId) {
		return BitmapFactory.decodeResource(getContext().getResources(), resId);
	}

	/**
	 * @return Whether the view is in stealth mode.
	 */
	public boolean isInStealthMode() {
		return mInStealthMode;
	}

	/**
	 * @return Whether the view has tactile feedback enabled.
	 */
	public boolean isTactileFeedbackEnabled() {
		return mEnableHapticFeedback;
	}

	/**
	 * Set whether the view is in stealth mode. If true, there will be no
	 * visible feedback as the user enters the pattern.
	 *
	 * @param inStealthMode
	 *            Whether in stealth mode.
	 */
	public void setInStealthMode(boolean inStealthMode) {
		mInStealthMode = inStealthMode;
	}

	/**
	 * Set whether the view will use tactile feedback. If true, there will be
	 * tactile feedback as the user enters the pattern.
	 *
	 * @param tactileFeedbackEnabled
	 *            Whether tactile feedback is enabled
	 */
	public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
		mEnableHapticFeedback = tactileFeedbackEnabled;
	}

	/**
	 * Set the call back for pattern detection.
	 *
	 * @param onPatternListener
	 *            The call back.
	 */
	public void setOnPatternListener(OnPatternListener onPatternListener) {
		mOnPatternListener = onPatternListener;
	}

	/**
	 * Set the pattern explicitely (rather than waiting for the user to input a
	 * pattern).
	 *
	 * @param displayMode
	 *            How to display the pattern.
	 * @param pattern
	 *            The pattern.
	 */
	public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
		mPattern.clear();
		mPattern.addAll(pattern);
		clearPatternDrawLookup();
		for (Cell cell : pattern) {
			mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
		}

		setDisplayMode(displayMode);
	}

	/**
	 * Set the display mode of the current pattern. This can be useful, for
	 * instance, after detecting a pattern to tell this view whether change the
	 * in progress result to correct or wrong.
	 *
	 * @param displayMode
	 *            The display mode.
	 */
	public void setDisplayMode(DisplayMode displayMode) {
		mPatternDisplayMode = displayMode;
		if (displayMode == DisplayMode.Animate) {
			if (mPattern.size() == 0) {
				throw new IllegalStateException("you must have a pattern to " + "animate if you want to setUrlImage the display mode to animate");
			}
			final Cell first = mPattern.get(0);
			mInProgressX = getCenterXForColumn(first.getColumn());
			mInProgressY = getCenterYForRow(first.getRow());
			clearPatternDrawLookup();
		}
		invalidate();
	}

	private void notifyCellAdded() {
		if (mOnPatternListener != null) {
			mOnPatternListener.onPatternCellAdded(mPattern);
		}
	}

	private void notifyPatternStarted() {
		if (mOnPatternListener != null) {
			mOnPatternListener.onPatternStart();
		} else {
			removeCallbacks(mClearPatternRunnable);
		}
	}

	private void notifyPatternDetected() {
		if (mOnPatternListener != null) {
			mOnPatternListener.onPatternDetected(mPattern);
		}

		if (mPattern != null) {
			if (mUnlockListener != null) {
				gesturepwd_unlock_textview.setVisibility(View.VISIBLE);
				if (new LockPatternUtils_12(mContext).checkPattern(mPattern)) {
					setDisplayMode(DisplayMode.Correct);
					mUnlockListener.OnUnlockSuccess();
				} else {
					startAnimation(mShakeAnim);
					mVibrator.vibrate(150);
					setDisplayMode(DisplayMode.Wrong);
					if (mPattern.size() >= LockPatternUtils_12.MIN_PATTERN_REGISTER_FAIL) {
						mFailedPatternAttemptsSinceLastTimeout++;
						int retry = LockPatternUtils_12.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
						if (retry >= 0) {
							if (retry == 0) {
								gesturepwd_unlock_textview.setText("错误次数过多,请" + LockPatternUtils_12.LOCKED_TIME + "秒后再试");
							} else {
								gesturepwd_unlock_textview.setText("密码错误");
							}
						}

					} else {
						gesturepwd_unlock_textview.setText("输入长度不够，请重试");
					}

					if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils_12.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
						mHandler.postDelayed(attemptLockout, 1000);
					} else {
						postDelayed(mClearPatternRunnable, 1000);
					}
				}
			}
		}
	}

	private void notifyPatternCleared() {
		if (mOnPatternListener != null) {
			mOnPatternListener.onPatternCleared();
		} else {
			removeCallbacks(mClearPatternRunnable);
		}

	}

	/**
	 * Clear the pattern.
	 */
	public void clearPattern() {
		resetPattern();
	}

	/**
	 * Reset all pattern state.
	 */
	private void resetPattern() {
		mPattern.clear();
		clearPatternDrawLookup();
		mPatternDisplayMode = DisplayMode.Correct;
		invalidate();
	}

	/**
	 * Clear the pattern lookup table.
	 */
	private void clearPatternDrawLookup() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				mPatternDrawLookup[i][j] = false;
			}
		}
	}

	/**
	 * Disable input (for instance when displaying a message that will timeout
	 * so user doesn't get view into messy state).
	 */
	public void disableInput() {
		mInputEnabled = false;
	}

	/**
	 * Enable input.
	 */
	public void enableInput() {
		mInputEnabled = true;
	}

	//
	// @Override
	// protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// final int width = w - getPaddingX() - getPaddingRight();
	// mSquareWidth = width / 3.0f;
	//
	// final int height = h - getPaddingY() - getPaddingBottom();
	// mSquareHeight = height / 4.0f;
	// }

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
		// View should be large enough to contain 3 side-by-side target bitmaps
		return 3 * mBitmapWidth;
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		// View should be large enough to contain 3 side-by-side target bitmaps
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

	/**
	 * Determines whether the point x, y will add a new point to the current
	 * pattern (in addition to finding the cell, also makes heuristic choices
	 * such as filling in gaps based on current pattern).
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 */
	private Cell detectAndAddHit(float x, float y) {
		final Cell cell = checkForNewHit(x, y);
		if (cell != null) {
			// check for gaps in existing pattern
			Cell fillInGapCell = null;
			final ArrayList<Cell> pattern = mPattern;
			if (!pattern.isEmpty()) {
				final Cell lastCell = pattern.get(pattern.size() - 1);
				int dRow = cell.row - lastCell.row;
				int dColumn = cell.column - lastCell.column;

				int fillInRow = lastCell.row;
				int fillInColumn = lastCell.column;
				if (Math.abs(dRow) == 3 && Math.abs(dColumn) != 1) {
					fillInRow = lastCell.row + ((dRow > 0) ? 1 : -1);
				}

				if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
					fillInColumn = lastCell.column + ((dColumn > 0) ? 1 : -1);
				}

				fillInGapCell = Cell.of(fillInRow, fillInColumn);
			}

			if (fillInGapCell != null && !mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
				addCellToPattern(fillInGapCell);
			}
			addCellToPattern(cell);
			if (mEnableHapticFeedback) {
				performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
						| HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			}
			return cell;
		}
		return null;
	}

	private void addCellToPattern(Cell newCell) {
		mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
		mPattern.add(newCell);
		notifyCellAdded();
	}

	// helper method to find which cell a point maps to
	private Cell checkForNewHit(float x, float y) {

		final int rowHit = getRowHit(y);
		if (rowHit < 0) {
			return null;
		}
		final int columnHit = getColumnHit(x);
		if (columnHit < 0) {
			return null;
		}

		if (mPatternDrawLookup[rowHit][columnHit]) {
			return null;
		}
		return Cell.of(rowHit, columnHit);
	}

	/**
	 * Helper method to find the row that y falls into.
	 * 
	 * @param y
	 *            The y coordinate
	 * @return The row that y falls in, or -1 if it falls in no row.
	 */
	private int getRowHit(float y) {

		final float squareHeight = mSquareHeight;
		float hitSize = squareHeight * mHitFactor;

		float offset = getPaddingY() + (squareHeight - hitSize) / 2f;
		for (int i = 0; i < 4; i++) {

			final float hitTop = offset + squareHeight * i;
			if (y >= hitTop && y <= hitTop + hitSize) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper method to find the column x fallis into.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @return The column that x falls in, or -1 if it falls in no column.
	 */
	private int getColumnHit(float x) {
		final float squareWidth = mSquareWidth;
		float hitSize = squareWidth * mHitFactor;

		float offset = getPaddingX() + (squareWidth - hitSize) / 2f;
		for (int i = 0; i < 3; i++) {

			final float hitLeft = offset + squareWidth * i;
			if (x >= hitLeft && x <= hitLeft + hitSize) {
				return i;
			}
		}
		return -1;
	}

	public int postion;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mInputEnabled || !isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (setImage) {
				lastX = event.getRawX();
				lastY = event.getRawY();
				mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
				mStatus = JudgeStatus(event.getX(), event.getY());
			} else {
				handleActionDown(event);
			}
			return true;
		case MotionEvent.ACTION_UP:

			if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mTwelvePatternLockerInfo, this);
				isEditable = false;
				return true;
			}
			mStatus = STATUS_INIT;

			if (setImage && !isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}
			if (setImage) {
				float newX = event.getRawX();
				float newY = event.getRawY();
				if (Math.abs(newX - lastX) <= 10 && Math.abs(newY - lastY) <= 10) {
					float x = event.getX();
					float y = event.getY();
					Cell cell = checkForNewHit(x, y);
					if (cell != null) {
						selectCell = cell;
						selectPosition = cell.row * 3 + cell.column;
						invalidate();
						showControllerView();
					}
				}
			} else {
				handleActionUp(event);
			}
			return true;

		case MotionEvent.ACTION_MOVE:
			if (setImage) {
				mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
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

				if (isEditable && mStatus == STATUS_DRAG) {
					mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;
					adjustLayout();
					mPreMovePointF.set(mCurMovePointF);
				}
			} else {
				handleActionMove(event);
			}
			return true;
		case MotionEvent.ACTION_CANCEL:
			if (setImage) {

			} else {
				resetPattern();
				mPatternInProgress = false;
				notifyPatternCleared();
				if (PROFILE_DRAWING) {
					if (mDrawingProfilingStarted) {
						Debug.stopMethodTracing();
						mDrawingProfilingStarted = false;
					}
				}
			}
			return true;
		}
		return false;
	}

	private void handleActionMove(MotionEvent event) {
		// Handle all recent motion events so we don't skip any cells even when
		// the device
		// is busy...
		final int historySize = event.getHistorySize();
		for (int i = 0; i < historySize + 1; i++) {
			final float x = i < historySize ? event.getHistoricalX(i) : event.getX();
			final float y = i < historySize ? event.getHistoricalY(i) : event.getY();
			final int patternSizePreHitDetect = mPattern.size();
			Cell hitCell = detectAndAddHit(x, y);
			final int patternSize = mPattern.size();
			if (hitCell != null && patternSize == 1) {
				mPatternInProgress = true;
				notifyPatternStarted();
			}
			// note current x and y for rubber banding of in progress patterns
			final float dx = Math.abs(x - mInProgressX);
			final float dy = Math.abs(y - mInProgressY);
			if (dx + dy > mSquareWidth * 0.01f) {
				float oldX = mInProgressX;
				float oldY = mInProgressY;

				mInProgressX = x;
				mInProgressY = y;

				if (mPatternInProgress && patternSize > 0) {
					final ArrayList<Cell> pattern = mPattern;
					final float radius = mSquareWidth * mDiameterFactor * 0.5f;

					final Cell lastCell = pattern.get(patternSize - 1);

					float startX = getCenterXForColumn(lastCell.column);
					float startY = getCenterYForRow(lastCell.row);

					float left;
					float top;
					float right;
					float bottom;

					final Rect invalidateRect = mInvalidate;

					if (startX < x) {
						left = startX;
						right = x;
					} else {
						left = x;
						right = startX;
					}

					if (startY < y) {
						top = startY;
						bottom = y;
					} else {
						top = y;
						bottom = startY;
					}

					// Invalidate between the pattern's last cell and the
					// current location
					invalidateRect.set((int) (left - radius), (int) (top - radius), (int) (right + radius), (int) (bottom + radius));

					if (startX < oldX) {
						left = startX;
						right = oldX;
					} else {
						left = oldX;
						right = startX;
					}

					if (startY < oldY) {
						top = startY;
						bottom = oldY;
					} else {
						top = oldY;
						bottom = startY;
					}

					// Invalidate between the pattern's last cell and the
					// previous location
					invalidateRect.union((int) (left - radius), (int) (top - radius), (int) (right + radius), (int) (bottom + radius));

					// Invalidate between the pattern's new cell and the
					// pattern's previous cell
					if (hitCell != null) {
						startX = getCenterXForColumn(hitCell.column);
						startY = getCenterYForRow(hitCell.row);
						if (patternSize >= 2) {
							// (re-using hitcell for old cell)
							hitCell = pattern.get(patternSize - 1 - (patternSize - patternSizePreHitDetect));
							oldX = getCenterXForColumn(hitCell.column);
							oldY = getCenterYForRow(hitCell.row);

							if (startX < oldX) {
								left = startX;
								right = oldX;
							} else {
								left = oldX;
								right = startX;
							}

							if (startY < oldY) {
								top = startY;
								bottom = oldY;
							} else {
								top = oldY;
								bottom = startY;
							}
						} else {
							left = right = startX;
							top = bottom = startY;
						}

						final float widthOffset = mSquareWidth / 2f;
						final float heightOffset = mSquareHeight / 2f;

						invalidateRect.set((int) (left - widthOffset), (int) (top - heightOffset), (int) (right + widthOffset), (int) (bottom + heightOffset));
					}

					invalidate(invalidateRect);
				} else {
					invalidate();
				}
			}
		}
	}

	private void handleActionUp(MotionEvent event) {
		// report pattern detected
		if (!mPattern.isEmpty()) {
			mPatternInProgress = false;
			notifyPatternDetected();
			invalidate();
		}
		if (PROFILE_DRAWING) {
			if (mDrawingProfilingStarted) {
				Debug.stopMethodTracing();
				mDrawingProfilingStarted = false;
			}
		}
	}

	private void handleActionDown(MotionEvent event) {
		resetPattern();
		final float x = event.getX();
		final float y = event.getY();
		final Cell hitCell = detectAndAddHit(x, y);
		if (hitCell != null) {
			mPatternInProgress = true;
			mPatternDisplayMode = DisplayMode.Correct;
			notifyPatternStarted();
		} else {
			mPatternInProgress = false;
			notifyPatternCleared();
		}
		if (hitCell != null) {
			final float startX = getCenterXForColumn(hitCell.column);
			final float startY = getCenterYForRow(hitCell.row);

			final float widthOffset = mSquareWidth / 2f;
			final float heightOffset = mSquareHeight / 2f;

			invalidate((int) (startX - widthOffset), (int) (startY - heightOffset), (int) (startX + widthOffset), (int) (startY + heightOffset));
		}
		mInProgressX = x;
		mInProgressY = y;
		if (PROFILE_DRAWING) {
			if (!mDrawingProfilingStarted) {
				Debug.startMethodTracing("LockPatternDrawing");
				mDrawingProfilingStarted = true;
			}
		}
	}

	private float getCenterXForColumn(int column) {
		return getPaddingX() + column * mSquareWidth + mSquareWidth / 2f;
	}

	private float getCenterYForRow(int row) {
		return getPaddingY() + row * mSquareHeight + mSquareHeight / 2f;
	}

	public int getPaddingY() {
		int offsizeY = mDrawableHeight / 2 + framePadding;
		return offsizeY;
	}

	public int getPaddingX() {
		int offsizeX = mDrawableWidth / 2 + framePadding;
		return offsizeX;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		adjustLayout();
		final ArrayList<Cell> pattern = mPattern;
		final int count = pattern.size();
		final boolean[][] drawLookup = mPatternDrawLookup;

		final float squareWidth = mSquareWidth;
		final float squareHeight = mSquareHeight;

		float radius = (squareWidth * mDiameterFactor * 0.5f);
		mPathPaint.setStrokeWidth(radius);

		final Path currentPath = mCurrentPath;
		currentPath.rewind();

		final boolean drawPath = (!mInStealthMode || mPatternDisplayMode == DisplayMode.Wrong);

		boolean oldFlag = (mPaint.getFlags() & Paint.FILTER_BITMAP_FLAG) != 0;
		mPaint.setFilterBitmap(true); // draw with higher quality since we

		for (int i = 0; i < 4; i++) {
			float topY = getPaddingY() + i * squareHeight;
			for (int j = 0; j < 3; j++) {
				float leftX = getPaddingX() + j * squareWidth;
				drawCircle(canvas, (int) leftX, (int) topY, drawLookup[i][j], i * 3 + j);
			}
		}

		// render with transforms
		// draw the lines
		if (drawPath && mTwelvePatternLockerInfo.isDrawLine()) {
			boolean anyCircles = false;
			for (int i = 0; i < count; i++) {
				Cell cell = pattern.get(i);

				// only draw the part of the pattern stored in
				// the lookup table (this is only different in the case
				// of animation).
				if (!drawLookup[cell.row][cell.column]) {
					break;
				}
				anyCircles = true;

				float centerX = getCenterXForColumn(cell.column);
				float centerY = getCenterYForRow(cell.row);
				if (i == 0) {
					currentPath.moveTo(centerX, centerY);
				} else {
					currentPath.lineTo(centerX, centerY);
				}
			}

			// add last in progress section
			if ((mPatternInProgress || mPatternDisplayMode == DisplayMode.Animate) && anyCircles) {
				currentPath.lineTo(mInProgressX, mInProgressY);
			}
			// chang the line color in different DisplayMode
			if (mPatternDisplayMode == DisplayMode.Wrong)
				mPathPaint.setColor(Color.RED);
			else
				mPathPaint.setColor(mTwelvePatternLockerInfo.getLineColor());
			canvas.drawPath(currentPath, mPathPaint);
		}

		mPaint.setFilterBitmap(oldFlag); // restore default flag

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

		if (selectCell != null) {

			float centerX = getCenterXForColumn(selectCell.column);
			float centerY = getCenterYForRow(selectCell.row);

//			int offsetX = (int) (mSquareWidth / 2);
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

	/**
	 * @param canvas
	 * @param leftX
	 * @param topY
	 * @param partOfPattern
	 *            Whether this circle is part of the pattern.
	 */
	private void drawCircle(Canvas canvas, int leftX, int topY, boolean partOfPattern, int position) {
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

	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private Handler mHandler = new Handler();
	private TextView gesturepwd_unlock_textview;

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			clearPattern();
		}
	};

	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			clearPattern();
			setEnabled(false);
			new CountDownTimer(LockPatternUtils_12.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {

					gesturepwd_unlock_textview.setVisibility(View.VISIBLE);

					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						gesturepwd_unlock_textview.setText(secondsRemaining + " 秒后重试");
					} else {
						gesturepwd_unlock_textview.setText("请绘制手势密码");
					}

				}

				@Override
				public void onFinish() {
					setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

	/**
	 * 设置是否显示绘制操作按钮
	 * 
	 * @param isVisiable
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		selectCell = null;
		selectPosition = -1;
		invalidate();
	}

	public void setImage(boolean b) {
		setImage = b;
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

//		if (controllerView != null && controllerView.getParent() == null) {
//			LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_LOCKER_TWELVE);
//			mController_container_layout.addView(controllerView);
//		}
		Intent intent = new Intent(mContext, SelectImageActivity.class);
		intent.putExtra("from", ChooseStickerUtils.FROM_LOCKER_TWELVE);
		((Activity)mContext).startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
	}

	public void setTwelvePatternLockerInfo(TwelvePatternLockerInfo twelvePatternLockerInfo) {
		this.mTwelvePatternLockerInfo = twelvePatternLockerInfo;
		mCenterPoint.x = twelvePatternLockerInfo.getX() + twelvePatternLockerInfo.getWidth() / 2;
		mCenterPoint.y = twelvePatternLockerInfo.getY() + twelvePatternLockerInfo.getHeight() / 2;
		mViewWidth = twelvePatternLockerInfo.getWidth();
		mViewHeight = twelvePatternLockerInfo.getHeight();
		mSquareWidth = mViewWidth / 3;
		mSquareHeight = mViewHeight / 4;
		init();
//		if (isVisiable) {
//			showControllerView();
//		}
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

	public void updateImage() {
		for (int i = 0; i < bitmaps.length; i++) {
			Bitmap bitmap = mTwelvePatternLockerInfo.getBitmaps()[i];
			if (bitmap != null) {
				bitmaps[i] = DrawableUtils.scaleTo(bitmap, defaultImageSize, defaultImageSize);
			} else {
				bitmaps[i] = emptyBitmap;
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

	public void updateTempImage() {
		for (int i = 0; i < bitmaps.length; i++) {
			Bitmap bitmap = mTwelvePatternLockerInfo.getBitmaps()[i];
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

	@Override
	public void selectImage(String imageUrl) {
		if (selectPosition != -1) {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
			mTwelvePatternLockerInfo.getBitmaps()[selectPosition] = bitmap;
			updateTempImage();
		}
	}

	public void selectImage(Bitmap bitmap) {
		if (selectPosition != -1) {
			mTwelvePatternLockerInfo.getBitmaps()[selectPosition] = bitmap;
			updateTempImage();
		}

	}

	public void setHeadTextView(TextView gesturepwd_unlock_textview2) {
		this.gesturepwd_unlock_textview = gesturepwd_unlock_textview2;
	}

	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}

}
