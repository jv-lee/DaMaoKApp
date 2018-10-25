package com.lockstudio.sticklocker.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.activity.LockThemePreviewActivity;
import com.lockstudio.sticklocker.activity.WebviewActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseFragment;
import com.lockstudio.sticklocker.model.FixedSpeedScroller;
import com.lockstudio.sticklocker.model.LockThemeInfo;
import com.lockstudio.sticklocker.service.DownloadService;
import com.lockstudio.sticklocker.util.AppManagerUtils;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DeviceInfoUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.view.BannerImageView;
import com.lockstudio.sticklocker.view.CustomIndView;
import com.lockstudio.sticklocker.view.HeaderAndFooterGridview;
import com.lockstudio.sticklocker.view.HeaderAndFooterGridview.ShowFootViewListener;
import com.lockstudio.sticklocker.view.HeaderAndFooterGridview.ShowHeadViewListener;
import com.lockstudio.sticklocker.view.NoScrollViewPager;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.TwoBallsLoadingView;
import com.lockstudio.sticklocker.viewpage.AdViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.opda.android.activity.R;

public class FeaturedFragment extends BaseFragment implements ShowFootViewListener, ShowHeadViewListener, AdapterView.OnItemClickListener, OnClickListener {

	private HeaderAndFooterGridview featured_gridview;
	private HeaderAndFooterGridview new_gridview;
	private HeaderAndFooterGridview better_gridview;
	private TextView footer_textview, footer_textview_2, footer_textview_3;
	private TwoBallsLoadingView footer_loading_view, footer_loading_view_2, footer_loading_view_3;
	private FeaturedAdapter adapter;
	private FeaturedAdapter newThemeAdapter;
	private FeaturedAdapter betterThemeAdapter;
	private ArrayList<BannerInfo> bannerInfos=null;
	private ProgressBar loadProgressBar,loadProgressBar1;
	private TextView loadTextView,loadTextView1;
	public static final int PAGE_SIZE = 18;

	private final int MSG_REQUEST_URL_JSON = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;
	private final int MSG_NOTIFY_BANNER_CHANGED = 103;
	private final int JSON_NULL = 104;
	private final int MSG_REQUEST_URL_JSON_2 = 1000;
	private final int MSG_NOTIFY_CHANGED_2 = 1001;
	private final int JSON_NULL_2 = 1002;
	private final int MSG_REQUEST_URL_JSON_4 = 1003;
	private final int MSG_NOTIFY_CHANGED_4 = 1004;
	private final int JSON_NULL_4 = 1005;

	private final int MSG_REQUEST_URL_JSON_3 = 2000;
	private final int MSG_NOTIFY_CHANGED_3 = 2001;
	private final int JSON_NULL_3 = 2002;
	private boolean isLock = false;
	private boolean isLock2 = false;
	private boolean isLock3 = false;
	private boolean headLock = false;
	private int maxId;

	private int maxItemWidth, maxItemHeight;
	private NoScrollViewPager viewPager;
	private int page = 0,tab_hot_num=1,tab_better_num=1;
//	private View tab_hot_indicator, tab_new_indicator, tab_better_indicator;
	private TextView tab_hot, tab_new, tab_better,tab_time,tab_praise;
	private LinearLayout layout_tab_hot, layout_tab_new, layout_tab_better,main_paixu_bar;
	private String TAG = "V5_FRAGMENT_FEATURED";
	
	private AdViewPager adViewPager;
	private AdAdapter mAdAdapter;
	private CustomIndView indView;
	private float xDistance, yDistance;
	/** 记录按下的X坐标 **/
	private float mLastMotionX, mLastMotionY;
	/** 是否是左右滑动 **/
	private boolean mIsBeingDragged = true;
	private boolean initDataLock;
	private FixedSpeedScroller scroller=null;
	
	private ImageView cursor;// 动画图片
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private int screenW;
	private int one, two;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new FeaturedAdapter(mContext);
		newThemeAdapter = new FeaturedAdapter(mContext);
		betterThemeAdapter = new FeaturedAdapter(mContext);

		maxItemWidth = LockApplication.getInstance().getConfig().getScreenWidth() / 3;
		maxItemHeight = maxItemWidth * 16 / 9;
		
