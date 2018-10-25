package com.lockstudio.sticklocker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.OutlineContainer;
import com.lockstudio.sticklocker.activity.LockThemePreviewActivity;
import com.lockstudio.sticklocker.base.BaseFragment;
import com.lockstudio.sticklocker.model.LockThemeInfo;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.view.RadarLoadView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.opda.android.activity.R;

public class FindFragment extends BaseFragment implements OnClickListener {
	private JazzyViewPager mJazzy;
	private ArrayList<View> views = new ArrayList<View>();
	private View view;
	private LinearLayout load_layout;
	private RadarLoadView loadView;
	private boolean init;
	private boolean loadData;
	private RelativeLayout viewpager_layout;
	private TextView viewpager_textview;
	private TextView load_textview;
	private static final long loadTime = 2500;
	private boolean loading = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_find_theme, container, false);
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		if (!init) {
			initViewAndData();
			init = true;
		}
		return view;
	}

	private void initViewAndData() {
		load_layout = (LinearLayout) view.findViewById(R.id.loading_layout);
		viewpager_layout = (RelativeLayout) view.findViewById(R.id.viewpager_layout);
		viewpager_textview = (TextView) view.findViewById(R.id.viewpager_textview);
		load_textview = (TextView) view.findViewById(R.id.load_textview);
		loadView = (RadarLoadView) view.findViewById(R.id.loadview);
		loadView.stopAnim();
		mJazzy = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);

	}

	private long startTime;

	public void loadData(boolean isFindPage) {
		if (!init) {
			return;
		}
		if ((!loadData || isFindPage) && !loading) {
			loading = true;
			loadData = true;
			load_layout.setVisibility(View.VISIBLE);
			load_textview.setVisibility(View.VISIBLE);
			loadView.setVisibility(View.VISIBLE);
			viewpager_layout.setVisibility(View.INVISIBLE);
			viewpager_textview.setVisibility(View.INVISIBLE);
			mJazzy.setVisibility(View.INVISIBLE);

			loadView.startAnim();
			startTime = System.currentTimeMillis();
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(HostUtil.getUrl("MasterLockNew/randfileupload?json=" + System.currentTimeMillis()),
					null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							if (response != null && response.optInt("code") == 200) {
								parseJson(response);
							} else {
								sendHandlerMsg(0);
							}
						}

					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							sendHandlerMsg(0);
						}
					});
			RequestQueue requestQueue = VolleyUtil.instance().getRequestQueue();
			if (requestQueue != null) {
				requestQueue.add(jsonObjectRequest);
			}
		}
	}

	protected void sendHandlerMsg(int i) {
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		if (time < loadTime) {
			mHandler.sendEmptyMessageDelayed(i, loadTime - time);
		} else {
			mHandler.sendEmptyMessage(i);
		}

	}

	private void parseJson(JSONObject response) {
		JSONArray jsonArray = response.optJSONArray("json");
		if (jsonArray != null && jsonArray.length() > 0 && mContext != null) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			views.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.optJSONObject(i);

				if (jsonObject != null) {

					LockThemeInfo lockThemeInfo = new LockThemeInfo();
					lockThemeInfo.setName(jsonObject.optString("uploadname"));
					lockThemeInfo.setMemo(jsonObject.optString("uploadmemo"));
					lockThemeInfo.setThemeUrl(jsonObject.optString("fileurlzip"));
					lockThemeInfo.setImageUrl(jsonObject.optString("fileurlimg"));
					lockThemeInfo.setThumbnailUrl(jsonObject.optString("fileurlabbr"));
					lockThemeInfo.setUploadTime(jsonObject.optString("ctime"));
					lockThemeInfo.setUploadUser(jsonObject.optInt("upload_status"));
					lockThemeInfo.setVersionCode(jsonObject.optInt("vertion_code"));
					lockThemeInfo.setPraise(jsonObject.optInt("praise"));
					lockThemeInfo.setThemeId(jsonObject.optInt("id"));

					View itemView = layoutInflater.inflate(R.layout.viewpager_item_theme, null);
					ImageView imageView = (ImageView) itemView.findViewById(R.id.theme_imageview);
					imageView.setTag(lockThemeInfo);
					imageView.setOnClickListener(this);
					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), imageView, lockThemeInfo.getThumbnailUrl(),
							R.drawable.wallpaper_thumbnail_default, R.drawable.wallpaper_thumbnail_default);
					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), imageView, lockThemeInfo.getImageUrl(),
							R.drawable.wallpaper_thumbnail_default, R.drawable.wallpaper_thumbnail_default);
					views.add(itemView);
				}

				if (i == jsonArray.length() - 1) {
					View itemView2 = layoutInflater.inflate(R.layout.viewpager_item_theme, null);
					views.add(itemView2);
				}

			}
			sendHandlerMsg(1);
		} else {
			sendHandlerMsg(0);
		}

	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				loading = false;
				break;
			case 1:
				loading = false;
				loadView.stopAnim();
				load_layout.setVisibility(View.INVISIBLE);
				load_textview.setVisibility(View.INVISIBLE);
				loadView.setVisibility(View.INVISIBLE);
				viewpager_layout.setVisibility(View.VISIBLE);
				viewpager_textview.setVisibility(View.VISIBLE);
				mJazzy.setVisibility(View.VISIBLE);
				mJazzy.setAdapter(new MyPagerAdapter());
				mJazzy.setCurrentItem(0);
				mJazzy.setOnPageChangeListener(new OnPageChangeListener() {
					private boolean b;

					@Override
					public void onPageSelected(int position) {

					}

					@Override
					public void onPageScrolled(int position, float scale, int offsize) {
						if (position == views.size() - 2 && offsize > 100 && !b) {
							b = true;
							loadData = false;

							ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
									Animation.RELATIVE_TO_SELF, 0.5f);
							scaleAnimation.setDuration(500);
							scaleAnimation.setFillAfter(true);
							load_layout.startAnimation(scaleAnimation);

							loadData(false);
						}
					}

					@Override
					public void onPageScrollStateChanged(int position) {

					}
				});

				ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.75f);
				scaleAnimation.setDuration(600);
				scaleAnimation.setFillAfter(true);
				viewpager_layout.startAnimation(scaleAnimation);
				break;

			default:
				break;
			}

		}

	};

	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = views.get(position);
			container.addView(view, 0);
			mJazzy.setObjectForPosition(view, position);
			return view;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(mJazzy.findViewFromObject(position));
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}
	}

	@Override
	public void onClick(View v) {
		CustomEventCommit.commit(mContext.getApplicationContext(), "V5_FRAGMENT_FIND", "CLICK");
		Object object = v.getTag();
		if (object != null && object instanceof LockThemeInfo) {
			LockThemeInfo lockThemeInfo = (LockThemeInfo) v.getTag();

			if (lockThemeInfo != null) {
				Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
				intent.putExtra("THUMBNAIL_URL", lockThemeInfo.getThumbnailUrl());
				intent.putExtra("IMAGE_URL", lockThemeInfo.getImageUrl());
				intent.putExtra("THEME_URL", lockThemeInfo.getThemeUrl());
				intent.putExtra("themeAuthor", lockThemeInfo.getMemo());
				intent.putExtra("themeName", lockThemeInfo.getName());
				intent.putExtra("themeId", lockThemeInfo.getThemeId());
				intent.putExtra("FROM", "FEATURED");
				startActivity(intent);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadView != null) {
			loadView.stopAnim();
		}
	}

}
