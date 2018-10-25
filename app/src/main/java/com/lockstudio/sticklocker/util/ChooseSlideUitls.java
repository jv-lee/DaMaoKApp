package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.lockstudio.sticklocker.view.SlideContainerView;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择锁屏样式的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseSlideUitls {
	private SlideContainerView mSlideLockView;
	private Bitmap mSelectBitmap;
	private Context mContext;
	private View view;
	private GridView locker_gridview;
//	private OnLockerSelectorListener mOnLockerSelectorListener;
	private ChooseLockerAdapter chooseLockerAdapter;
	private int lockerStyle;
	private ArrayList<String> lockerInfos;
	private int bitmapRes[]={R.drawable.icon_slide_wu,R.drawable.icon_slide1,R.drawable.icon_slide3,
			R.drawable.icon_slide4,R.drawable.icon_slide5};
	private String bitmapName[]={"无","圆点","空心","小花","星星"};

	public ChooseSlideUitls(Context context,SlideContainerView containerView) {
		this.mContext = context;
		this.mSlideLockView  = containerView;
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_slide_layout, null);
		locker_gridview = (GridView) view.findViewById(R.id.slide_gridview);
		initData();
	}

	private void initData() {

		lockerInfos = new ArrayList<String>();
		for(int i=0;i<5;i++){
			lockerInfos.add(bitmapName[i]);
		}
		chooseLockerAdapter = new ChooseLockerAdapter(mContext, lockerInfos);
		locker_gridview.setAdapter(chooseLockerAdapter);
		locker_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				LockerInfo lockerInfo = (LockerInfo) parent.getItemAtPosition(position);
//				if (mOnLockerSelectorListener != null) {
//					if (lockerInfo.getStyleId() == LockerInfo.StyleFree) {
//						SimpleToast.makeText(mContext, "已添加自由锁,任意滑动解锁", SimpleToast.LENGTH_SHORT).show();
//					}
//					mOnLockerSelectorListener.selectLocker(lockerInfo);
//				}
				//mSelectBitmap=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.diy_like_pressed);
				mSlideLockView.selectImage(position);
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
	
	public class ChooseLockerAdapter extends BaseAdapter {
		private ArrayList<String> lockerInfos = new ArrayList<String>();
		private LayoutInflater inflater;
		
		public ChooseLockerAdapter(Context mContext, ArrayList<String> lockerInfos) {
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
				convertView = inflater.inflate(R.layout.gridview_item_locker,null);
				holder.locker_imageview = (ImageView) convertView.findViewById(R.id.locker_imageview);
//				holder.select_imageview = (ImageView) convertView.findViewById(R.id.select_imageview);
				holder.locker_textview = (TextView) convertView.findViewById(R.id.locker_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
//			final LockerInfo lockerInfo = lockerInfos.get(position);
			holder.locker_imageview.setImageResource(bitmapRes[position]);
			holder.locker_textview.setText(lockerInfos.get(position));
//			if (lockerInfo.isSelecter()) {
//				holder.select_imageview.setVisibility(View.VISIBLE);
//			} else {
//				holder.select_imageview.setVisibility(View.GONE);
//
//			}

			return convertView;
		}

		class ViewHolder {
			public ImageView locker_imageview;
			public ImageView select_imageview;
			public TextView locker_textview;
		}
	}
}