		openDamao();
	}
	
	private void openDamao() {
		// TODO Auto-generated method stub
		// 启动常驻通知栏
//		DMSDK.startMNTView(mContext);
//		// 启动push通知栏
//		DmLbx.showPushView(mContext);
//		// 显示底部banner条
//		DmLbx.showBomBanner(mContext);
//		// 显示小横条广告
//		DmLbx.showShortBanner(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_featured, container, false);
		viewPager = (NoScrollViewPager) view.findViewById(R.id.viewPager);
		viewPager.setNoScroll(false);
		tab_hot=(TextView) view.findViewById(R.id.tab_hot);
		tab_new=(TextView) view.findViewById(R.id.tab_new);
		tab_better=(TextView) view.findViewById(R.id.tab_better);
//		tab_hot.setOnClickListener(this);
//		tab_new.setOnClickListener(this);
//		tab_better.setOnClickListener(this);
//        tab_hot_indicator = view.findViewById(R.id.tab_hot_indicator);
//        tab_new_indicator = view.findViewById(R.id.tab_new_indicator);
//        tab_better_indicator = view.findViewById(R.id.tab_better_indicator);
		
		layout_tab_hot=(LinearLayout)view.findViewById(R.id.layout_tab_hot);
		layout_tab_new=(LinearLayout)view.findViewById(R.id.layout_tab_new);
		layout_tab_better=(LinearLayout)view.findViewById(R.id.layout_tab_better);
		layout_tab_hot.setOnClickListener(this);
		layout_tab_new.setOnClickListener(this);
		layout_tab_better.setOnClickListener(this);
		
		main_paixu_bar=(LinearLayout)view.findViewById(R.id.main_paixu_bar);
		tab_time=(TextView) view.findViewById(R.id.tab_time);
		tab_praise=(TextView) view.findViewById(R.id.tab_praise);
		tab_time.setOnClickListener(this);
		tab_praise.setOnClickListener(this);
		
		featured_gridview = (HeaderAndFooterGridview) LayoutInflater.from(mContext).inflate(R.layout.theme_gridview_layout, null, false);
		featured_gridview.setTag("featured");
		featured_gridview.setOnItemClickListener(this);
		new_gridview = (HeaderAndFooterGridview) LayoutInflater.from(mContext).inflate(R.layout.theme_gridview_layout, null, false);
		new_gridview.setTag("new");
		new_gridview.setOnItemClickListener(this);
		better_gridview = (HeaderAndFooterGridview) LayoutInflater.from(mContext).inflate(R.layout.theme_gridview_layout, null, false);
		better_gridview.setTag("better");
		better_gridview.setOnItemClickListener(this);
		addHeadView();
		addFootView();

		featured_gridview.setAdapter(adapter);
		new_gridview.setAdapter(newThemeAdapter);
		better_gridview.setAdapter(betterThemeAdapter);

		ArrayList<View> gridViews = new ArrayList<View>();
		gridViews.add(featured_gridview);
		gridViews.add(better_gridview);
		gridViews.add(new_gridview);
		viewPager.setAdapter(new MyPagerAdapter(gridViews));
		viewPager.setCurrentItem(page);
		viewPager.setOnPageChangeListener(viewPagerChangedListener);
		updateTabView();

		if (bannerInfos!=null && bannerInfos.size()>0) {
			mHandler.sendEmptyMessage(MSG_NOTIFY_BANNER_CHANGED);
		}
		if (adapter.getLockThemeInfos().size() == 0) {
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}
		if (newThemeAdapter.getLockThemeInfos().size() == 0) {
			mHandler2.sendEmptyMessage(MSG_REQUEST_URL_JSON_2);
		}
		if (betterThemeAdapter.getLockThemeInfos().size() == 0) {
			mHandler4.sendEmptyMessage(MSG_REQUEST_URL_JSON_4);
		}
		
		cursor = (ImageView) view.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.indicator_blue_small).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels-DensityUtil.dip2px(mContext, 40);// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		one = offset + screenW / 3;
		two = offset + (screenW / 3) * 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置

		return view;
	}

	private void updateTabView() {
		if (page == 0) {
			CustomEventCommit.commitEvent(mContext, TAG, "PAGE", "Better");
			tab_better.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
//			tab_better.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			tab_hot.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_hot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tab_new.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//            tab_better_indicator.setVisibility(View.VISIBLE);
//            tab_hot_indicator.setVisibility(View.INVISIBLE);
//            tab_new_indicator.setVisibility(View.INVISIBLE);
		} else if(page==1){
			CustomEventCommit.commitEvent(mContext, TAG, "PAGE", "Hot");
			tab_better.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_better.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tab_new.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tab_hot.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
//			tab_hot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//            tab_hot_indicator.setVisibility(View.VISIBLE);
//            tab_better_indicator.setVisibility(View.INVISIBLE);
//            tab_new_indicator.setVisibility(View.INVISIBLE);
		}else {
			CustomEventCommit.commitEvent(mContext, TAG, "PAGE", "New");
			tab_better.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_better.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tab_new.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
//			tab_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			tab_hot.setTextColor(mContext.getResources().getColor(R.color.gray));
//			tab_hot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//            tab_hot_indicator.setVisibility(View.INVISIBLE);
//            tab_better_indicator.setVisibility(View.INVISIBLE);
//            tab_new_indicator.setVisibility(View.VISIBLE);
		}
		showPaixu();
	}

	/**
	 * 添加刷新控件
	 */
	private void addFootView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View mFootView = inflater.inflate(R.layout.fragment_foot_refresh_layout, null);
		footer_loading_view = (TwoBallsLoadingView) mFootView.findViewById(R.id.footer_loading_view);
		footer_textview = (TextView) mFootView.findViewById(R.id.footer_textview);
		featured_gridview.addFooterView(mFootView);
		featured_gridview.setShowFootViewListener(this);

		View mFootView2 = inflater.inflate(R.layout.fragment_foot_refresh_layout, null);
		footer_loading_view_2 = (TwoBallsLoadingView) mFootView2.findViewById(R.id.footer_loading_view);
		footer_textview_2 = (TextView) mFootView2.findViewById(R.id.footer_textview);
		new_gridview.addFooterView(mFootView2);
		new_gridview.setShowFootViewListener(this);
		
		View mFootView3 = inflater.inflate(R.layout.fragment_foot_refresh_layout, null);
		footer_loading_view_3 = (TwoBallsLoadingView) mFootView3.findViewById(R.id.footer_loading_view);
		footer_textview_3 = (TextView) mFootView3.findViewById(R.id.footer_textview);
		better_gridview.addFooterView(mFootView3);
		better_gridview.setShowFootViewListener(this);
	}

	/**
	 * 添加HeadView(广告控件)
	 */
	private void addHeadView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
