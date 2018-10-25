package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.BoonBean;
import com.lockstudio.sticklocker.util.BannerUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.RLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.opda.android.activity.R;

public class WelfareActivity extends BaseActivity {

	private ListView welfare;
	private BoonListAdapter adapter;

	public static final int PAGE_SIZE = 10;

	private final int MSG_REQUEST_URL_JSON = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;
	private int maxBannerWidth, maxBannerHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welfare);
		welfare = (ListView) findViewById(R.id.welfare_list);
		adapter = new BoonListAdapter(mContext);

		welfare.setAdapter(this.adapter);
		if (this.adapter.getArrayList().size() == 0) {
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}

		BannerUtils.setBannerTitle_String(this, "福利、活动");
	}

	private String getRequestUrl() {
		return HostUtil.getUrl("MasterLockNewToo/welfare?json=1");
	}

	private void requestCachedJson() {
		String url = getRequestUrl();
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
			}
		}
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
				}
			});

			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray array = jsonObject.optJSONArray("json");
			int count = array.length();
			for (int i = 0; i < count; i++) {
				Map<String, BoonBean> map = new HashMap<String, BoonBean>();
				JSONObject js = array.optJSONObject(i);
				BoonBean bannerBean = new BoonBean();
				bannerBean.setTitle(js.optString("tile"));
				bannerBean.setWeb_url(js.optString("url"));
				bannerBean.setImg_url(js.optString("image_url"));
				map.put("BANNER", bannerBean);

				adapter.getArrayList().add(map);
			}

			mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
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
				requestUrlJson();
				break;

			case MSG_REQUEST_CACHED_JSON:
				requestCachedJson();
				break;

			default:
				break;
			}
			return false;
		}
	});

	public class BoonListAdapter extends BaseAdapter implements View.OnClickListener {
		private LayoutInflater layoutInflater;
		private ArrayList<Map<String, BoonBean>> arrayList;
		private Context mContext;

		public BoonListAdapter(Context context) {
			this.mContext = context;
			arrayList = new ArrayList<Map<String, BoonBean>>(1);
			layoutInflater = LayoutInflater.from(context);
		}

		public ArrayList<Map<String, BoonBean>> getArrayList() {
			return arrayList;
		}

		@Override
		public int getCount() {
			return arrayList.size() == 0 ? 1 : arrayList.size();
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.listview_item_boon, parent, false);
				holder.bannerImageView = (ImageView) convertView.findViewById(R.id.image_view_banner);
				holder.titleViewItem0 = (TextView) convertView.findViewById(R.id.text_view_title_item0);
				holder.zhanview_view = convertView.findViewById(R.id.zhanview_view);

				holder.boon_item_layout0 = convertView.findViewById(R.id.boon_item_layout0);
				holder.boon_banner_layout = convertView.findViewById(R.id.boon_banner_layout);
				holder.boon_banner_layout.setOnClickListener(this);
				holder.boon_item_layout0.setOnClickListener(this);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.bannerImageView.setBackgroundResource(R.drawable.wallpaper_banner_default);

			if (arrayList.size() != 0) {
				Map<String, BoonBean> map = (Map<String, BoonBean>) getItem(position);
				if (map.containsKey("BANNER")) {
					BoonBean bannerBean = map.get("BANNER");
					holder.boon_banner_layout.setTag(bannerBean);
					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), holder.bannerImageView, bannerBean.getImg_url(),
							R.drawable.wallpaper_banner_default, R.drawable.wallpaper_banner_default, maxBannerWidth, maxBannerHeight);

					holder.boon_item_layout0.setTag(bannerBean);
					holder.titleViewItem0.setText(bannerBean.getTitle());
				}
				if (position == arrayList.size() - 1) {
					holder.zhanview_view.setVisibility(View.VISIBLE);
				} else {
					holder.zhanview_view.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.boon_banner_layout || i == R.id.boon_item_layout0) {
				BoonBean bean = (BoonBean) v.getTag();
				if (bean != null) {
					Intent intent = new Intent(mContext, WebviewActivity.class);
					intent.putExtra("url", bean.getWeb_url());
					intent.putExtra("title", bean.getTitle());
					mContext.startActivity(intent);
				}

			} else {
			}
		}

		class ViewHolder {
			View boon_banner_layout, boon_item_layout0;
			ImageView bannerImageView;
			TextView titleViewItem0;
			View zhanview_view;
		}
	}

}
