package com.lockstudio.sticklocker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.Interface.OnFocuseChangeListener;
import com.lockstudio.sticklocker.Interface.OnRemoveLockerViewListener;
import com.lockstudio.sticklocker.Interface.OnUpdateViewListener;
import com.lockstudio.sticklocker.activity.SelectImageActivity;
import com.lockstudio.sticklocker.model.SlideLockerInfo;
import com.lockstudio.sticklocker.util.ChooseSlideUitls;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.ChooseStickerUtils.OnImageSelectorListener;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.MConstants;

import cn.opda.android.activity.R;

@SuppressWarnings("deprecation")
public class SlideContainerView extends AbsoluteLayout implements OnImageSelectorListener {

	private Context mContext;
	private DragViews d1 = null;
	private DragViews2 d2 = null;
	private Paint mPaint;
	private Canvas mCanvas;
	private int theLargen;
	private SharedPreferences sp;
	private Bitmap mBitmapCircleDefault;
	private Bitmap mBitmapCircleGreen;
	private Bitmap mBitmapCircleIcon;
	private int mPreviousx = 0;
	private int mPreviousy = 0;
	private int screenWidth;
	private int screenHeight;
	private int iCurrentx;
	private int iCurrenty;
	private int left;
	private int top;
	private int right;
	private int bottom;

	private Bitmap[] bitmaps = new Bitmap[2];
	private Path mFramePath = new Path();
	private Path mSelectPath = new Path();
	private Point mLTPoint;
	private Point mRTPoint;
	private Point mRBPoint;
	private Point mLBPoint;
	private int frameColor = Color.GRAY;
	private int frameWidth = DensityUtil.dip2px(getContext(), 1.5f);
	private Paint mFramePaint;
	private Paint mSelectPaint;
	private Drawable mDeleteDrawable/* , mControllerDrawable */;
	private Point mDeletePoint = new Point();
	private Point mControllerPoint = new Point();
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	private int mDrawableWidth, mDrawableHeight;
	private android.widget.RelativeLayout.LayoutParams mContainerLayoutParams;
	private SlideLockerInfo mSlideLockerInfo;
	private OnUpdateViewListener mOnUpdateViewListener;
	public static final int STATUS_INIT = 0;
	public static final int STATUS_DRAG = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_CONTROLLER = 3;
	private int mStatus = STATUS_INIT;
	private int leftOrRight;
	private boolean isEditable = true;
	private boolean isVisiable = true;
	private boolean setImage;
	private OnRemoveLockerViewListener mOnRemoveViewListener;
	private OnFocuseChangeListener mOnFocuseChangeListener;
	private View controllerView,chooseSlideView;
	private LinearLayout mController_container_layout;
	private boolean first;
	private int bitmapIconRes[]={0,R.drawable.icon_slide11,R.drawable.icon_slide31,
			R.drawable.icon_slide41,R.drawable.icon_slide51};

	private int lLeft;
	private int lTop;
	private int lRight;
	private int lBottom;
	private int rLeft;
	private int rTop;
	private int rRight;
	private int rBottom;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 10:
				lLeft = msg.arg1;
				lTop = msg.arg2;
				lRight = lLeft + theLargen;
				lBottom = lTop + theLargen;
				d1.setLayoutParams(new LayoutParams(theLargen, theLargen, lLeft, lTop));
				SlideContainerView.this.invalidate();
				break;
			case 20:
				rLeft = msg.arg1;
				rTop = msg.arg2;
				rRight = rLeft + theLargen;
				rBottom = rTop + theLargen;
				d2.setLayoutParams(new LayoutParams(theLargen, theLargen, rLeft, rTop));
				SlideContainerView.this.invalidate();
				break;
			case 1:
				d1.setLayoutParams(new LayoutParams(theLargen, theLargen, d1.getZhuIconCenter().get(0), d1.getZhuIconCenter().get(1)));
				d2.setLayoutParams(new LayoutParams(theLargen, theLargen, d2.getZhuIconCenter().get(0), d2.getZhuIconCenter().get(1)));
				break;
			case 11:
				leftOrRight = 1;
				isVisiable = true;
				showControllerView();
				invalidate();
				break;
			case 22:
				leftOrRight = 2;
				isVisiable = true;
				showControllerView();
				invalidate();
				break;

