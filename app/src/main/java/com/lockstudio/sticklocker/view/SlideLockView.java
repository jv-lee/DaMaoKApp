package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.lockstudio.sticklocker.Interface.UnlockListener;
import com.lockstudio.sticklocker.model.SlideLockerInfo;
import com.lockstudio.sticklocker.util.DensityUtil;

import cn.opda.android.activity.R;

@SuppressWarnings("deprecation")
public class SlideLockView extends AbsoluteLayout {
	
	private Context mContext;
	private SharedPreferences sp;
	private Bitmap mBitmapCircleDefault;
	private Bitmap mBitmapCircleGreen;
	private DragViewsForLock dLock;
	private int centerX2;
	private int theLargen;
	private int centerY2;
	private SlideLockerInfo mSlideLockerInfo;
	private UnlockListener mUnlockListener;
	private Paint mPaint;
	private Canvas mCanvas;
	private boolean oneVisible;
	private Bitmap mBitmapCircleIcon;
	private int bitmapIconRes[]={0,R.drawable.icon_slide11,R.drawable.icon_slide31,
			R.drawable.icon_slide41,R.drawable.icon_slide51};
	
	int left;
	int top;
	int centerX1;
	int centerY1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				left = msg.arg1;
				top = msg.arg2;
				centerX1 = left + dLock.getWidth() / 2;
				centerY1 = top + dLock.getHeight() / 2;
				if (isCollsionWithRect(centerX1, centerY1, dLock.getWidth() / 3 * 2, dLock.getHeight() / 3 * 2, centerX2, centerY2,
						dLock.getWidth() / 3 * 2, dLock.getHeight() / 3 * 2)) {
					dLock.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleGreen));
				} else {
					dLock.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleDefault));
				}
				oneVisible=true;
				invalidate();
				break;
			case 1:
				left = msg.arg1;
				top = msg.arg2;
				centerX1 = left + dLock.getWidth() / 2;
				centerY1 = top + dLock.getHeight() / 2;
				if (isCollsionWithRect(centerX1, centerY1, dLock.getWidth() / 3 * 2, dLock.getHeight() / 3 * 2, centerX2, centerY2,
						dLock.getWidth() / 3 * 2, dLock.getHeight() / 3 * 2)) {
					dLock.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleGreen));
					mUnlockListener.OnUnlockSuccess();
				} else {
					dLock.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleDefault));
					dLock.setLayoutParams(new LayoutParams(theLargen, theLargen, dLock.getPoint().get(0), dLock.getPoint().get(1)));
				}
				oneVisible=false;
				invalidate();
				break;

			default:
				break;
			}
		}
	};
	
	public SlideLockView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SlideLockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public SlideLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	private void init() {
		setWillNotDraw(false);
		sp = mContext.getSharedPreferences("myprefence", Context.MODE_MULTI_PROCESS);
		theLargen = sp.getInt("largen", DensityUtil.dip2px(mContext, 60));
		if (dLock == null) {
			dLock = new DragViewsForLock(mContext, mHandler);
			dLock.setSlideLockerInfo(mSlideLockerInfo);
			addView(dLock, new LayoutParams(theLargen, theLargen, dLock.getZhuIconCenter().get(0), dLock.getZhuIconCenter().get(1)));
			addView(new View(mContext), new LayoutParams(theLargen, theLargen, dLock.getZhuIconCenter().get(0), dLock.getScreenHeight()));
			updateImage();
		}
		int left2 = mSlideLockerInfo.getLeft2();
		int top2 = mSlideLockerInfo.getTop2();
		int right2 = mSlideLockerInfo.getRight2();
		int bottom2 = mSlideLockerInfo.getBottom2();
//		int left2 = sp.getInt("left2", 0);
//		int top2 = sp.getInt("top2", 0);
//		int right2 = sp.getInt("right2", 0);
//		int bottom2 = sp.getInt("bottom2", 0);
		
		int p = mSlideLockerInfo.getBitmapRes();
		if(p>=bitmapIconRes.length){
			p = 1;
		}
		mBitmapCircleIcon=BitmapFactory.decodeResource(mContext.getResources(),bitmapIconRes[p]);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 2));
		PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8}, 1);  
		mPaint.setPathEffect(effects); 
		mPaint.setColor(Color.RED);

		centerX2 = (left2 + right2) / 2;
		centerY2 = (top2 + bottom2) / 2;
	}
	public void setSlideLockerInfo(SlideLockerInfo mSlideLockerInfo) {
		this.mSlideLockerInfo = mSlideLockerInfo;
		init();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.mCanvas = canvas;
//		int left11 = mSlideLockerInfo.getLeft1()+ theLargen / 2;
//		int top11 = mSlideLockerInfo.getTop1()+ theLargen / 2;
//		int left22 = mSlideLockerInfo.getLeft2()+ theLargen / 2;
//		int top22 = mSlideLockerInfo.getTop2()+ theLargen / 2;
//		drawAL(left1, top1, (Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2);
//		canvas.drawLine((Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2, left2, top2, mPaint);
		int iconWidth=0;
		int iconHeight=0;
		int firstWidth=0;
		int firstHeight=0;
		if(oneVisible){
			int left1 = left+ theLargen / 2;
			int top1 = top+ theLargen / 2;
			int left2 = mSlideLockerInfo.getLeft2()+ theLargen / 2;
			int top2 = mSlideLockerInfo.getTop2()+ theLargen / 2;
			
			if(mBitmapCircleIcon==null){
//				drawAL(left1, top1, (Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2);
//				canvas.drawLine((Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2, left2, top2, mPaint);
			}else{
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
			
			
//			drawAL(left1, top1, (Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2);
//			canvas.drawLine((Math.abs(left2) + Math.abs(left1)) / 2, (Math.abs(top2) + Math.abs(top1)) / 2, left2, top2, mPaint);
		}
	}
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

	public void updateImage(){
		mBitmapCircleDefault = BitmapFactory.decodeResource(getResources(),R.drawable.girl);
		mBitmapCircleGreen = BitmapFactory.decodeResource(getResources(),R.drawable.boy);
		Bitmap[] bitmaps = mSlideLockerInfo.getBitmaps();
		if (bitmaps[0] != null){
			mBitmapCircleDefault = bitmaps[0];
		} 
		if (bitmaps[1] != null){
			mBitmapCircleGreen = bitmaps[1];
		} 
		dLock.setBackgroundDrawable(new BitmapDrawable(mBitmapCircleDefault));
	}

	private boolean isCollsionWithRect(int x1, int y1, int w1, int h1,int x2,int y2, int w2, int h2) {
		if (x1 >= x2 && x1 >= x2 + w2) {
			return false;
		} else if (x1 <= x2 && x1 + w1 <= x2) {
			return false;
		} else if (y1 >= y2 && y1 >= y2 + h2) {
			return false;
		} else if (y1 <= y2 && y1 + h1 <= y2) {
			return false;
		}
		return true;
	}
	
	public void disableInput() {
		dLock.disableInput();
	}
	
	public static interface OnCoupleListener {
		void unlockSuccsed();
	}
	
	public void setUnlockListener(UnlockListener mUnlockListener) {
		this.mUnlockListener = mUnlockListener;
	}

}
