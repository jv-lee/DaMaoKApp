package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.activity.WallpaperListActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.WallpaperGridItemInfo;
import com.lockstudio.sticklocker.view.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.opda.android.activity.R;

/**
 * 选择壁纸的工具类
 * 
 * @author 庄宏岩
 * 
 */
public class ChooseWallpaperUitls implements OnClickListener, AdapterView.OnItemClickListener {
	private Context mContext;
	private View view;
	private View diy_wallpaper_tv_1, diy_wallpaper_tv_2, diy_wallpaper_tv_3, diy_wallpaper_tv_4, diy_wallpaper_tv_5;
	private OnWallpaperSelecterListener mOnWallpaperSelecterListener;
	private ChooseWallpaperAdapter adapter;
	private WallpaperUtils.Resolution resolution;

	private final int MSG_NOTIFY_CHANGED = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_REQUEST_URL_JSON = 102;

	private static final int ID_FEATURED = 37;
	private static final int ID_CARTOON = 38;
	private static final int ID_STAR = 39;
	private static final int ID_AESTHETICISM = 40;
//	private static final int ID_GAME = 42;
	private static final int ID_YOUTH = 44;

	private int maxThumbnailWidth, maxThumbnailHeight;

	private Map<Integer, String> categoryMap;

	public ChooseWallpaperUitls(Context context) {
		this.mContext = context;
		adapter = new ChooseWallpaperAdapter(context);
		view = LayoutInflater.from(mContext).inflate(R.layout.choose_wallpaper_layout, null);
		HorizontalListView listView = (HorizontalListView) view.findViewById(R.id.wallpaper_diy_hs);
		listView.setOnItemClickListener(this);
		diy_wallpaper_tv_1 = view.findViewById(R.id.diy_wallpaper_tv_1);
		diy_wallpaper_tv_2 = view.findViewById(R.id.diy_wallpaper_tv_2);
		diy_wallpaper_tv_3 = view.findViewById(R.id.diy_wallpaper_tv_3);
		diy_wallpaper_tv_4 = view.findViewById(R.id.diy_wallpaper_tv_4);
		diy_wallpaper_tv_5 = view.findViewById(R.id.diy_wallpaper_tv_5);
		view.findViewById(R.id.choose_wallpaper_button1).setOnClickListener(this);
		view.findViewById(R.id.choose_wallpaper_button2).setOnClickListener(this);
		view.findViewById(R.id.choose_wallpaper_button3).setOnClickListener(this);
		view.findViewById(R.id.choose_wallpaper_button4).setOnClickListener(this);
		view.findViewById(R.id.choose_wallpaper_button5).setOnClickListener(this);

		categoryMap = new HashMap<Integer, String>(5);
		categoryMap.put(ID_FEATURED, mContext.getResources().getString(R.string.paper_hot));
		categoryMap.put(ID_AESTHETICISM, mContext.getResources().getString(R.string.paper_beutiful));
		categoryMap.put(ID_CARTOON, mContext.getResources().getString(R.string.paper_2));
		categoryMap.put(ID_STAR, mContext.getResources().getString(R.string.paper_famous));
		categoryMap.put(ID_YOUTH, mContext.getResources().getString(R.string.paper_youth));

		resolution = new WallpaperUtils(context).getResolution();

		listView.setAdapter(adapter);

		setViewCheck(0);

		maxThumbnailWidth = LockApplication.getInstance().getConfig().getScreenWidth() / 3;
		maxThumbnailHeight = maxThumbnailWidth * 16 / 9;

		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;
		msg.arg1 = ID_FEATURED;
		mHandler.sendMessage(msg);
	}

	public View getView() {
		return view;
	}