			default:
				break;
			}
			return false;
		}
	});

	public SlideContainerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SlideContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public SlideContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	private void init() {
		if (setImage) {
//			ChooseStickerUtils chooseStickerUtils = new ChooseStickerUtils(mContext, ChooseStickerUtils.FROM_LOCKER_SLIDE);
//			chooseStickerUtils.setOnImageSelectorListener(this);
//			controllerView = chooseStickerUtils.getView();
			
			ChooseSlideUitls  mChooseSlideUitls=new ChooseSlideUitls(mContext,this);
			chooseSlideView=mChooseSlideUitls.getView();
		}

		setWillNotDraw(false);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		theLargen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		d1 = new DragViews(mContext, mHandler);
		d1.setSlideLockerInfo(mSlideLockerInfo);
		d2 = new DragViews2(mContext, mHandler);
		d2.setSlideLockerInfo(mSlideLockerInfo);
		updateImage();
		first = mSlideLockerInfo.isFirst();
		// first = sp.getBoolean("first", true);
		if (first) {
			mSlideLockerInfo.setFirst(false);
			// sp.edit().putBoolean("first", false).commit();
			addView(d1, new LayoutParams(theLargen, theLargen, d1.getInitPoint1().get(0), d1.getInitPoint1().get(1)));
			addView(d2, new LayoutParams(theLargen, theLargen, d2.getInitPoint2().get(0), d2.getInitPoint2().get(1)));
		} else {
			addView(d1, new LayoutParams(theLargen, theLargen, d1.getZhuIconCenter().get(0), d1.getZhuIconCenter().get(1)));
			addView(d2, new LayoutParams(theLargen, theLargen, d2.getZhuIconCenter().get(0), d2.getZhuIconCenter().get(1)));
		}

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 2));
		mPaint.setColor(Color.WHITE);

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

		mLTPoint = new Point();
		mRTPoint = new Point();
		mRBPoint = new Point();
		mLBPoint = new Point();
		mDeletePoint = LocationToPoint(LEFT_TOP);
		mControllerPoint = LocationToPoint(LEFT_BOTTOM);
		if (mDeleteDrawable == null) {
			mDeleteDrawable = getContext().getResources().getDrawable(R.drawable.diy_delete);
		}
		// if (mControllerDrawable == null) {
		// mControllerDrawable =
		// getContext().getResources().getDrawable(R.drawable.diy_rotate);
		// }
		
		int p = mSlideLockerInfo.getBitmapRes();
		if(p>=bitmapIconRes.length){
			p = 1;
		}
		mBitmapCircleIcon=BitmapFactory.decodeResource(mContext.getResources(),bitmapIconRes[p]);
		
		mDrawableWidth = mDeleteDrawable.getIntrinsicWidth();
		mDrawableHeight = mDeleteDrawable.getIntrinsicHeight();

	}

	public void updateImage() {
		mBitmapCircleDefault = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
		mBitmapCircleGreen = BitmapFactory.decodeResource(getResources(), R.drawable.boy);

		Bitmap bitmap = mSlideLockerInfo.getBitmaps()[0];
		Bitmap bitmap2 = mSlideLockerInfo.getBitmaps()[1];
		if (bitmap != null) {
			bitmaps[0] = bitmap;
		} else {
			bitmaps[0] = mBitmapCircleDefault;
		}
		if (bitmap2 != null) {
			bitmaps[1] = bitmap2;
		} else {
			bitmaps[1] = mBitmapCircleGreen;
		}
		d1.setBackgroundDrawable(new BitmapDrawable(bitmaps[0]));
		d2.setBackgroundDrawable(new BitmapDrawable(bitmaps[1]));

	}
	
	private void showChooseSlideUitls(){
		if (chooseSlideView != null && chooseSlideView.getParent() == null) {
		    mController_container_layout.removeAllViews();
		    mController_container_layout.addView(chooseSlideView);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mCanvas = canvas;
		if (lLeft == 0) {
			lLeft = d1.getLeft();
			lTop = d1.getTop();
			lRight = d1.getRight();
			lBottom = d1.getBottom();
			rLeft = d2.getLeft();
			rTop = d2.getTop();
			rRight = d2.getRight();
			rBottom = d2.getBottom();
		}
		int x1 = (int) (lLeft + theLargen / 2);
		int y1 = (int) (lTop + theLargen / 2);
		int x2 = (int) (rLeft + theLargen / 2);
		int y2 = (int) (rTop + theLargen / 2);
		int iconWidth=0;
		int iconHeight=0;
		int firstWidth=0;
		int firstHeight=0;
		if(mBitmapCircleIcon==null){
			drawAL(x1, y1, (Math.abs(x2) + Math.abs(x1)) / 2, (Math.abs(y2) + Math.abs(y1)) / 2);
			canvas.drawLine((Math.abs(x2) + Math.abs(x1)) / 2, (Math.abs(y2) + Math.abs(y1)) / 2, x2, y2, mPaint);
		}else{
			int left1 = (int) (lLeft + theLargen / 2);
			int top1 = (int) (lTop + theLargen / 2);
			int left2 = (int) (rLeft + theLargen / 2);
			int top2 = (int) (rTop + theLargen / 2);
//			int deleteNum=Math.abs(lLeft-lRight)/2/(mBitmapCircleIcon.getWidth()-DensityUtil.dip2px(mContext, 2));

			if(left1<=left2 && top1<top2){
				if(Math.abs(left1-left2)>Math.abs(top1-top2)){
					iconWidth=Math.abs(left1-left2)/(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(top1-top2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth-i*(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2)), firstHeight-i*iconHeight, mPaint);
					}
				}else{
					iconWidth=Math.abs(top1-top2)/(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(left1-left2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth-i*iconHeight, firstHeight-i*(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2)), mPaint);
					}
				}
			}else if(left1<=left2 && top1>=top2){
				if(Math.abs(left1-left2)>Math.abs(top1-top2)){
					iconWidth=Math.abs(left1-left2)/(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(top1-top2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth-i*(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2)), firstHeight+i*iconHeight, mPaint);
					}
				}else{
					iconWidth=Math.abs(top1-top2)/(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(left1-left2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth-i*iconHeight, firstHeight+i*(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2)), mPaint);
					}
				}
			}else if(left1>left2 && top1<top2){
				if(Math.abs(left1-left2)>Math.abs(top1-top2)){
					iconWidth=Math.abs(left1-left2)/(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(top1-top2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth+i*(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2)), firstHeight-i*iconHeight, mPaint);
					}
				}else{
					iconWidth=Math.abs(top1-top2)/(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(left1-left2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth+i*iconHeight, firstHeight-i*(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2)), mPaint);
					}
				}
			}else if(left1>left2 && top1>=top2){
				if(Math.abs(left1-left2)>Math.abs(top1-top2)){
					iconWidth=Math.abs(left1-left2)/(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(top1-top2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth+i*(mBitmapCircleIcon.getWidth()+DensityUtil.dip2px(mContext, 2)), firstHeight+i*iconHeight, mPaint);
					}
				}else{
					iconWidth=Math.abs(top1-top2)/(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2));
					if(iconWidth==0){
						iconWidth=1;
					}
					iconHeight=Math.abs(left1-left2)/iconWidth;
					firstWidth=left2;
					firstHeight=top2-mBitmapCircleIcon.getHeight()/2;
					for(int i=0;i<iconWidth;i++){
						canvas.drawBitmap(mBitmapCircleIcon, firstWidth+i*iconHeight, firstHeight+i*(mBitmapCircleIcon.getHeight()+DensityUtil.dip2px(mContext, 2)), mPaint);
					}
				}
			}
		
		}
		// 处于可编辑状态才画边框和控制图标
		if (isEditable && isVisiable) {
			if (leftOrRight == 1) {
				mSelectPath.reset();
				mSelectPath.moveTo(lLeft, lTop);
				mSelectPath.lineTo(lRight, lTop);
				mSelectPath.lineTo(lRight, lBottom);
				mSelectPath.lineTo(lLeft, lBottom);
				mSelectPath.lineTo(lLeft, lTop);
				canvas.drawPath(mSelectPath, mSelectPaint);

			}
			if (leftOrRight == 2) {
				mSelectPath.reset();
				mSelectPath.moveTo(rLeft, rTop);
				mSelectPath.lineTo(rRight, rTop);
				mSelectPath.lineTo(rRight, rBottom);
				mSelectPath.lineTo(rLeft, rBottom);
				mSelectPath.lineTo(rLeft, rTop);
				canvas.drawPath(mSelectPath, mSelectPaint);

			}
			if(leftOrRight != 2 && leftOrRight != 1){
				mSelectPath.reset();
				mSelectPath.moveTo(lRight-theLargen / 2, lBottom-theLargen / 2);
				mSelectPath.lineTo(rLeft+theLargen / 2, lBottom-theLargen / 2);
				mSelectPath.lineTo(rLeft+theLargen / 2, rTop+theLargen / 2);
				mSelectPath.lineTo(lRight-theLargen / 2, rTop+theLargen / 2);
				mSelectPath.lineTo(lRight-theLargen / 2, lBottom-theLargen / 2);
				canvas.drawPath(mSelectPath, mSelectPaint);
				showChooseSlideUitls();
			}
			
			mFramePath.reset();
			mLTPoint.x = DensityUtil.dip2px(mContext, 18);
			mLTPoint.y = DensityUtil.dip2px(mContext, 18);
			mRTPoint.x = getWidth() - DensityUtil.dip2px(mContext, 18);
			mRTPoint.y = DensityUtil.dip2px(mContext, 18);
			mRBPoint.x = getWidth() - DensityUtil.dip2px(mContext, 18);
			mRBPoint.y = getHeight() - DensityUtil.dip2px(mContext, 18);
			mLBPoint.x = DensityUtil.dip2px(mContext, 18);
			mLBPoint.y = getHeight() - DensityUtil.dip2px(mContext, 18);

			mFramePath.moveTo(mLTPoint.x, mLTPoint.y);
			mFramePath.lineTo(mRTPoint.x, mRTPoint.y);
			mFramePath.lineTo(mRBPoint.x, mRBPoint.y);
			mFramePath.lineTo(mLBPoint.x, mLBPoint.y);
			mFramePath.lineTo(mLTPoint.x, mLTPoint.y);
			canvas.drawPath(mFramePath, mFramePaint);

			mDeleteDrawable.setBounds(mDeletePoint.x - mDrawableWidth / 2, mDeletePoint.y - mDrawableHeight / 2, mDeletePoint.x + mDrawableWidth / 2,
					mDeletePoint.y + mDrawableHeight / 2);
			mDeleteDrawable.draw(canvas);

			// mControllerDrawable.setBounds(mControllerPoint.x - mDrawableWidth
			// / 2, mControllerPoint.y - mDrawableHeight / 2, mControllerPoint.x
			// + mDrawableWidth / 2, mControllerPoint.y + mDrawableHeight / 2);
			// mControllerDrawable.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int iAction = event.getAction();
		iCurrentx = (int) event.getX();
		iCurrenty = (int) event.getY();

		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			mStatus = JudgeStatus(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (isVisiable && mStatus == STATUS_DELETE && mStatus == JudgeStatus(event.getX(), event.getY())) {
				mOnRemoveViewListener.removeView(mSlideLockerInfo, this);
				isEditable = false;
				return true;
			}

			if (isVisiable && mStatus == STATUS_CONTROLLER && mStatus == JudgeStatus(event.getX(), event.getY())) {
				// showControllerView();
				return true;
			}

			mStatus = STATUS_INIT;

			if (setImage && !isVisiable) {
				mOnFocuseChangeListener.focuseChange(this);
			}
			
//			Rect rect = new Rect(lLeft + theLargen / 2, lTop + theLargen / 2, rLeft + theLargen / 2, rTop + theLargen / 2);  
//	        if(rect.contains((int)iCurrentx, (int)iCurrenty)){  
//	            Log.i("debug", "范围之内");
//	        }  
//	        else{  
//	            Log.i("debug", "范围之外");
//	        } 
			leftOrRight=0;
			SlideContainerView.this.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			left = getLeft() + iDeltx;
			top = getTop() + iDelty;
			right = getRight() + iDeltx;
			bottom = getBottom() + iDelty;

			if (iDeltx != 0 || iDelty != 0) {

				if (left < DensityUtil.dip2px(mContext, 10)) {
					left = DensityUtil.dip2px(mContext, 10);
					right = left + getWidth();
				}

				if (right > screenWidth) {
					right = screenWidth;
					left = right - getWidth();
				}

				if (top < 0) {
					top = 0;
					bottom = top + getHeight();
				}

				if (bottom > screenHeight) {
					bottom = screenHeight;
					top = bottom - getHeight();
				}

				mContainerLayoutParams.topMargin = top;
				mSlideLockerInfo.setY(top);
				mOnUpdateViewListener.updateView(SlideContainerView.this, mContainerLayoutParams);
				d1.setLayoutParams(new LayoutParams(theLargen, theLargen, d1.getZhuIconCenter().get(0), d1.getZhuIconCenter().get(1)));
				d2.setLayoutParams(new LayoutParams(theLargen, theLargen, d2.getZhuIconCenter().get(0), d2.getZhuIconCenter().get(1)));
				layout(left, top, right, bottom);

			}

			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

	private void showControllerView() {
//		if (controllerView != null && controllerView.getParent() == null) {
//			LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_LOCKER_SLIDE);
//			mController_container_layout.removeAllViews();
//			mController_container_layout.addView(controllerView);
//			
//		}
		mController_container_layout.removeAllViews();
		Intent intent = new Intent(mContext, SelectImageActivity.class);
		intent.putExtra("from", ChooseStickerUtils.FROM_LOCKER_SLIDE);
		((Activity)mContext).startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
	}

	/**
	 * 设置是否显示绘制操作按钮
	 * 
	 * @param isVisiable
	 */
	public void setVisible(boolean isVisiable) {
		this.isVisiable = isVisiable;
		leftOrRight = -1;
		invalidate();
	}

	public void setImage(boolean b) {
		setImage = b;
	}

	public void setSlideLockerInfo(SlideLockerInfo mSlideLockerInfo) {
		this.mSlideLockerInfo = mSlideLockerInfo;
		init();
//		if (isVisiable) {
//			showControllerView();
//		}
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

	public void setLargen(int largen) {
		this.theLargen = largen;
		mHandler.sendEmptyMessage(1);
	}

	public void notifyLargened() {
		d1.saveSize();
		d2.saveSize();
	}

	/**
	 * 两个点之间的距离
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return (float) Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 */
	private int JudgeStatus(float x, float y) {
		PointF touchPoint = new PointF(x, y);
		PointF deletePointF = new PointF(mDeletePoint);
		PointF controllerPointF = new PointF(mControllerPoint);

		float distanceToDelete = distance4PointF(touchPoint, deletePointF);
		float distanceToController = distance4PointF(touchPoint, controllerPointF);
		if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_DELETE;
		}
		if (distanceToController < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
			return STATUS_CONTROLLER;
		}
		return STATUS_DRAG;

	}

	/**
	 * 画箭头
	 */
	private void drawAL(int sx, int sy, int ex, int ey) {
		double H = 8; // 箭头高度
		double L = 3.5; // 底边的一半
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // 箭头角度
		double arraow_len = Math.sqrt(L * L + H * H) + DensityUtil.dip2px(mContext, 10); // 箭头的长度
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// 画线
		mCanvas.drawLine(sx, sy, ex, ey, mPaint);
		Path triangle = new Path();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.close();
		mCanvas.drawPath(triangle, mPaint);

	}

	// 计算
	public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	public void setOnUpdateViewListener(OnUpdateViewListener onUpdateViewListener) {
		this.mOnUpdateViewListener = onUpdateViewListener;
	}

	public void setContainerLayoutParams(android.widget.RelativeLayout.LayoutParams containerLayoutParams) {
		this.mContainerLayoutParams = containerLayoutParams;
	}

	public void setOnRemoveViewListener(OnRemoveLockerViewListener onRemoveViewListener) {
		this.mOnRemoveViewListener = onRemoveViewListener;
	}

	public void setOnFocuseChangeListener(OnFocuseChangeListener onFocuseChangeListener) {
		this.mOnFocuseChangeListener = onFocuseChangeListener;
	}

	public void setControllerContainerLayout(LinearLayout controller_container_layout) {
		this.mController_container_layout = controller_container_layout;
	}

	@Override
	public void selectImage(String imageUrl) {
		Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageUrl);
		if (leftOrRight == 1) {
			if (bitmap != null) {
				mSlideLockerInfo.getBitmaps()[0] = bitmap;
			}
		}
		if (leftOrRight == 2) {
			if (bitmap != null) {
				mSlideLockerInfo.getBitmaps()[1] = bitmap;
			}
		}
		updateImage();

	}

	public void selectImage(Bitmap bitmap) {
		if (leftOrRight == 1) {
			if (bitmap != null) {
				mSlideLockerInfo.getBitmaps()[0] = bitmap;
			}
		}
		if (leftOrRight == 2) {
			if (bitmap != null) {
				mSlideLockerInfo.getBitmaps()[1] = bitmap;
			}
		}
		updateImage();

	}
	public void selectImage(int position) {
		int bitmapRes=bitmapIconRes[position];
		if(bitmapRes>0){
			mBitmapCircleIcon = BitmapFactory.decodeResource(mContext.getResources(),bitmapRes);
			mSlideLockerInfo.setBitmapRes(position);
		}else{
			mBitmapCircleIcon=null;
			mSlideLockerInfo.setBitmapRes(position);
		}
		invalidate();		
	}
}
