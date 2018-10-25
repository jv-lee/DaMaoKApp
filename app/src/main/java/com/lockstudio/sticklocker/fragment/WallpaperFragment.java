package com.lockstudio.sticklocker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.activity.MainActivity;
import com.lockstudio.sticklocker.activity.WallpaperListActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseFragment;
import com.lockstudio.sticklocker.model.WallpaperItemInfo;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class WallpaperFragment extends BaseFragment {
	private WallPaperAdapter adapter;

	private final int MSG_REQUEST_URL_JSON = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new WallPaperAdapter(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wallpaper, container,
				false);
		GridView wallpaper_gridview = (GridView) view
				.findViewById(R.id.wallpaper_gridview);
		wallpaper_gridview.setAdapter(adapter);
		if (adapter.getItemInfoArrayList().size() == 0) {
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}
		return view;
	}

	private void requestCachedJson() {
		String url = getRequestUrl();
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(
					url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
			}
		}
	}

	private void requestUrlJson() {
		String url = getRequestUrl();
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
					null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
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
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width", LockApplication.getInstance()
					.getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance()
					.getConfig().getScreenHeight());
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETWALLPAPER_ZONE
				+ "?json=" + jsonObject.toString());
		RLog.i("WALLPAPER_FEATURED_URL", url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject != null && jsonObject.optInt("code") == 200) {
			if (jsonObject.has("json")) {
				adapter.getItemInfoArrayList().clear();
				RLog.d("AAAAA", jsonObject.toString());
				JSONArray array = jsonObject.optJSONArray("json");
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					WallpaperItemInfo itemInfo = new WallpaperItemInfo();
					itemInfo.setId(js.optInt("id"));
					itemInfo.setTitle(js.optString("title"));
					itemInfo.setDesc(js.optString("explain"));
					itemInfo.setImageUrl(js.optString("image"));

					adapter.getItemInfoArrayList().add(itemInfo);
				}
				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
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
				break;

			case MSG_REQUEST_URL_JSON:
//				requestUrlJson();
				//Gavan测试图片
				testParseJson();
				break;

			case MSG_REQUEST_CACHED_JSON:
//				requestCachedJson();
				break;

			default:
				break;
			}
			return false;
		}
	});

	public class WallPaperAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private Context mContext;
		private ArrayList<WallpaperItemInfo> itemInfoArrayList;

		public WallPaperAdapter(Context context) {
			this.mContext = context;
			layoutInflater = LayoutInflater.from(context);
			itemInfoArrayList = new ArrayList<WallpaperItemInfo>();
		}

		@Override
		public int getCount() {
			return itemInfoArrayList.size() == 0 ? 8 : itemInfoArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			if (itemInfoArrayList.size() == 0)
				return null;
			return itemInfoArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			if (position == 0)
				return 0;
			else
				return position - 1;
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
						R.layout.gridview_item_wallpaper, parent, false);
				holder.gridview_item_wallpaper_imageview = (ImageView) convertView
						.findViewById(R.id.gridview_item_wallpaper_imageview);
				holder.gridview_item_wallpaper_textview = (TextView) convertView
						.findViewById(R.id.gridview_item_wallpaper_textview);
				holder.gridview_item_wallpaper_layout = (LinearLayout) convertView
						.findViewById(R.id.gridview_item_wallpaper_layout);
				holder.zhanwei_view = convertView
						.findViewById(R.id.gridview_item_zhanwei_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (itemInfoArrayList.size() != 0) {
				final WallpaperItemInfo itemInfo = (WallpaperItemInfo) getItem(position);
				holder.gridview_item_wallpaper_textview.setText(itemInfo
						.getTitle());
				VolleyUtil.instance().setUrlImage(
						VolleyUtil.instance().getRequestQueue(),
						holder.gridview_item_wallpaper_imageview,
						itemInfo.getImageUrl(),
						R.drawable.wallpaper_group_default,
						R.drawable.wallpaper_group_default);

				holder.gridview_item_wallpaper_layout
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent more_intent = new Intent(mContext,
										WallpaperListActivity.class);
								more_intent.putExtra("id", itemInfo.getId());
								more_intent.putExtra("title",
										itemInfo.getTitle());
								mContext.startActivity(more_intent);
								CustomEventCommit.commit(
										mContext.getApplicationContext(),
										MainActivity.TAG, "WALLPAPER_MORE:"
												+ itemInfo.getTitle());
							}
						});

				int count = getCount();
				if (count % 2 == 0) {
					if (position == count - 1 || position == count - 2) {
						holder.zhanwei_view.setVisibility(View.VISIBLE);
					} else {
						holder.zhanwei_view.setVisibility(View.GONE);
					}
				} else {
					if (position == count - 1) {
						holder.zhanwei_view.setVisibility(View.VISIBLE);
					} else {
						holder.zhanwei_view.setVisibility(View.GONE);
					}
				}
			}

			return convertView;
		}

		class ViewHolder {
			ImageView gridview_item_wallpaper_imageview;
			TextView gridview_item_wallpaper_textview;
			LinearLayout gridview_item_wallpaper_layout;
			View zhanwei_view;
		}

		public ArrayList<WallpaperItemInfo> getItemInfoArrayList() {
			return itemInfoArrayList;
		}
	}

	// Gavan 测试图片主题
	private void testParseJson() {

		adapter.getItemInfoArrayList().clear();
//		for (int i = 0; i < 6; i++) {
//			WallpaperItemInfo itemInfo = new WallpaperItemInfo();
//			itemInfo.setId(1);
//			itemInfo.setTitle("title");
//			itemInfo.setDesc("explain");
//			itemInfo.setImageUrl(testURL);
//
//			adapter.getItemInfoArrayList().add(itemInfo);
//		}
		
		try {
            InputStreamReader isr = new InputStreamReader(mContext.getAssets().open("bizhi.json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject testjson = new JSONObject(builder.toString());//builder读取了JSON中的数据。
                                                                     //直接传入JSONObject来构造一个实例
            JSONArray array = testjson.optJSONArray("json");
			for (int i = 0; i < array.length(); i++) {
				JSONObject js = array.optJSONObject(i);
				WallpaperItemInfo itemInfo = new WallpaperItemInfo();
				itemInfo.setId(js.optInt("id"));
				itemInfo.setTitle(js.optString("title"));
				itemInfo.setDesc(js.optString("explain"));
				itemInfo.setImageUrl(js.optString("image"));

				adapter.getItemInfoArrayList().add(itemInfo);
			}
			mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

	private String testURL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524632909025&di=79fe6627d5cc9aa2c5d801aab12521dc&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D991737270%2C1027779279%26fm%3D214%26gp%3D0.jpg";

}
