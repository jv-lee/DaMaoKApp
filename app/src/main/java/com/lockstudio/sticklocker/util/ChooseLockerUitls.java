package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.AppLockerInfo;
import com.lockstudio.sticklocker.model.CoupleLockerInfo;
import com.lockstudio.sticklocker.model.LockerInfo;
import com.lockstudio.sticklocker.model.LoveLockerInfo;
import com.lockstudio.sticklocker.model.NinePatternLockerInfo;
import com.lockstudio.sticklocker.model.NumPasswordLockerInfo;
import com.lockstudio.sticklocker.model.SlideLockerInfo;
import com.lockstudio.sticklocker.model.TwelvePatternLockerInfo;
import com.lockstudio.sticklocker.model.WordPasswordLockerInfo;
import com.lockstudio.sticklocker.view.SimpleToast;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择锁屏样式的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseLockerUitls {
	private Context mContext;
	private View view;
	private GridView locker_gridview;
	private OnLockerSelectorListener mOnLockerSelectorListener;
	private ChooseLockerAdapter chooseLockerAdapter;
	private int lockerStyle;
	private ArrayList<LockerInfo> lockerInfos;

	public ChooseLockerUitls(Context context) {
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_locker_layout, null);
		locker_gridview = (GridView) view.findViewById(R.id.locker_gridview);
		initData();
	}

	private void initData() {

		lockerInfos = new ArrayList<LockerInfo>();

		LockerInfo lockerInfo = new LockerInfo();
		lockerInfo.setStyleId(LockerInfo.StyleFree);
		lockerInfo.setSelecter(lockerStyle == LockerInfo.StyleFree);
		lockerInfo.setLockerName("自由锁");
		lockerInfo.setPreviewRes(R.drawable.locker_free);
		lockerInfos.add(lockerInfo);
		
		AppLockerInfo appLockerInfo = new AppLockerInfo();
		appLockerInfo.setStyleId(LockerInfo.StyleApp);
		appLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleApp);
		appLockerInfo.setWidth(LockApplication.getInstance().getConfig().getScreenWidth()-DensityUtil.dip2px(mContext, 10));
		appLockerInfo.setHeight(LockApplication.getInstance().getConfig().getScreenHeight()/3*2);
		appLockerInfo.setX(DensityUtil.dip2px(mContext, 15));
		appLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight()/3 + DensityUtil.dip2px(mContext, 10));
		appLockerInfo.setLockerName("应用锁屏");
		appLockerInfo.setPreviewRes(R.drawable.locker_apps);
		lockerInfos.add(appLockerInfo);

		SlideLockerInfo slideLockerInfo = new SlideLockerInfo();
		slideLockerInfo.setStyleId(LockerInfo.StyleSlide);
		slideLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleSlide);
		slideLockerInfo.setHeight(LockApplication.getInstance().getConfig().getScreenWidth());
		slideLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - slideLockerInfo.getHeight());
		slideLockerInfo.setLeft1(0);
		slideLockerInfo.setTop1(0);
		slideLockerInfo.setRight1(0);
		slideLockerInfo.setBottom1(0);
		slideLockerInfo.setLeft2(0);
		slideLockerInfo.setTop2(0);
		slideLockerInfo.setRight2(0);
		slideLockerInfo.setBottom2(0);
		slideLockerInfo.setBitmapRes(0);
		slideLockerInfo.setFirst(true);
		slideLockerInfo.setLockerName("滑块锁屏");
		slideLockerInfo.setPreviewRes(R.drawable.locker_slider);
		lockerInfos.add(slideLockerInfo);
		
		