//		View mHeadView = inflater.inflate(R.layout.gridview_item_featured_title, null);
//		bannerImageView = (BannerImageView) mHeadView.findViewById(R.id.gridview_item_featured_banner);
//		bannerImageView.setOnClickListener(this);
//		featured_gridview.addHeaderView(mHeadView);
//
		LinearLayout headViewLayout = (LinearLayout) inflater.inflate(R.layout.head_loading_layout, null);
		loadProgressBar = (ProgressBar) headViewLayout.findViewById(R.id.load_progressbar);
		loadTextView = (TextView) headViewLayout.findViewById(R.id.head_load_textview);
		new_gridview.addHeaderView(headViewLayout);
		new_gridview.setShowHeadViewListener(this);
		
//		LinearLayout headViewLayout1 = (LinearLayout) inflater.inflate(R.layout.head_loading_layout, null);
//		loadProgressBar1 = (ProgressBar) headViewLayout.findViewById(R.id.load_progressbar);
//		loadTextView1 = (TextView) headViewLayout.findViewById(R.id.head_load_textview);
//		better_gridview.addHeaderView(headViewLayout1);
		better_gridview.setShowHeadViewListener(this);
		
		
		View mHeadView = inflater.inflate(R.layout.fragment_head_ad_layout, null);
		adViewPager = (AdViewPager) mHeadView.findViewById(R.id.ad_viewpager);
		//Gavan 关闭广告banner
		adViewPager.setVisibility(View.GONE);
		
		try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            scroller = new FixedSpeedScroller(adViewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(adViewPager, scroller);
            scroller.setmDuration(1000);
        } catch (Exception e) {
//            LogUtils.e(TAG, "", e);
        }
		LayoutParams layoutParams = (LayoutParams) adViewPager.getLayoutParams();
		layoutParams.height = DeviceInfoUtils.getDeviceWidth(mContext) * 13 / 36;
		adViewPager.setLayoutParams(layoutParams);
		
		indView = (CustomIndView) mHeadView.findViewById(R.id.indview);

		adViewPager.setOnPageChangeListener(new AdPageChangeListener());
		adViewPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				adViewPager.getGestureDetector().onTouchEvent(event);
				final float x = event.getRawX();
				final float y = event.getRawY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					xDistance = yDistance = 0f;
					mLastMotionX = x;
					mLastMotionY = y;
				case MotionEvent.ACTION_MOVE:
					scroller.setmDuration(100);
					final float xDiff = Math.abs(x - mLastMotionX);
					final float yDiff = Math.abs(y - mLastMotionY);
					xDistance += xDiff;
					yDistance += yDiff;
					// 左右滑动避免和下拉刷新冲突
					if (xDistance > yDistance || Math.abs(xDistance - yDistance) < 0.00001f) {
						mIsBeingDragged = true;
						mLastMotionX = x;
						mLastMotionY = y;
						((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);
					} else {
						mIsBeingDragged = false;
						((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
					}
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_CANCEL:
					if (mIsBeingDragged) {
						((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		featured_gridview.addHeaderView(mHeadView);
		featured_gridview.setShowHeadViewListener(this);
	}

	Handler refreshAdHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int currentItem = adViewPager.getCurrentItem();
				if (currentItem == mAdAdapter.getCount() - 1) {
					adViewPager.setCurrentItem(0, true);
				} else {
					adViewPager.setCurrentItem(currentItem + 1, true);
				}
				break;
			default:
				break;
			}
		}

	};
	
	public class AdPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			indView.updatePage(position);
            refreshAdHandler.removeMessages(0);
            refreshAdHandler.sendEmptyMessageDelayed(0, 5000);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int position) {

		}
	}
	
	/** 广告控件适配器 **/
	public class AdAdapter extends PagerAdapter {
		public List<View> mListViews;

		public AdAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(mListViews.get(position));
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(mListViews.get(position));
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == (obj);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if ("featured".equals(parent.getTag())) {
			if (adapter.getLockThemeInfos().size() > 0 && id >= 0 && id < adapter.getLockThemeInfos().size()) {
				final LockThemeInfo lockThemeInfo = adapter.getLockThemeInfos().get((int) id);
				Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
				intent.putExtra("THUMBNAIL_URL", lockThemeInfo.getThumbnailUrl());
				intent.putExtra("IMAGE_URL", lockThemeInfo.getImageUrl());
				intent.putExtra("THEME_URL", lockThemeInfo.getThemeUrl());
				intent.putExtra("themeAuthor", lockThemeInfo.getMemo());
				intent.putExtra("themeName", lockThemeInfo.getName());
				intent.putExtra("themeId", lockThemeInfo.getThemeId());
				intent.putExtra("FROM", "FEATURED");
				startActivity(intent);

				// CustomEventCommit.commit(mContext.getApplicationContext(),
				// MainActivity.TAG, "FEATURED_THEME:" + position);
			}
		} else if ("new".equals(parent.getTag())) {
			if (newThemeAdapter.getLockThemeInfos().size() > 0 && id >= 0 && id < newThemeAdapter.getLockThemeInfos().size()) {
				final LockThemeInfo lockThemeInfo = newThemeAdapter.getLockThemeInfos().get((int) id);
				Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
				intent.putExtra("THUMBNAIL_URL", lockThemeInfo.getThumbnailUrl());
				intent.putExtra("IMAGE_URL", lockThemeInfo.getImageUrl());
				intent.putExtra("THEME_URL", lockThemeInfo.getThemeUrl());
				intent.putExtra("themeAuthor", lockThemeInfo.getMemo());
				intent.putExtra("themeName", lockThemeInfo.getName());
				intent.putExtra("themeId", lockThemeInfo.getThemeId());
				intent.putExtra("FROM", "FEATURED");
				startActivity(intent);

				// CustomEventCommit.commit(mContext.getApplicationContext(),
				// MainActivity.TAG, "FEATURED_THEME:" + position);
			}
		}else if ("better".equals(parent.getTag())) {
			if (betterThemeAdapter.getLockThemeInfos().size() > 0 && id >= 0 && id < betterThemeAdapter.getLockThemeInfos().size()) {
				final LockThemeInfo lockThemeInfo = betterThemeAdapter.getLockThemeInfos().get((int) id);
				Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
				intent.putExtra("THUMBNAIL_URL", lockThemeInfo.getThumbnailUrl());
				intent.putExtra("IMAGE_URL", lockThemeInfo.getImageUrl());
				intent.putExtra("THEME_URL", lockThemeInfo.getThemeUrl());
				intent.putExtra("themeAuthor", lockThemeInfo.getMemo());
				intent.putExtra("themeName", lockThemeInfo.getName());
				intent.putExtra("themeId", lockThemeInfo.getThemeId());
				intent.putExtra("FROM", "FEATURED");
				startActivity(intent);

				// CustomEventCommit.commit(mContext.getApplicationContext(),
				// MainActivity.TAG, "FEATURED_THEME:" + position);
			}
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED:
				adapter.notifyDataSetChanged();
				isLock = false;
				break;

			case MSG_REQUEST_URL_JSON:
				requestUrlJson();
				break;
			case JSON_NULL:
				footer_textview.setVisibility(View.VISIBLE);
				footer_textview.setText(R.string.page_end_text);
				footer_loading_view.setVisibility(View.GONE);
				break;

			case MSG_REQUEST_CACHED_JSON:
				requestCachedJson();
				//Gavan 精选的假图片数据
				testParseJson();
				break;

			case MSG_NOTIFY_BANNER_CHANGED:
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				ArrayList<View> views = new ArrayList<View>();
				if(bannerInfos!=null&&bannerInfos.size()>0){
					
					View mHeadView1 = inflater.inflate(R.layout.gridview_item_featured_title, null);
					final BannerImageView bannerImageView1 = (BannerImageView) mHeadView1.findViewById(R.id.gridview_item_featured_banner);
					bannerImageView1.setBackgroundResource(R.drawable.icon_featured_find);
					bannerImageView1.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
//							DMSDK.showNoTitleFullView(mContext);
//							DmLbx.showFullView(mContext,R.drawable.dm_icon_endview_ok, "微信分享", "微信分享成功");
						}
					});
					views.add(mHeadView1);
					
					for(int i=0;i<bannerInfos.size();i++){
						final BannerInfo bannerInfo = bannerInfos.get(i);
						View mHeadView = inflater.inflate(R.layout.gridview_item_featured_title, null);
						final BannerImageView bannerImageView = (BannerImageView) mHeadView.findViewById(R.id.gridview_item_featured_banner);
						VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), bannerImageView, bannerInfo.getImageUrl(),
								R.drawable.wallpaper_banner_default, R.drawable.wallpaper_banner_default, LockApplication.getInstance().getConfig().getScreenWidth(),
								LockApplication.getInstance().getConfig().getScreenWidth() * 13 / 36);
						bannerImageView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								if(bannerInfo.getType()==1){
									if (bannerInfo != null && !"".equals(bannerInfo.getRedirectUrl()) && bannerInfo.getRedirectUrl() != null) {
										Intent intent = new Intent(mContext, WebviewActivity.class);
										intent.putExtra("url", bannerInfo.getRedirectUrl());
										intent.putExtra("title", bannerInfo.getTitle());
										mContext.startActivity(intent);
									}
								}else{
									if(AppManagerUtils.appInstalled(mContext, bannerInfo.getPname())){
										AppManagerUtils.runApp(mContext, bannerInfo.getPname());
									}else{
										if (!DownloadService.downloadUrls.contains(bannerInfo.getRedirectUrl())) {
											Intent intent = new Intent(mContext, DownloadService.class);
											intent.putExtra("packageName", bannerInfo.getPname());
											intent.putExtra("url", bannerInfo.getRedirectUrl());
											intent.putExtra("name", bannerInfo.getCname());
											mContext.startService(intent);
										} else {
											SimpleToast.makeText(mContext, R.string.app_is_downloading, SimpleToast.LENGTH_SHORT).show();
										}
									}
								}
							}
						});
						views.add(mHeadView);
					}
				}
				
				mAdAdapter = new AdAdapter(views);

				adViewPager.setAdapter(mAdAdapter);
				adViewPager.setCurrentItem(0);

				
				indView.create();
				indView.setMaxPageCount(mAdAdapter.getCount());
				indView.setNowPage(adViewPager.getCurrentItem());
				indView.updatePage(0);
				refreshAdHandler.sendEmptyMessageDelayed(0, 3000);
				
				break;

			default:
				break;
			}
			return false;
		}
	});

	private Handler mHandler2 = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler2.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED_2:
				if (newThemeAdapter.getLockThemeInfos() != null && newThemeAdapter.getLockThemeInfos().size() > 0) {
					maxId = newThemeAdapter.getLockThemeInfos().get(0).getThemeId();
				}
				newThemeAdapter.notifyDataSetChanged();
				isLock2 = false;
				headLock = false;
				break;

			case MSG_REQUEST_URL_JSON_2:
				requestUrlJson2();
				break;
			case JSON_NULL_2:
				loadProgressBar.setVisibility(View.INVISIBLE);
				footer_textview_2.setVisibility(View.VISIBLE);
				footer_textview_2.setText(R.string.page_end_text);
				footer_loading_view_2.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			return false;
		}
	});
	
	private Handler mHandler4 = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler4.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED_4:
				if (betterThemeAdapter.getLockThemeInfos() != null && betterThemeAdapter.getLockThemeInfos().size() > 0) {
					maxId = betterThemeAdapter.getLockThemeInfos().get(0).getThemeId();
				}
				betterThemeAdapter.notifyDataSetChanged();
				isLock3 = false;
				headLock = false;
				break;

			case MSG_REQUEST_URL_JSON_4:
				requestUrlJson4();
				break;
			case JSON_NULL_4:
//				loadProgressBar1.setVisibility(View.INVISIBLE);
				footer_textview_3.setVisibility(View.VISIBLE);
				footer_textview_3.setText(R.string.page_end_text);
				footer_loading_view_3.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			return false;
		}
	});

	private Handler mHandler3 = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler3.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED_3:
				if (newThemeAdapter.getLockThemeInfos() != null && newThemeAdapter.getLockThemeInfos().size() > 0) {
					maxId = newThemeAdapter.getLockThemeInfos().get(0).getThemeId();
				}
				newThemeAdapter.notifyDataSetChanged();
				headLock = false;
				loadProgressBar.setVisibility(View.INVISIBLE);
				loadTextView.setText(msg.obj + "个新锁屏");
				break;

			case MSG_REQUEST_URL_JSON_3:
				requestUrlJson3();
				break;
			case JSON_NULL_3:
				headLock = false;
				loadProgressBar.setVisibility(View.INVISIBLE);
				loadTextView.setText("-小伙伴们都在忙着上传自己作品-");
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void showFootView() {
		if (page == 0) {
			if (footer_loading_view.getVisibility() == View.VISIBLE && !isLock) {
				isLock = true;
				mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
			}
		} else if (page == 1) {
			if (footer_loading_view_3.getVisibility() == View.VISIBLE && !isLock3) {
				isLock3 = true;
				mHandler4.sendEmptyMessage(MSG_REQUEST_URL_JSON_4);
			}
		}else {
			if (footer_loading_view_2.getVisibility() == View.VISIBLE && !isLock2) {
				isLock2 = true;
				mHandler2.sendEmptyMessage(MSG_REQUEST_URL_JSON_2);
			}
		}
	}

	@Override
	public void showHeadView() {
		if((page==0 && featured_gridview.getFirstVisiblePosition()==0) || (page==1 && better_gridview.getFirstVisiblePosition()==0)){
			if(main_paixu_bar.getVisibility()==View.GONE){
				open();
				if(isOpen){
					main_paixu_bar.setVisibility(View.VISIBLE);
					isOpen=false;
				}
			}else{
				close();
			}
		}else{
			if(main_paixu_bar.getVisibility()==View.VISIBLE){
				close();
				tab_time.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
				tab_praise.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
			}
		}
		
		if (loadProgressBar.getVisibility() != View.VISIBLE && !headLock) {
			loadProgressBar.setVisibility(View.VISIBLE);
			loadTextView.setText("摩擦摩擦...");
			headLock = true;
			mHandler3.sendEmptyMessage(MSG_REQUEST_URL_JSON_3);
		}
	}
	private boolean isClose;
	private boolean isOpen;

	public void open() {
		final LayoutParams layoutParams = (LayoutParams) main_paixu_bar
				.getLayoutParams();
		final int result_layout_height_px = DeviceInfoUtils
				.getDeviceWidth(mContext) * 4 / 36;
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 1; i < 15; i++) {
					final int f = i;
					layoutParams.height = (int) (result_layout_height_px * (1.0f * (f / 14f)));
					Message message = new Message();
					message.obj = layoutParams;
					message.what = 1111;
					handler1.sendMessage(message);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				isOpen = true;
			}
		}).start();
	}
	
	public void close() {
		final LayoutParams layoutParams = (LayoutParams) main_paixu_bar
				.getLayoutParams();
		final int result_layout_height_px = DeviceInfoUtils
				.getDeviceWidth(mContext) * 4 / 36;
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 1; i < 15; i++) {
					final int f = i;
					layoutParams.height = (int) (result_layout_height_px * (1 - 1.0f * (f / 14f)));
					Message message = new Message();
					message.obj = layoutParams;
					message.what = 2222;
					message.arg1 = f;
					handler1.sendMessage(message);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	Handler handler1=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1111:
				LayoutParams params=(LayoutParams) msg.obj;
				main_paixu_bar.setLayoutParams(params);
				main_paixu_bar.invalidate();
				break;
			case 2222:
				LayoutParams params1=(LayoutParams) msg.obj;
				int arg=msg.arg1;
				main_paixu_bar.setLayoutParams(params1);
				main_paixu_bar.invalidate();
				if (arg == 14) {
					main_paixu_bar.setVisibility(View.GONE);
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	private void showPaixu(){
		if(page == 0){
			pageColor0();
		}else if(page == 1){
			pageColor1();
		}
			if(main_paixu_bar.getVisibility()==View.VISIBLE){
				main_paixu_bar.setVisibility(View.GONE);
			}
	}

	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
			jsonObject.put("type", tab_better_num);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			jsonObject.put("begin_num", adapter.getLockThemeInfos().size() + 1);
			jsonObject.put("end_num", PAGE_SIZE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETTHEME + "?json=" + jsonObject.toString());
		RLog.i("FEATURED_URL", url);
		return url;
	}

	private String getRequestUrl2() {
		JSONObject jsonObject = new JSONObject();
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			jsonObject.put("begin_num", newThemeAdapter.getLockThemeInfos().size() + 1);
			jsonObject.put("end_num", PAGE_SIZE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETNEWTHEME + "?json=" + jsonObject.toString() /*+ "&time=" + System.currentTimeMillis()*/);
		RLog.i("NEW_URL", url);
		return url;
	}

	private String getRequestUrl3() {
		JSONObject jsonObject = new JSONObject();
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("id", maxId);
			jsonObject.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETNEWTHEME2 + "?json=" + jsonObject.toString() /*+ "&time=" + System.currentTimeMillis()*/);
		RLog.i("NEW_URL", url);
		return url;
	}
	
	private String getRequestUrl4() {
		JSONObject jsonObject = new JSONObject();
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
			jsonObject.put("type", tab_hot_num);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			jsonObject.put("begin_num", newThemeAdapter.getLockThemeInfos().size() + 1);
			jsonObject.put("end_num", PAGE_SIZE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETNEWTHEME3 + "?json=" + jsonObject.toString() /*+ "&time=" + System.currentTimeMillis()*/);
		RLog.i("NEW_URL", url);
		return url;
	}

	private void requestCachedJson() {
		String url = getRequestUrl();
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
				return;
			}
		}
		mHandler.sendEmptyMessage(JSON_NULL);
	}

	private void requestUrlJson() {
		String url = getRequestUrl();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.i("JSON", response.toString());
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler.sendEmptyMessage(MSG_REQUEST_CACHED_JSON);
					if (MConstants.hosts[0].equals(LockApplication.getInstance().getConfig().getHost())) {
						LockApplication.getInstance().getConfig().setHost(MConstants.hosts[0]);
					} else {
						LockApplication.getInstance().getConfig().setHost(MConstants.hosts[0]);
					}
				}
			});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private void requestUrlJson2() {
		String url = getRequestUrl2();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.i("JSON", response.toString());
					parseJson2(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler2.sendEmptyMessage(JSON_NULL_2);
				}
			});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private void requestUrlJson3() {
		String url = getRequestUrl3();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.i("JSON", response.toString());
					parseJson3(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler3.sendEmptyMessage(JSON_NULL_3);
				}
			});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}
	
	private void requestUrlJson4() {
		String url = getRequestUrl4();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.i("JSON", response.toString());
					parseJson4(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mHandler4.sendEmptyMessage(JSON_NULL_4);
				}
			});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {

			if (jsonObject.has("banners")) {
				if(bannerInfos!=null && bannerInfos.size()>0){
					
				}else{
					if(bannerInfos==null){
						bannerInfos=new ArrayList<BannerInfo>();
					}
					JSONArray banners = jsonObject.optJSONArray("banners");
					if(banners!=null && banners.length()>0){
						for(int i=0;i<banners.length();i++){
							JSONObject banner = banners.optJSONObject(i);
							if (banner != null) {
								BannerInfo bannerInfo = new BannerInfo();
								bannerInfo.setTitle(banner.optString("title"));
								bannerInfo.setImageUrl(banner.optString("image"));
								bannerInfo.setRedirectUrl(banner.optString("url"));
								bannerInfo.setCname(banner.optString("cname"));
								bannerInfo.setPname(banner.optString("pname"));
								bannerInfo.setType(banner.optInt("type"));
								//saveBannerInfo();
								bannerInfos.add(bannerInfo);
							}
						}
					}
					mHandler.sendEmptyMessage(MSG_NOTIFY_BANNER_CHANGED);
				}
			}

			JSONArray array = jsonObject.optJSONArray("json");
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					LockThemeInfo lockThemeInfo = new LockThemeInfo();
					lockThemeInfo.setName(js.optString("uploadname"));
					lockThemeInfo.setMemo(js.optString("uploadmemo"));
					lockThemeInfo.setThemeUrl(js.optString("fileurlzip"));
					lockThemeInfo.setImageUrl(js.optString("fileurlimg"));
					lockThemeInfo.setThumbnailUrl(js.optString("fileurlabbr"));
					lockThemeInfo.setUploadTime(js.optString("ctime"));
					lockThemeInfo.setUploadUser(js.optInt("upload_status"));
					lockThemeInfo.setVersionCode(js.optInt("vertion_code"));
					lockThemeInfo.setPraise(js.optInt("praise"));
					lockThemeInfo.setThemeId(js.optInt("id"));

					adapter.getLockThemeInfos().add(lockThemeInfo);
				}

				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
			} else {
				mHandler.sendEmptyMessage(JSON_NULL);
			}
		}
	}

	private void parseJson2(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {

			JSONArray array = jsonObject.optJSONArray("json");
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					LockThemeInfo lockThemeInfo = new LockThemeInfo();
					lockThemeInfo.setName(js.optString("uploadname"));
					lockThemeInfo.setMemo(js.optString("uploadmemo"));
					lockThemeInfo.setThemeUrl(js.optString("fileurlzip"));
					lockThemeInfo.setImageUrl(js.optString("fileurlimg"));
					lockThemeInfo.setThumbnailUrl(js.optString("fileurlabbr"));
					lockThemeInfo.setUploadTime(js.optString("ctime"));
					lockThemeInfo.setUploadUser(js.optInt("upload_status"));
					lockThemeInfo.setVersionCode(js.optInt("vertion_code"));
					lockThemeInfo.setPraise(js.optInt("praise"));
					lockThemeInfo.setThemeId(js.optInt("id"));

					newThemeAdapter.getLockThemeInfos().add(lockThemeInfo);
				}

				mHandler2.sendEmptyMessage(MSG_NOTIFY_CHANGED_2);
			} else {
				mHandler2.sendEmptyMessage(JSON_NULL_2);
			}
		}
	}

	private void parseJson3(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {

			JSONArray array = jsonObject.optJSONArray("json");
			if (array != null && array.length() > 0) {
				ArrayList<LockThemeInfo> lockThemeInfos = new ArrayList<LockThemeInfo>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					LockThemeInfo lockThemeInfo = new LockThemeInfo();
					lockThemeInfo.setName(js.optString("uploadname"));
					lockThemeInfo.setMemo(js.optString("uploadmemo"));
					lockThemeInfo.setThemeUrl(js.optString("fileurlzip"));
					lockThemeInfo.setImageUrl(js.optString("fileurlimg"));
					lockThemeInfo.setThumbnailUrl(js.optString("fileurlabbr"));
					lockThemeInfo.setUploadTime(js.optString("ctime"));
					lockThemeInfo.setUploadUser(js.optInt("upload_status"));
					lockThemeInfo.setVersionCode(js.optInt("vertion_code"));
					lockThemeInfo.setPraise(js.optInt("praise"));
					lockThemeInfo.setThemeId(js.optInt("id"));
					lockThemeInfos.add(lockThemeInfo);
				}
				newThemeAdapter.getLockThemeInfos().addAll(0, lockThemeInfos);
				if (lockThemeInfos.size() > 0) {
					Message message = new Message();
					message.what = MSG_NOTIFY_CHANGED_3;
					message.obj = lockThemeInfos.size();
					mHandler3.sendMessage(message);
				} else {
					mHandler3.sendEmptyMessage(JSON_NULL_3);
				}
			} else {
				mHandler3.sendEmptyMessage(JSON_NULL_3);
			}
		}
	}
	
	private void parseJson4(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {

			JSONArray array = jsonObject.optJSONArray("json");
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					LockThemeInfo lockThemeInfo = new LockThemeInfo();
					lockThemeInfo.setName(js.optString("uploadname"));
					lockThemeInfo.setMemo(js.optString("uploadmemo"));
					lockThemeInfo.setThemeUrl(js.optString("fileurlzip"));
					lockThemeInfo.setImageUrl(js.optString("fileurlimg"));
					lockThemeInfo.setThumbnailUrl(js.optString("fileurlabbr"));
					lockThemeInfo.setUploadTime(js.optString("ctime"));
					lockThemeInfo.setUploadUser(js.optInt("upload_status"));
					lockThemeInfo.setVersionCode(js.optInt("vertion_code"));
					lockThemeInfo.setPraise(js.optInt("praise"));
					lockThemeInfo.setThemeId(js.optInt("id"));

					betterThemeAdapter.getLockThemeInfos().add(lockThemeInfo);
				}

				mHandler4.sendEmptyMessage(MSG_NOTIFY_CHANGED_4);
			} else {
				mHandler4.sendEmptyMessage(JSON_NULL_4);
			}
		}
	}

