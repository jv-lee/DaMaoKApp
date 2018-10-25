package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.WallpaperImageInfo;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.util.WallpaperUtils;
import com.lockstudio.sticklocker.view.HeaderAndFooterGridview;
import com.lockstudio.sticklocker.view.TwoBallsLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class WallpaperListActivity extends BaseActivity implements
		OnItemClickListener, HeaderAndFooterGridview.ShowFootViewListener {

	private final String TAG = "V5_WALLPAPER_LIST_ACTIVITY";

	private WallpaperUtils.Resolution resolution;

	private WallpaperListAdapter adapter;
	private TwoBallsLoadingView footer_loading_view;

	private TextView footer_textview;
	private final int MSG_REQUEST_URL_JSON = 100;

	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;
	public static final int PAGE_SIZE = 18;

	private boolean from_diy;
	private String title;
	private int id;
	// Gavan 控制显示，不让它无限循环
	private boolean isLock = true;

	private int maxThumbnailWidth, maxThumbnailHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallpaper_list);

		id = getIntent().getIntExtra("id", 1);
		title = getIntent().getStringExtra("title");
		resolution = new WallpaperUtils(this).getResolution();
		from_diy = getIntent().getBooleanExtra("from_diy", false);
		adapter = new WallpaperListAdapter(this);
		HeaderAndFooterGridview gridView = (HeaderAndFooterGridview) findViewById(R.id.wallpaper_detail_gridview);

		LayoutInflater inflater = LayoutInflater.from(this);
		View mFootView = inflater.inflate(
				R.layout.fragment_foot_refresh_layout, null);
		footer_loading_view = (TwoBallsLoadingView) mFootView
				.findViewById(R.id.footer_loading_view);
		footer_textview = (TextView) mFootView
				.findViewById(R.id.footer_textview);
		gridView.addFooterView(mFootView);
		// Gavan 控制显示，不让它无限循环
		// gridView.setShowFootViewListener(this);
		showend();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);

		TextView textView = (TextView) findViewById(R.id.text_view_title);
		textView.setText(title);
		findViewById(R.id.text_view_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		maxThumbnailWidth = LockApplication.getInstance().getConfig()
				.getScreenWidth() / 3;
		maxThumbnailHeight = maxThumbnailWidth * 16 / 9;

		mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
	}

	private void requestCachedJson() {
		String url = getRequestUrl();
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(
					url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
				return;
			}
		}
		footer_loading_view.setVisibility(View.GONE);
		footer_textview.setVisibility(View.VISIBLE);
		footer_textview.setText(R.string.page_end_text);
	}

	private void requestUrlJson() {
		String url = getRequestUrl();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
					null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							RLog.d("WALLPAPER_LIST", response.toString());
							parseJson(response);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							mHandler.sendEmptyMessage(MSG_REQUEST_CACHED_JSON);
						}
					});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", id);
			jsonObject.put("begin_num", adapter.getArrayList().size() + 1);
			jsonObject.put("end_num", PAGE_SIZE);
			jsonObject.put("screen_width", LockApplication.getInstance()
					.getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance()
					.getConfig().getScreenHeight());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETWALLPAPER + "?json="
				+ jsonObject.toString());
		RLog.i(TAG, url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray array = jsonObject.optJSONArray("json");
			if (null != array && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					WallpaperImageInfo imageInfo = new WallpaperImageInfo();
					JSONObject js = array.optJSONObject(i);
					if (resolution == WallpaperUtils.Resolution.big) {
						imageInfo.setThumbnailUrl(js.optString("abbr_png"));
						imageInfo.setImageUrl(js.optString("image_png"));
					} else if (resolution == WallpaperUtils.Resolution.normal) {
						imageInfo.setThumbnailUrl(js.optString("abbr_720"));
						imageInfo.setImageUrl(js.optString("image_720"));
					} else {
						imageInfo.setThumbnailUrl(js.optString("abbr_480"));
						imageInfo.setImageUrl(js.optString("image_480"));
					}

					adapter.getArrayList().add(imageInfo);
				}
				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
			} else {
				footer_loading_view.setVisibility(View.GONE);
				footer_textview.setVisibility(View.VISIBLE);
				footer_textview.setText(R.string.page_end_text);
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
				// Gavan 控制显示，不让它无限循环
				// isLock = false;
				break;

			case MSG_REQUEST_URL_JSON:
				// requestUrlJson();
				// Gavan测试本地图片列表
				testParseJson();
				break;

			case MSG_REQUEST_CACHED_JSON:
				// requestCachedJson();
				// showend();
				break;

			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final WallpaperImageInfo imageInfo = (WallpaperImageInfo) parent
				.getItemAtPosition(position);
		if (imageInfo != null) {
			if (from_diy) {
				Intent data = new Intent();
				data.putExtra("THUMBNAIL_URL", imageInfo.getThumbnailUrl());
				data.putExtra("IMAGE_URL", imageInfo.getImageUrl());
				setResult(MConstants.RESULT_CODE_WALLPAPER_LIST, data);
				finish();
			} else {
				Intent intent = new Intent(mContext,
						WallpaperPreviewActivity.class);
				intent.putExtra("thumbnail_url", imageInfo.getThumbnailUrl());
				intent.putExtra("image_url", imageInfo.getImageUrl());
				mContext.startActivity(intent);
			}
			// CustomEventCommit.commit(mContext.getApplicationContext(), TAG,
			// title);
		}
	}

	@Override
	public void showFootView() {
		if (footer_loading_view.getVisibility() == View.VISIBLE && !isLock) {
			isLock = true;
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}
	}

	public class WallpaperListAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private ArrayList<WallpaperImageInfo> arrayList;

		public WallpaperListAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			arrayList = new ArrayList<WallpaperImageInfo>();
		}

		public ArrayList<WallpaperImageInfo> getArrayList() {
			return arrayList;
		}

		@Override
		public int getCount() {
			return arrayList.size() == 0 ? 12 : arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			if (arrayList.size() == 0) {
				return null;
			}
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			if (position == 0)
				return 0;
			else
				return position - 1;
		}

		@Override
		public int getItemViewType(int position) {
			return position % 7 == 0 ? 0 : 1;

		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.gridview_item_paper_detail, parent, false);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.gridview_paper_detail_iv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (holder != null) {
				holder.imageView
						.setBackgroundResource(R.drawable.wallpaper_thumbnail_default);

				if (arrayList.size() > 0) {
					WallpaperImageInfo imageInfo = (WallpaperImageInfo) getItem(position);

					VolleyUtil.instance().setUrlImage(
							VolleyUtil.instance().getRequestQueue(),
							holder.imageView, imageInfo.getThumbnailUrl(),
							R.drawable.wallpaper_thumbnail_default,
							R.drawable.wallpaper_thumbnail_default,
							maxThumbnailWidth, maxThumbnailHeight);
				}

			}
			return convertView;
		}

		public class ViewHolder {
			public ImageView imageView;
		}

	}

	// Gavan 测试壁纸中的列表图片
	private void testParseJson() {
		// for (int i = 0; i < 6; i++) {
		// WallpaperImageInfo imageInfo = new WallpaperImageInfo();
		// if (resolution == WallpaperUtils.Resolution.big) {
		// imageInfo.setThumbnailUrl(test_thumbnailurl);
		// imageInfo.setImageUrl(test_thumbnailurl);
		// } else if (resolution == WallpaperUtils.Resolution.normal) {
		// imageInfo.setThumbnailUrl(test_thumbnailurl);
		// imageInfo.setImageUrl(test_thumbnailurl);
		// } else {
		// imageInfo.setThumbnailUrl(test_thumbnailurl);
		// imageInfo.setImageUrl(test_thumbnailurl);
		// }
		//
		// adapter.getArrayList().add(imageInfo);
		// }
		// mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);

		String tag = "tag" + id;

		try {
			InputStreamReader isr = new InputStreamReader(mContext.getAssets()
					.open("bizhilist.json"), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			isr.close();
			JSONObject testjson = new JSONObject(builder.toString());// builder读取了JSON中的数据。
																		// 直接传入JSONObject来构造一个实例
			JSONArray array = testjson.optJSONArray(tag);
			if (null != array && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					WallpaperImageInfo imageInfo = new WallpaperImageInfo();
					JSONObject js = array.optJSONObject(i);
//					if (resolution == WallpaperUtils.Resolution.big) {
//						imageInfo.setThumbnailUrl(js.optString("image"));
//						imageInfo.setImageUrl(js.optString("image"));
//					} else if (resolution == WallpaperUtils.Resolution.normal) {
//						imageInfo.setThumbnailUrl(js.optString("image"));
//						imageInfo.setImageUrl(js.optString("image"));
//					} else {
//						imageInfo.setThumbnailUrl(js.optString("image"));
//						imageInfo.setImageUrl(js.optString("image"));
//					}
					
					imageInfo.setThumbnailUrl(js.optString("image"));
					imageInfo.setImageUrl(js.optString("image"));

					adapter.getArrayList().add(imageInfo);
				}
				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
			} else {
				footer_loading_view.setVisibility(View.GONE);
				footer_textview.setVisibility(View.VISIBLE);
				footer_textview.setText(R.string.page_end_text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Gavan 控制显示，不让它无限循环
	private void showend() {
		footer_loading_view.setVisibility(View.GONE);
		footer_textview.setVisibility(View.VISIBLE);
		footer_textview.setText(R.string.page_end_text);
	}

	private String test_imageURL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524632909025&di=79fe6627d5cc9aa2c5d801aab12521dc&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D991737270%2C1027779279%26fm%3D214%26gp%3D0.jpg";
	private String test_thumbnailurl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524638552013&di=3d4b04dcff929d78148ff73a17f8fe37&imgtype=0&src=http%3A%2F%2Ffb.topitme.com%2Fb%2F51%2Ffe%2F1130913718a93fe51bl.jpg";
}