//		ImagePasswordLockerInfo imagePasswordLockerInfo = new ImagePasswordLockerInfo();
//		imagePasswordLockerInfo.setStyleId(LockerInfo.StyleImagePassword);
//		imagePasswordLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleImagePassword);
//		imagePasswordLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
//		imagePasswordLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
//		imagePasswordLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - imagePasswordLockerInfo.getWidth()) / 2);
//		imagePasswordLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - imagePasswordLockerInfo.getHeight() - 100);
//		imagePasswordLockerInfo.setLockerName("图片密码锁");
//		imagePasswordLockerInfo.setPreviewRes(R.drawable.locker_word_password);
//		lockerInfos.add(imagePasswordLockerInfo);
		
		

		WordPasswordLockerInfo wordPasswordLockerInfo = new WordPasswordLockerInfo();
		wordPasswordLockerInfo.setStyleId(LockerInfo.StyleWordPassword);
		wordPasswordLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleWordPassword);
		wordPasswordLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.word_lock_height)));
		wordPasswordLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.word_lock_width)));
		wordPasswordLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - wordPasswordLockerInfo.getWidth()) / 2);
		wordPasswordLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - wordPasswordLockerInfo.getHeight() - 100);
		wordPasswordLockerInfo.setLockerName("文字密码锁");
		wordPasswordLockerInfo.setPreviewRes(R.drawable.locker_word_password);
		lockerInfos.add(wordPasswordLockerInfo);
		
		NumPasswordLockerInfo numPasswordLockerInfo = new NumPasswordLockerInfo();
		numPasswordLockerInfo.setStyleId(LockerInfo.StyleNumPassword);
		numPasswordLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleNumPassword);
		numPasswordLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.word_lock_height)));
		numPasswordLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.word_lock_width)));
		numPasswordLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - numPasswordLockerInfo.getWidth()) / 2);
		numPasswordLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - numPasswordLockerInfo.getHeight() - 100);
		numPasswordLockerInfo.setLockerName("数字密码锁");
		numPasswordLockerInfo.setPreviewRes(R.drawable.locker_num_password);
		lockerInfos.add(numPasswordLockerInfo);

		NinePatternLockerInfo ninePatternLockerInfo = new NinePatternLockerInfo();
		ninePatternLockerInfo.setStyleId(LockerInfo.StyleNinePattern);
		ninePatternLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleNinePattern);
		ninePatternLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		ninePatternLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		ninePatternLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - ninePatternLockerInfo.getWidth()) / 2);
		ninePatternLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - ninePatternLockerInfo.getHeight() - 200);
		ninePatternLockerInfo.setLockerName("九宫格");
		ninePatternLockerInfo.setPreviewRes(R.drawable.locker_ninepattern);
		lockerInfos.add(ninePatternLockerInfo);

		TwelvePatternLockerInfo twelvePatternLockerInfo = new TwelvePatternLockerInfo();
		twelvePatternLockerInfo.setStyleId(LockerInfo.StyleTwelvePattern);
		twelvePatternLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleTwelvePattern);
		twelvePatternLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		twelvePatternLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		twelvePatternLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - twelvePatternLockerInfo.getWidth()) / 2);
		twelvePatternLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - twelvePatternLockerInfo.getHeight() - 100);
		twelvePatternLockerInfo.setLockerName("十二宫格");
		twelvePatternLockerInfo.setPreviewRes(R.drawable.locker_twelvepattern);
		lockerInfos.add(twelvePatternLockerInfo);

		CoupleLockerInfo coupleLockerInfo = new CoupleLockerInfo();
		coupleLockerInfo.setStyleId(LockerInfo.StyleCouple);
		coupleLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleCouple);
		coupleLockerInfo.setHeight(DensityUtil.dip2px(mContext, 90));
		coupleLockerInfo.setX(DensityUtil.dip2px(mContext, 15));
		coupleLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - coupleLockerInfo.getHeight() - 300);
		coupleLockerInfo.setLockerName("情侣锁屏");
		coupleLockerInfo.setPreviewRes(R.drawable.locker_lovers);
		lockerInfos.add(coupleLockerInfo);
		
		LoveLockerInfo loveLockerInfo = new LoveLockerInfo();
		loveLockerInfo.setStyleId(LockerInfo.StyleLove);
		loveLockerInfo.setSelecter(lockerStyle == LockerInfo.StyleLove);
		loveLockerInfo.setHeight((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		loveLockerInfo.setWidth((int) (mContext.getResources().getDimension(R.dimen.lock_patternview_width)));
		loveLockerInfo.setX((LockApplication.getInstance().getConfig().getScreenWidth() - loveLockerInfo.getWidth()) / 2);
		loveLockerInfo.setY(LockApplication.getInstance().getConfig().getScreenHeight() - loveLockerInfo.getHeight() - 100);
		loveLockerInfo.setLockerName("爱心锁屏");
		loveLockerInfo.setPreviewRes(R.drawable.locker_love);
		lockerInfos.add(loveLockerInfo);

		chooseLockerAdapter = new ChooseLockerAdapter(mContext, lockerInfos);
		locker_gridview.setAdapter(chooseLockerAdapter);
		locker_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LockerInfo lockerInfo = (LockerInfo) parent.getItemAtPosition(position);
				if (mOnLockerSelectorListener != null) {
					if (lockerInfo.getStyleId() == LockerInfo.StyleFree) {
						SimpleToast.makeText(mContext, "已添加自由锁,任意滑动解锁", SimpleToast.LENGTH_SHORT).show();
					}
					mOnLockerSelectorListener.selectLocker(lockerInfo);
				}
			}
		});
		setGridViewWidth();

	}

	private void setGridViewWidth() {
		LayoutParams params = new LayoutParams(chooseLockerAdapter.getCount() * (DensityUtil.dip2px(mContext, 76)), LayoutParams.WRAP_CONTENT);
		locker_gridview.setLayoutParams(params);
		locker_gridview.setHorizontalSpacing(DensityUtil.dip2px(mContext, 4));
		locker_gridview.setVerticalSpacing(0);
		locker_gridview.setColumnWidth(DensityUtil.dip2px(mContext, 72));
		locker_gridview.setStretchMode(GridView.NO_STRETCH);
		locker_gridview.setNumColumns(chooseLockerAdapter.getCount());
	}

	public View getView() {
		return view;
	}

	public void setOnLockerSelectorListener(OnLockerSelectorListener onLockerSelectorListener) {
		this.mOnLockerSelectorListener = onLockerSelectorListener;
	}

	public interface OnLockerSelectorListener {
		void selectLocker(LockerInfo lockerInfo);
	}

	public void setSelectLocker(LockerInfo selectLocker) {
		if (selectLocker != null) {
			this.lockerStyle = selectLocker.getStyleId();
			for (int i = 0; i < lockerInfos.size(); i++) {
				LockerInfo lockerInfo = lockerInfos.get(i);
				if (lockerInfo.getStyleId() == lockerStyle) {
					lockerInfo.setSelecter(true);
				} else {
					lockerInfo.setSelecter(false);
				}
			}
		} else {
			for (int i = 0; i < lockerInfos.size(); i++) {
				LockerInfo lockerInfo = lockerInfos.get(i);
				lockerInfo.setSelecter(false);
			}
		}
		chooseLockerAdapter.notifyDataSetChanged();
	}

	public class ChooseLockerAdapter extends BaseAdapter {
		private ArrayList<LockerInfo> lockerInfos = new ArrayList<LockerInfo>();
		private LayoutInflater inflater;

		public ChooseLockerAdapter(Context mContext, ArrayList<LockerInfo> lockerInfos) {
			this.lockerInfos = lockerInfos;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return lockerInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return lockerInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gridview_item_locker, parent, false);
				holder.locker_imageview = (ImageView) convertView.findViewById(R.id.locker_imageview);
				holder.select_imageview = (ImageView) convertView.findViewById(R.id.select_imageview);
				holder.locker_textview = (TextView) convertView.findViewById(R.id.locker_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final LockerInfo lockerInfo = lockerInfos.get(position);
			holder.locker_imageview.setImageResource(lockerInfo.getPreviewRes());
			holder.locker_textview.setText(lockerInfo.getLockerName());
			if (lockerInfo.isSelecter()) {
				holder.select_imageview.setVisibility(View.VISIBLE);
			} else {
				holder.select_imageview.setVisibility(View.GONE);

			}

			return convertView;
		}

		class ViewHolder {
			public ImageView locker_imageview;
			public ImageView select_imageview;
			public TextView locker_textview;
		}
	}
}