	public void setOnWallpaperSelecterListener(OnWallpaperSelecterListener onWallpaperSelecterListener) {
		this.mOnWallpaperSelecterListener = onWallpaperSelecterListener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			((Activity) mContext).startActivityForResult(intent, 1);
		} else if (position == adapter.getCount() - 1) {
            Intent zone_paperIntent = new Intent(mContext, WallpaperListActivity.class);
            zone_paperIntent.putExtra("id", adapter.getGroupId());
            zone_paperIntent.putExtra("title", adapter.getGroupName());
            zone_paperIntent.putExtra("from_diy", true);
            ((Activity) mContext).startActivityForResult(zone_paperIntent, 1090);
            ((Activity) mContext).overridePendingTransition(R.anim.activity_in, 0);
		} else {
			final WallpaperGridItemInfo info = (WallpaperGridItemInfo) adapter.getItem(position);
			if (info != null && mOnWallpaperSelecterListener != null) {

				mOnWallpaperSelecterListener.selectWallpaper(info.getThumbnailUrl(), info.getImageUrl());
			}
		}
	}

	public interface OnWallpaperSelecterListener {
		void selectWallpaper(String thumbnailUrl, String imageUrl);
	}

	@Override
	public void onClick(View v) {
		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;
		int i = v.getId();
		if (i == R.id.choose_wallpaper_button1) {
			setViewCheck(0);
			msg.arg1 = ID_FEATURED;
			mHandler.sendMessage(msg);

		} else if (i == R.id.choose_wallpaper_button2) {
			setViewCheck(1);
			msg.arg1 = ID_CARTOON;
			mHandler.sendMessage(msg);

		} else if (i == R.id.choose_wallpaper_button3) {
			setViewCheck(2);
			msg.arg1 = ID_STAR;
			mHandler.sendMessage(msg);

		} else if (i == R.id.choose_wallpaper_button4) {
			setViewCheck(3);
			msg.arg1 = ID_AESTHETICISM;
			mHandler.sendMessage(msg);

		} else if (i == R.id.choose_wallpaper_button5) {
			setViewCheck(4);
			msg.arg1 = ID_YOUTH;
			mHandler.sendMessage(msg);

		} else {
		}
	}

	private void setViewCheck(int index) {
		View[] tv = { diy_wallpaper_tv_1, diy_wallpaper_tv_2, diy_wallpaper_tv_3, diy_wallpaper_tv_4, diy_wallpaper_tv_5 };
		for (int i = 0; i < 5; i++) {
			if (i == index)
				tv[i].setSelected(true);
			else
				tv[i].setSelected(false);
		}
	}

	private void requestCachedJson(int id) {

		String url = getRequestUrl(id);
		if (url != null) {
			JSONObject jsonObjectCached = VolleyUtil.instance().getJsonObject(url);
			if (jsonObjectCached != null) {
				parseJson(jsonObjectCached);
			}
		}
	}

	private void requestUrlJson(final int id) {
		String url = getRequestUrl(id);
		if (url != null) {
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							parseJson(response);
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message msg = new Message();
							msg.arg1 = id;
                            msg.what = MSG_REQUEST_CACHED_JSON;
                            mHandler.sendMessage(msg);
						}
					});
			VolleyUtil.instance().addRequest(jsonObjectRequest);
		}
	}

	private String getRequestUrl(int id) {
		adapter.setGroupId(id);
		adapter.setGroupName(categoryMap.get(id));

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETWALLPAPER_DIY + "?json=" + jsonObject.toString());
		RLog.i("WALLPAPER_DIY_URL", url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200&& jsonObject.has("json")) {

			ArrayList<WallpaperGridItemInfo> list = new ArrayList<WallpaperGridItemInfo>();
			JSONArray array = jsonObject.optJSONArray("json");
			for (int i = 0; i < array.length(); i++) {
				JSONObject js = array.optJSONObject(i);
				WallpaperGridItemInfo info = new WallpaperGridItemInfo();
				if (resolution == WallpaperUtils.Resolution.big) {
					info.setThumbnailUrl(js.optString("abbr_png"));
					info.setImageUrl(js.optString("image_png"));
				} else if (resolution == WallpaperUtils.Resolution.normal) {
					info.setThumbnailUrl(js.optString("abbr_720"));
					info.setImageUrl(js.optString("image_720"));
				} else {
					info.setThumbnailUrl(js.optString("abbr_480"));
					info.setImageUrl(js.optString("image_480"));
				}

				list.add(info);
			}

			adapter.setArrayList(list);

			mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			int id = msg.arg1;
			mHandler.removeMessages(msg.what);
			switch (what) {
				case MSG_NOTIFY_CHANGED:
					adapter.notifyDataSetChanged();
					break;

				case MSG_REQUEST_URL_JSON:
					adapter.clearList();
					requestUrlJson(id);
					break;

				case MSG_REQUEST_CACHED_JSON:
					requestCachedJson(id);
					break;

				default:
					break;
			}
			return false;
		}
	});

	class ChooseWallpaperAdapter extends BaseAdapter {
		private ArrayList<WallpaperGridItemInfo> arrayList;
		private LayoutInflater inflater;
		private Context mContext;
        private int groupId;
        private String groupName;

		public ChooseWallpaperAdapter(Context mContext) {
			this.arrayList = new ArrayList<WallpaperGridItemInfo>();
			this.mContext = mContext;
			inflater = LayoutInflater.from(mContext);
		}

		public ArrayList<WallpaperGridItemInfo> getArrayList() {
			return arrayList;
		}

		public void setArrayList(ArrayList<WallpaperGridItemInfo> arrayList) {
			this.arrayList = arrayList;
		}

		public void clearList() {
			if (arrayList != null) {
				arrayList.clear();
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			if (arrayList.size() == 0)
				return 5;

			return arrayList.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			if (arrayList.size() == 0)
				return null;
			if (position == arrayList.size() + 1)
				return null;

			return arrayList.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gridview_item_choose_wallpaper, parent, false);
				holder.wallpaper_imageview = (ImageView) convertView.findViewById(R.id.wallpaper_imageview);
				holder.wallpaper_textview = (TextView) convertView.findViewById(R.id.wallpaper_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.wallpaper_imageview.setVisibility(View.GONE);
				holder.wallpaper_textview.setVisibility(View.VISIBLE);
				holder.wallpaper_textview.setText(R.string.open_album);
			} else if (position == getCount() - 1) {
				holder.wallpaper_imageview.setVisibility(View.GONE);
				holder.wallpaper_textview.setVisibility(View.VISIBLE);
				holder.wallpaper_textview.setText(R.string.more_change);
			} else {
				holder.wallpaper_imageview.setVisibility(View.VISIBLE);
				holder.wallpaper_textview.setVisibility(View.GONE);
				holder.wallpaper_imageview.setBackgroundResource(R.drawable.wallpaper_thumbnail_default);
				if (arrayList.size() > 0) {
					WallpaperGridItemInfo info = (WallpaperGridItemInfo) getItem(position);
					if (info != null) {
						VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), holder.wallpaper_imageview, info.getThumbnailUrl(), R.drawable.wallpaper_thumbnail_default,
								R.drawable.wallpaper_thumbnail_default,
								maxThumbnailWidth,
								maxThumbnailHeight);
					}
				}
			}

			return convertView;
		}

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        class ViewHolder {
			public ImageView wallpaper_imageview;
			public TextView wallpaper_textview;
		}
	}
}
