package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.model.WordPasswordLockerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.WordPasswordLockUtils;
import com.lockstudio.sticklocker.util.WordPasswordLockUtils.OnLockSettingChange;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.opda.android.activity.R;

public class WordPasswordLockView extends LinearLayout implements OnLockSettingChange {
	public static float MAX_SCALE = 2.0f;
	public static final float MIN_SCALE = 0.6f;
	public static float SHAPE_MAX_SCALE = 2.0f;
	public static float SHAPE_MIN_SCALE = 0.5f;
	private int shape_width;
	private Animation mShakeAnim;
	private Vibrator mVibrator;
	private boolean setPassword;
	private boolean mInputEnabled = true;
	private String password = "";
	private String confirmPassword = "";
	private WordPasswordLockerInfo mPasswordLockerInfo;
	private OnLockSettingListener onLockSettingListener;
	private UnlockListener mUnlockListener;
	private float mTextScale = 1.0f;
	private float mShapeScale = 1.0f;
	private boolean showEdit;
	private boolean showEditOrFinish;
//	private boolean isFinish;

	public static enum State {
		First, Confirm, ConfirmWrong, Done
	}

	private State mState = State.First;

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
	private Point mEditPoint = new Point();
	private Drawable mDeleteDrawable, mEditDrawable, mFinishDrawable;
	private int mDrawableWidth, mDrawableHeight;

