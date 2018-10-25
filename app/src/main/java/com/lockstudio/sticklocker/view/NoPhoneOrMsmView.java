package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.CallLog.Calls;
import android.view.View;
import android.view.View.OnClickListener;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.util.DensityUtil;

import cn.opda.android.activity.R;

public class NoPhoneOrMsmView extends View implements OnClickListener {

	private Paint mPaint,mPaint1;
	private Rect mBounds;
	private int mCount;
	private Context mContext;

	public NoPhoneOrMsmView(Context context) {
		super(context);
		this.mContext=context;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBounds = new Rect();
		setOnClickListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setColor(0xb0000000);
		mPaint1.setColor(0x35ffffff);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		mPaint.setColor(Color.WHITE);
		
		if(LockApplication.getInstance().getConfig().isShowPhoneSms_face()){
			mPaint.setTextSize(50);
			String text_show="如果想要关闭该功能，请前往设置修改";
			mPaint.getTextBounds(text_show, 0, text_show.length(), mBounds);
			float textWidth = mBounds.width();
			float textHeight = mBounds.height();
			canvas.drawText(text_show, getWidth() / 2 - textWidth / 2, getHeight() / 3 + textHeight / 2, mPaint);
			LockApplication.getInstance().getConfig().setShowPhoneSms_face(!LockApplication.getInstance().getConfig().isShowPhoneSms_face());
		}
		mPaint.setTextSize(40);
		Bitmap phonebp = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_no_phone) ; 
		Bitmap smsbp = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_no_sms) ; 
		if(readMissCall()>0){
			String text = "未接电话 ("+String.valueOf(readMissCall()+")");
			canvas.drawLine(DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 342) - phonebp.getHeight()/2,
					getWidth()-DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 342) - phonebp.getHeight()/2, mPaint1);
			canvas.drawLine(DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 380) + phonebp.getHeight()/2,
					getWidth()-DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 380) + phonebp.getHeight()/2, mPaint1);
			canvas.drawBitmap(phonebp, DensityUtil.dip2px(mContext, 30), DensityUtil.dip2px(mContext, 363) - phonebp.getHeight()/2, mPaint);
			canvas.drawText(text, DensityUtil.dip2px(mContext, 40)+phonebp.getWidth(), DensityUtil.dip2px(mContext, 367), mPaint);
		}
		if(getNewSmsCount()>0){
			canvas.drawLine(DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 380) + phonebp.getHeight()/2,
					getWidth()-DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 380) + phonebp.getHeight()/2, mPaint1);
			String text1 = "未读信息 ("+String.valueOf(getNewSmsCount()+")");
			canvas.drawBitmap(smsbp, DensityUtil.dip2px(mContext, 30), DensityUtil.dip2px(mContext, 420) - smsbp.getHeight()/2, mPaint);
			canvas.drawText(text1, DensityUtil.dip2px(mContext, 40)+smsbp.getWidth(), DensityUtil.dip2px(mContext, 430), mPaint);
			canvas.drawLine(DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 445) + smsbp.getHeight()/2,
					getWidth()-DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 445) + smsbp.getHeight()/2, mPaint1);
		}
	}

	@Override
	public void onClick(View v) {
		//mCount++;
		//invalidate();
		setVisibility(View.GONE);
	}
	
	 /**
     * 获取未读短信
     * @return
     */
    public int getNewSmsCount() {  
        int result = 0;  
        Cursor csr = mContext.getContentResolver().query(Uri.parse("content://sms"), null,  
                "type = 1 and read = 0", null, null);  
        if (csr != null) {  
            result = csr.getCount();  
            csr.close();  
        }  
        return result;  
    }  
    /**
     * 获取未接电话
     * @return
     */
    public int readMissCall() {  
        int result = 0;  
        Cursor cursor = mContext.getContentResolver().query(Calls.CONTENT_URI, new String[] {
                Calls.TYPE  
            }, " type=? and new=?", new String[] {  
                    Calls.MISSED_TYPE + "", "1"  
            }, "date desc");  
      
        if (cursor != null) {  
            result = cursor.getCount();  
            cursor.close();  
        }  
        return result;  
    }  

}
