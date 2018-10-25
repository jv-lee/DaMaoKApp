package com.lockstudio.sticklocker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.WallpaperItemInfo;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class WallpaperGroupActivity extends BaseActivity {
	private WallPaperAdapter adapter;

	private final int MSG_REQUEST_URL_JSON = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallpaper_group);
		adapter = new WallPaperAdapter(mContext);
		GridView wallpaper_gridview = (GridView) findViewById(R.id.wallpaper_gridview);
		wallpaper_gridview.setAdapter(adapter);
		if (adapter.getItemInfoArrayList().size() == 0) {
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}

		findViewById(R.id.button_in_album).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, MConstants.REQUEST_CODE_ALBUM);
			}
		});

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
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width", LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETWALLPAPER_ZONE + "?json=" + jsonObject.toString());
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
				convertView = layoutInflater.inflate(R.layout.gridview_item_wallpaper, parent, false);
				holder.gridview_item_wallpaper_imageview = (ImageView) convertView.findViewById(R.id.gridview_item_wallpaper_imageview);
				holder.gridview_item_wallpaper_textview = (TextView) convertView.findViewById(R.id.gridview_item_wallpaper_textview);
				holder.gridview_item_wallpaper_layout = (LinearLayout) convertView.findViewById(R.id.gridview_item_wallpaper_layout);
				holder.zhanwei_view = convertView.findViewById(R.id.gridview_item_zhanwei_view);
				holder.header_view = convertView.findViewById(R.id.gridview_item_header_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (itemInfoArrayList.size() != 0) {
				final WallpaperItemInfo itemInfo = (WallpaperItemInfo) getItem(position);
				holder.gridview_item_wallpaper_textview.setText(itemInfo.getTitle());
				VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), holder.gridview_item_wallpaper_imageview, itemInfo.getImageUrl(),
						R.drawable.wallpaper_group_default, R.drawable.wallpaper_group_default);

				holder.gridview_item_wallpaper_layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent more_intent = new Intent(mContext, WallpaperListActivity.class);
						more_intent.putExtra("id", itemInfo.getId());
						more_intent.putExtra("title", itemInfo.getTitle());
						more_intent.putExtra("from_diy", true);
						((Activity) mContext).startActivityForResult(more_intent, MConstants.REQUEST_CODE_WALLPAPER_LIST);
						((Activity) mContext).overridePendingTransition(R.anim.activity_in, 0);
						CustomEventCommit.commit(mContext.getApplicationContext(), MainActivity.TAG, "WALLPAPER_MORE:" + itemInfo.getTitle());
					}
				});


                if (position == 0 || position == 1) {
                    holder.header_view.setVisibility(View.VISIBLE);
                } else {
                    holder.header_view.setVisibility(View.GONE);

                }

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
			View header_view;
		}

		public ArrayList<WallpaperItemInfo> getItemInfoArrayList() {
			return itemInfoArrayList;
		}
	}

	private String natrue_path;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		switch (requestCode) {
		case MConstants.REQUEST_CODE_WALLPAPER_LIST:
			String thumbnailUrl = data.getStringExtra("THUMBNAIL_URL");
			String imageUrl = data.getStringExtra("IMAGE_URL");

			String goal = MConstants.IMAGECACHE_PATH + "natureCrop";
			Intent intent = new Intent(mContext, PaperEditActivity.class);
			intent.putExtra("image_path", goal);
			intent.putExtra("THUMBNAIL_URL", thumbnailUrl);
			intent.putExtra("IMAGE_URL", imageUrl);
			intent.putExtra("from_diy", true);
			startActivityForResult(intent, MConstants.REQUEST_CODE_EDIT);

			break;
		case MConstants.REQUEST_CODE_EDIT:
			if (resultCode == MConstants.RESULT_CODE_EDIT) {
				setResult(MConstants.RESULT_CODE_ALBUM, data);
				finish();
			}
			break;
		case MConstants.REQUEST_CODE_ALBUM:
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
				if (null == cursor) {
					SimpleToast.makeText(this, R.string.resource_not_found, SimpleToast.LENGTH_SHORT).show();
					return;
				}
				cursor.moveToFirst();
				natrue_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
			} else {
				natrue_path = uri.getPath();
			}
			new Thread(mRunnable).start();
			break;
		default:
			break;
		}
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			String goal = MConstants.IMAGECACHE_PATH + "natureCrop";
			FileUtils.copyFile(natrue_path, goal);
			Intent intent = new Intent(mContext, PaperEditActivity.class);
			if (!TextUtils.isEmpty(goal)) {
				intent.putExtra("image_path", goal);
				intent.putExtra("from_diy", true);
				startActivityForResult(intent, MConstants.REQUEST_CODE_EDIT);
			}
		}
	};
}