	private int framePadding = 8;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);
	private Path mFramePath = new Path();
	private Paint mFramePaint;

	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_EDIT = 3;
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
	private RelativeLayout.LayoutParams mContainerLayoutParams;
	private LinearLayout mController_container_layout;
	private View controllerView;
	private PasswordIndView mPasswordIndView;
	private Context mContext;
	private LinearLayout textview_layout, edittext_layout;
	private TextView mCleanPass;
	private TextView[] textViews = new TextView[12];
	private EditText[] editTexts = new EditText[12];
	private ImageView[] imageViews = new ImageView[12];
	private String[] words = new String[12];
	private Typeface typeface;
	private float lastX = 0;
	private float lastY = 0;
	private boolean init;

	public WordPasswordLockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		setWillNotDraw(false);
	}

	public WordPasswordLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		setWillNotDraw(false);
	}

	public WordPasswordLockView(Context context) {
		super(context);
		this.mContext = context;
		setWillNotDraw(false);
	}

	public void init(Context mContext) {

		mShakeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_x);
		mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}

		if (mEditDrawable == null) {
			mEditDrawable = getContext().getResources().getDrawable(R.drawable.diy_edit);
		}
		if (mFinishDrawable == null) {
			mFinishDrawable = getContext().getResources().getDrawable(R.drawable.diy_finish);
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

		textview_layout = (LinearLayout) findViewById(R.id.textview_layout);
		edittext_layout = (LinearLayout) findViewById(R.id.edittext_layout);
		for (int i = 0; i < 12; i++) {
			int textviewId = R.id.word_textview_1;
			int edittextId = R.id.word_edittext_1;
			int imageviewId = R.id.word_imageview_1;
			textViews[i] = (TextView) findViewById(textviewId + i * 2);
			textViews[i].setText(words[i]);
			editTexts[i] = (EditText) findViewById(edittextId + i);
			editTexts[i].setText(words[i]);
			imageViews[i] = (ImageView) findViewById(imageviewId + i * 2);
			if (!isEditable) {
				textViews[i].setTag(i);
				textViews[i].setOnTouchListener(VIEW_TOUCH_DARK);
			}
		}
		MAX_SCALE = 75.0f / mPasswordLockerInfo.getTextSize() * 0.7f;
		SHAPE_MAX_SCALE = 1.5f;

		shape_width = DensityUtil.dip2px(mContext, 50);
		mPasswordLockerInfo.setShapeId(getShapeId(mPasswordLockerInfo.getShapeName()));

		if (isEditable) {
			WordPasswordLockUtils wordPasswordLockUtils = new WordPasswordLockUtils(mContext);
			wordPasswordLockUtils.setOnLockSettingChange(this);
			wordPasswordLockUtils.initSelectData(mPasswordLockerInfo.getFont(), mPasswordLockerInfo.getTextColor(), mPasswordLockerInfo.getShadowColor());
			wordPasswordLockUtils.setAlpha(mPasswordLockerInfo.getAlpha());
			wordPasswordLockUtils.setScale(MAX_SCALE, mTextScale);
			wordPasswordLockUtils.setShape(mPasswordLockerInfo.getShapeName(), mPasswordLockerInfo.getShapeId());
			wordPasswordLockUtils.setShapeScale(SHAPE_MAX_SCALE, mShapeScale);
			controllerView = wordPasswordLockUtils.getView();
		}
		init = true;
		updateTextView();
		updateImageView();

		transformDraw();
	}

	private int getShapeId(String shapeName) {
		if ("null".equals(shapeName)) {
			return R.drawable.translucent_bitmap;
		}
		if ("xin".equals(shapeName)) {
			return R.drawable.word_shape_xin;
		}
		if ("mao".equals(shapeName)) {
			return R.drawable.word_shape_mao;
		}
		if ("xuehua".equals(shapeName)) {
			return R.drawable.word_shape_xuehua;
		}
		if ("xuehua2".equals(shapeName)) {
			return R.drawable.word_shape_xuehua2;
		}
		if ("huabian".equals(shapeName)) {
			return R.drawable.word_shape_huabian;
		}
		if ("huabian2".equals(shapeName)) {
			return R.drawable.word_shape_huabian2;
		}
		if ("huabian3".equals(shapeName)) {
			return R.drawable.word_shape_huabian3;
		}
		if ("huabian4".equals(shapeName)) {
			return R.drawable.word_shape_huabian4;
		}
		if ("huabian5".equals(shapeName)) {
			return R.drawable.word_shape_huabian5;
		}
		if ("liujiaoxing".equals(shapeName)) {
			return R.drawable.word_shape_liujiaoxing;
		}
		if ("dunpai".equals(shapeName)) {
			return R.drawable.word_shape_dunpai;
		}
		if ("lingjie".equals(shapeName)) {
			return R.drawable.word_shape_lingjie;
		}
		if ("shuidi".equals(shapeName)) {
			return R.drawable.word_shape_shuidi;
		}
		if ("wubianxing".equals(shapeName)) {
			return R.drawable.word_shape_wubianxing;
		}
		if ("wujiaoxing".equals(shapeName)) {
			return R.drawable.word_shape_wujiaoxing;
		}
		if ("wujiaoxing2".equals(shapeName)) {
			return R.drawable.word_shape_wujiaoxing2;
		}
		if ("yazi".equals(shapeName)) {
			return R.drawable.word_shape_yazi;
		}
		if ("yuan".equals(shapeName)) {
			return R.drawable.word_shape_yuan;
		}
		return 0;
	}
	
	private int getPressedShapeId(String shapeName) {
		if ("null".equals(shapeName)) {
			return R.drawable.translucent_bitmap;
		}
		if ("xin".equals(shapeName)) {
			return R.drawable.word_shape_xin_qian;
		}
		if ("mao".equals(shapeName)) {
			return R.drawable.word_shape_mao_qian;
		}
		if ("xuehua".equals(shapeName)) {
			return R.drawable.word_shape_xuehua_qian;
		}
		if ("xuehua2".equals(shapeName)) {
			return R.drawable.word_shape_xuehua2_qian;
		}
		if ("huabian".equals(shapeName)) {
			return R.drawable.word_shape_huabian_qian;
		}
		if ("huabian2".equals(shapeName)) {
			return R.drawable.word_shape_huabian2_qian;
		}
		if ("huabian3".equals(shapeName)) {
			return R.drawable.word_shape_huabian3_qian;
		}
		if ("huabian4".equals(shapeName)) {
			return R.drawable.word_shape_huabian4_qian;
		}
		if ("huabian5".equals(shapeName)) {
			return R.drawable.word_shape_huabian5_qian;
		}
		if ("liujiaoxing".equals(shapeName)) {
			return R.drawable.word_shape_liujiaoxing_qian;
		}
		if ("dunpai".equals(shapeName)) {
			return R.drawable.word_shape_dunpai_qian;
		}
		if ("lingjie".equals(shapeName)) {
			return R.drawable.word_shape_lingjie_qian;
		}
		if ("shuidi".equals(shapeName)) {
			return R.drawable.word_shape_shuidi_qian;
		}
		if ("wubianxing".equals(shapeName)) {
			return R.drawable.word_shape_wubianxing_qian;
		}
		if ("wujiaoxing".equals(shapeName)) {
			return R.drawable.word_shape_wujiaoxing_qian;
		}
		if ("wujiaoxing2".equals(shapeName)) {
			return R.drawable.word_shape_wujiaoxing2_qian;
		}
		if ("yazi".equals(shapeName)) {
			return R.drawable.word_shape_yazi_qian;
		}
		if ("yuan".equals(shapeName)) {
			return R.drawable.word_shape_yuan_qian;
		}
		return 0;
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

			if(textview_layout.getVisibility()==View.VISIBLE && !controllerView.isShown()){
				mEditDrawable.setBounds(mEditPoint.x - mDrawableWidth / 2, mEditPoint.y - mDrawableHeight / 2, mEditPoint.x + mDrawableWidth / 2, mEditPoint.y
						+ mDrawableHeight / 2);
				mEditDrawable.draw(canvas);
			}else{
				mFinishDrawable.setBounds(mEditPoint.x - mDrawableWidth / 2, mEditPoint.y - mDrawableHeight / 2, mEditPoint.x + mDrawableWidth / 2, mEditPoint.y
						+ mDrawableHeight / 2);
				mFinishDrawable.draw(canvas);
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
		mEditPoint = LocationToPoint(RIGHT_TOP);
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
	 * The call back interface for detecting patterns entered by the user.
	 */
	public interface OnLockSettingListener {
		void updateState(State state, String password);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mInputEnabled && isEditable) {
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

				if (isVisiable && mStatus == STATUS_EDIT && mStatus == JudgeStatus(event.getX(), event.getY())) {
					if (edittext_layout.getVisibility() == View.GONE) {
						if(controllerView.isShown()){
							mController_container_layout.removeAllViews();
							setVisible(false);
						}else{
							mController_container_layout.removeAllViews();
							edittext_layout.setVisibility(View.VISIBLE);
							textview_layout.setVisibility(View.GONE);
							mStatus = STATUS_INIT;
							showEdit=false;
							showEditOrFinish=true;
						}
					}else{
					    showControllerView();
					    edittext_layout.setVisibility(View.GONE);
						textview_layout.setVisibility(View.VISIBLE);
						InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
						for (int i = 0; i < 12; i++) {
							words[i] = editTexts[i].getText().toString();
							textViews[i].setText(words[i]);
							imm.hideSoftInputFromWindow(editTexts[i].getWindowToken(), 0);
						}
						//mPasswordLockerInfo.setWords(words);
					    invalidate();
						showEdit=true;
						showEditOrFinish=false;
					}
					return true;
				}
				mStatus = STATUS_INIT;

				if (isEditable && isVisiable) {
					float newX = event.getRawX();
					float newY = event.getRawY();
					if (Math.abs(newX - lastX) <= 10 && Math.abs(newY - lastY) <= 10) {
						//showControllerView();
						mController_container_layout.removeAllViews();
						edittext_layout.setVisibility(View.VISIBLE);
						textview_layout.setVisibility(View.GONE);
						showEdit=false;
						//mStatus = STATUS_INIT;
					}
				}

				if (isEditable && !isVisiable) {
					mOnFocuseChangeListener.focuseChange(this);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setPassword(boolean b) {
		setPassword = b;
	}

	public void setInputEnabel(boolean mInputEnabled) {
		this.mInputEnabled = mInputEnabled;
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
	
	public void setTextView(TextView cleanPass) {
		this.mCleanPass = cleanPass;
		this.mCleanPass.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					cleanPassword();
				}
			});
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
		invalidate();
	}
	public boolean getVisible(){
		return isVisiable;
	}
	
	public boolean getShowEdit(){
		return showEdit;
	}
	/**
	 * 设置是否显示绘制操作按钮
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		if (!isVisiable && init) {
			edittext_layout.setVisibility(View.GONE);
			textview_layout.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			for (int i = 0; i < 12; i++) {
				words[i] = editTexts[i].getText().toString();
				textViews[i].setText(words[i]);
				imm.hideSoftInputFromWindow(editTexts[i].getWindowToken(), 0);
			}
			mPasswordLockerInfo.setWords(words);
		}

		invalidate();
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF exitPointF = new PointF(mEditPoint);

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
	 * 两个点之间的距离
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	public void showControllerView() {
		showEdit=true;
		if (controllerView != null && controllerView.getParent() == null) {
			mController_container_layout.removeAllViews();
			mController_container_layout.addView(controllerView);
		}
		if(edittext_layout.getVisibility()==View.VISIBLE){
			edittext_layout.setVisibility(View.GONE);
			textview_layout.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			for (int i = 0; i < 12; i++) {
				words[i] = editTexts[i].getText().toString();
				textViews[i].setText(words[i]);
				imm.hideSoftInputFromWindow(editTexts[i].getWindowToken(), 0);
			}
		}
	}

	public void setPasswordLockerInfo(WordPasswordLockerInfo passwordLockerInfo) {
		this.mPasswordLockerInfo = passwordLockerInfo;
		mCenterPoint.x = mPasswordLockerInfo.getX() + mPasswordLockerInfo.getWidth() / 2;
		mCenterPoint.y = mPasswordLockerInfo.getY() + mPasswordLockerInfo.getHeight() / 2;
		mViewWidth = mPasswordLockerInfo.getWidth();
		mViewHeight = mPasswordLockerInfo.getHeight();
		words = mPasswordLockerInfo.getWords();
		mTextScale = mPasswordLockerInfo.getTextScale();
		mShapeScale = mPasswordLockerInfo.getShapeScale();
		if (!TextUtils.isEmpty(mPasswordLockerInfo.getFont()) && new File(mPasswordLockerInfo.getFont()).exists()) {
			typeface = Typeface.createFromFile(mPasswordLockerInfo.getFont());
		}
		init(getContext());
		if (isVisiable) {
			//showControllerView();
			edittext_layout.setVisibility(View.VISIBLE);
			textview_layout.setVisibility(View.GONE);
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

	public void setContainerLayoutParams(RelativeLayout.LayoutParams containerLayoutParams) {
		this.mContainerLayoutParams = containerLayoutParams;
	}

	public void setControllerContainerLayout(LinearLayout controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	public int getTopMargin() {
		return mViewPaddingTop;
	}

	@Override
	public void change(String fontPath, String fontUrl, float scale, int color, int shadowColor, int alpha) {
		if (color == shadowColor) {
			shadowColor = 0x00000000;
		}
		if (TextUtils.isEmpty(fontPath)) {
			typeface = null;
		} else {
			if (!fontPath.equals(mPasswordLockerInfo.getFont())) {
				typeface = Typeface.createFromFile(fontPath);
			}
		}
		this.mTextScale = scale;
		mPasswordLockerInfo.setTextScale(scale);
		mPasswordLockerInfo.setAlpha(alpha);
		mPasswordLockerInfo.setTextColor(color);
		mPasswordLockerInfo.setFont(fontPath);
		mPasswordLockerInfo.setShadowColor(shadowColor);
		if (init) {
			updateTextView();
		}
	}

	@Override
	public void changeShape(String shapeName, int shapeId, float shapeScale) {
		mPasswordLockerInfo.setShapeName(shapeName);
		mPasswordLockerInfo.setShapeId(shapeId);
		mPasswordLockerInfo.setShapeScale(shapeScale);
		this.mShapeScale = shapeScale;
		updateImageView();
	}

	private void updateTextView() {
		float textSize = mPasswordLockerInfo.getTextSize();
		int alpha = mPasswordLockerInfo.getAlpha();
		int textColor = mPasswordLockerInfo.getTextColor();
		int editColor = mPasswordLockerInfo.getTextColor();
		int shadowColor = mPasswordLockerInfo.getShadowColor();
		textColor = Color.argb(alpha, Color.red(textColor), Color.green(textColor), Color.blue(textColor));
		for (int i = 0; i < 12; i++) {
			TextView textView = textViews[i];
			EditText editText = editTexts[i];
			if (typeface != null) {
				textView.setTypeface(typeface);
				editText.setTypeface(typeface);
			} else {
				textView.setTypeface(Typeface.DEFAULT);
				editText.setTypeface(Typeface.DEFAULT);
			}
			textView.setShadowLayer(5, 0, 0, shadowColor);
			textView.setTextSize(textSize * mTextScale);
			textView.setTextColor(textColor);
			editText.setTextSize(textSize * mTextScale);
			editText.setTextColor(editColor);
		}
	}

	private Bitmap bitmap_shape,bitmap_shape_pressed;
	private void updateImageView() {
		bitmap_shape = DrawableUtils.getBitmap(mContext, mPasswordLockerInfo.getShapeId());
		bitmap_shape_pressed = DrawableUtils.getBitmap(mContext, getPressedShapeId(mPasswordLockerInfo.getShapeName()));
		if (!"null".equals(mPasswordLockerInfo.getShapeName())) {
			bitmap_shape = DrawableUtils.scaleTo(bitmap_shape, (int) (shape_width * mShapeScale), (int) (shape_width * mShapeScale));
			bitmap_shape_pressed = DrawableUtils.scaleTo(bitmap_shape_pressed, (int) (shape_width * mShapeScale), (int) (shape_width * mShapeScale));
		}
		for (int i = 0; i < 12; i++) {
			imageViews[i].setImageBitmap(bitmap_shape);
		}
	}
	
	public void cleanPassword(){
		if (mState == State.First) {
			if(password.length()>0){
				password=password.substring(0, password.length()-1);
				mPasswordIndView.setInputPassLength(password.length());
			}
		} else if (mState == State.Confirm || mState == State.ConfirmWrong) {
			if(confirmPassword.length()>0){
				confirmPassword=confirmPassword.substring(0, confirmPassword.length()-1);
				mPasswordIndView.setInputPassLength(confirmPassword.length());
			}
		}
	}

	public void onClick(int i) {
		String position = i+"";
		if (i == 10) {
			position = "a";
		} else if (i == 11) {
			position = "b";
		}
		if (setPassword) {
			if (mState == State.First) {
				password += position;
				mPasswordIndView.setInputPassLength(password.length());
				if (password.length() == 4) {
					mState = State.Confirm;
					mPasswordIndView.setInputPassLength(confirmPassword.length());
					setInputEnabel(false);
					handler.sendEmptyMessageDelayed(2, 100);
				}
			} else if (mState == State.Confirm || mState == State.ConfirmWrong) {
				confirmPassword += position;
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

		} else {
			password += position;
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
	
	/**
	 * 让控件点击时，颜色变深
	 * */
	OnTouchListener VIEW_TOUCH_DARK = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			TextView textView = (TextView) v;
			int position = (Integer) textView.getTag();
			ImageView imageView = imageViews[position]; 
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView.setImageBitmap(bitmap_shape_pressed);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView.setImageBitmap(bitmap_shape);
				onClick(position);
			}
			return true;
		}
	};

}