//	private void saveBannerInfo() {
//		if (bannerInfo != null) {
//			SharedPreferences sharedPreferences = null;
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_MULTI_PROCESS);
//			} else {
//				sharedPreferences = mContext.getSharedPreferences("huodong.cfg", Context.MODE_PRIVATE);
//			}
//
//			Editor editor = sharedPreferences.edit();
//			editor.putString("huodong_title", bannerInfo.getTitle());
//			editor.putString("huodong_url", bannerInfo.getRedirectUrl());
//			editor.apply();
//		}
//	}

	private boolean pageFlog0=true,pageFlog1=true;
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.gridview_item_featured_banner) {//			if (bannerInfo != null && bannerInfo.getRedirectUrl() != null) {
//				Intent intent = new Intent(mContext, WebviewActivity.class);
//				intent.putExtra("url", bannerInfo.getRedirectUrl());
//				intent.putExtra("title", bannerInfo.getTitle());
//				mContext.startActivity(intent);
//			}

		} else if (i == R.id.layout_tab_better) {
			page = 0;
			viewPager.setCurrentItem(0, false);
//			featured_gridview.setSelection(0);
			scrollToListviewTop(featured_gridview);
			showPaixu();

		} else if (i == R.id.layout_tab_hot) {
			page = 1;
			viewPager.setCurrentItem(1, false);
//			better_gridview.setSelection(0);
			scrollToListviewTop(better_gridview);
			showPaixu();

		} else if (i == R.id.layout_tab_new) {
			page = 2;
			viewPager.setCurrentItem(2, false);
//			new_gridview.setSelection(0);
			scrollToListviewTop(new_gridview);
			showPaixu();

		} else if (i == R.id.tab_time) {
			if (page == 0) {
				tab_better_num = 1;
				bannerInfos = null;
				adapter.getLockThemeInfos().clear();
				mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
				pageFlog0 = true;
			} else {
				tab_hot_num = 1;
				betterThemeAdapter.getLockThemeInfos().clear();
				mHandler4.sendEmptyMessage(MSG_REQUEST_URL_JSON_4);
				pageFlog1 = true;
			}
			tab_time.setTextColor(mContext.getResources().getColor(R.color.red));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));

		} else if (i == R.id.tab_praise) {
			if (page == 0) {
				tab_better_num = 2;
				bannerInfos = null;
				adapter.getLockThemeInfos().clear();
				mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
				pageFlog0 = false;
			} else {
				tab_hot_num = 2;
				betterThemeAdapter.getLockThemeInfos().clear();
				mHandler4.sendEmptyMessage(MSG_REQUEST_URL_JSON_4);
				pageFlog1 = false;
			}
			tab_time.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.red));

		} else {
		}
	}
	
	public void pageColor0(){
		if(pageFlog0){
			tab_time.setTextColor(mContext.getResources().getColor(R.color.red));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
		}else{
			tab_time.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.red));
		}
	}
	
	public void pageColor1(){
		if(pageFlog1){
			tab_time.setTextColor(mContext.getResources().getColor(R.color.red));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
		}else{
			tab_time.setTextColor(mContext.getResources().getColor(R.color.main_paixu_bar_text_color));
			tab_praise.setTextColor(mContext.getResources().getColor(R.color.red));
		}
	}
	
	/**
	 * listview回滚至顶部
	 * @param listView
	 */
	public static void scrollToListviewTop(final AbsListView listView)
    {
        listView.smoothScrollToPosition(0);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (listView.getFirstVisiblePosition() > 0)
                {
                    listView.smoothScrollToPosition(0);
                    handler.postDelayed(this, 100);
                    //listView.setSelection(0);
                }
            }
        }, 100);
    }

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(mListViews.get(position));
		}

		@Override
		public void finishUpdate(View view) {

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == (obj);
		}

		@Override
		public void restoreState(Parcelable view, ClassLoader classLoader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View view) {
		}
	}

	private ViewPager.SimpleOnPageChangeListener viewPagerChangedListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			super.onPageScrolled(position, positionOffset, positionOffsetPixels);

			Matrix matrix = new Matrix();
			switch (position) {
			case 0:
				matrix.postTranslate(offset + (one - offset) * positionOffset, 0);
				break;
			case 1:
				matrix.postTranslate(one + (two - one) * positionOffset, 0);
				break;
			case 2:
				matrix.postTranslate(two + (two - one) * positionOffset, 0);
				break;

			default:
				break;
			}
			cursor.setImageMatrix(matrix);
            // TODO: 15/7/7  调试看下两个参数，利用参数进行判断，屏幕左右移动
            RLog.d("onPageScrolled", "positionOffset=" + positionOffset + ", positionOffsetPixels=" + positionOffsetPixels);
		}

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			page = position;
			updateTabView();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			super.onPageScrollStateChanged(state);
        }
	};

	private class BannerInfo {
		private String title;
		private String imageUrl;
		private String redirectUrl;
		private String cname;
		private String pname;
		public String getCname() {
			return cname;
		}

		public void setCname(String cname) {
			this.cname = cname;
		}

		public String getPname() {
			return pname;
		}

		public void setPname(String pname) {
			this.pname = pname;
		}

		private int type;
		
		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}
	}

	private class FeaturedAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private ArrayList<LockThemeInfo> lockThemeInfos;

		public FeaturedAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			lockThemeInfos = new ArrayList<LockThemeInfo>();
		}

		@Override
		public int getCount() {
			return lockThemeInfos.size() == 0 ? 9 : lockThemeInfos.size();
		}

		@Override
		public Object getItem(int position) {
			if (lockThemeInfos.size() == 0) {
				return null;
			}
			return lockThemeInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.gridview_item_featured, parent, false);
				mHolder.thumbnail = (ImageView) convertView.findViewById(R.id.gridview_item_featrued_thumbnail);
				mHolder.favorite = (TextView) convertView.findViewById(R.id.gridview_item_featrued_favorite);
				mHolder.feature_favorite_linearLayout = convertView.findViewById(R.id.feature_favorite_linearLayout);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			if (mHolder != null) {
				mHolder.thumbnail.setBackgroundResource(R.drawable.wallpaper_thumbnail_default);
				if (lockThemeInfos.size() > 0) {
					final LockThemeInfo lockThemeInfo = lockThemeInfos.get(position);

					
//					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), mHolder.thumbnail, lockThemeInfo.getThumbnailUrl(),
//							R.drawable.wallpaper_thumbnail_default, R.drawable.wallpaper_thumbnail_default, maxItemWidth, maxItemHeight);
					//Gavan
					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), mHolder.thumbnail, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524653902640&di=2e62cfee609b682077bc78a4e9a8d4f6&imgtype=0&src=http://attachments.gfan.com/forum/201412/10/202153ruf5qxqbqc25vveq.jpg",
							R.drawable.wallpaper_thumbnail_default, R.drawable.wallpaper_thumbnail_default, maxItemWidth, maxItemHeight);
					
					mHolder.favorite.setText(lockThemeInfo.getPraise() + "");
					mHolder.feature_favorite_linearLayout.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}

		public ArrayList<LockThemeInfo> getLockThemeInfos() {
			return lockThemeInfos;
		}

		private class ViewHolder {
			private ImageView thumbnail;
			private TextView favorite;
			private View feature_favorite_linearLayout;
		}
	}

	//Gavan 测试图片主题
	private void testParseJson(){
//		JSONObject js = array.optJSONObject(i);
		LockThemeInfo lockThemeInfo = new LockThemeInfo();
		lockThemeInfo.setName("a");
		lockThemeInfo.setMemo("a");
		lockThemeInfo.setThemeUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524653902640&di=2e62cfee609b682077bc78a4e9a8d4f6&imgtype=0&src=http://attachments.gfan.com/forum/201412/10/202153ruf5qxqbqc25vveq.jpg");
		lockThemeInfo.setImageUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524653902640&di=2e62cfee609b682077bc78a4e9a8d4f6&imgtype=0&src=http://attachments.gfan.com/forum/201412/10/202153ruf5qxqbqc25vveq.jpg");
		lockThemeInfo.setThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524653902640&di=2e62cfee609b682077bc78a4e9a8d4f6&imgtype=0&src=http://attachments.gfan.com/forum/201412/10/202153ruf5qxqbqc25vveq.jpg");
		lockThemeInfo.setUploadTime("a");
		lockThemeInfo.setUploadUser(1);
		lockThemeInfo.setVersionCode(1);
		lockThemeInfo.setPraise(1);
		lockThemeInfo.setThemeId(1);

		adapter.getLockThemeInfos().add(lockThemeInfo);

	mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
	}
	
	
}
