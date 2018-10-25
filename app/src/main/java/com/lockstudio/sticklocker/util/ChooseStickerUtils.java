package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.lockstudio.sticklocker.activity.TextImageEditActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.model.StickerImageInfo;
import com.lockstudio.sticklocker.view.EditTextDialog;
import com.lockstudio.sticklocker.view.EditTextDialog.OnEditTextOkClickListener;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 选择贴纸view的工具类
 * 
 * @author 庄宏岩
 */
public class ChooseStickerUtils implements OnClickListener, TwoWayAdapterView.OnItemClickListener, TwoWayAbsListView.OnScrollListener {

	private static final String TAG = "V5_STICKER";

	private Context mContext;
	private View view;
	private TextView sticker_group_textview_1, sticker_group_textview_2, sticker_group_textview_3, sticker_group_textview_4, sticker_group_textview_5;
	private StickerResourceAdapter adapter;
	private OnImageSelectorListener mOnImageSelectorListener;
	private TwoWayGridView listView;

	private final int MSG_NOTIFY_CHANGED = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_REQUEST_URL_JSON = 102;

	private static final int ID_FEATURED = 1;
	private static final int ID_CARTOON = 2;
	private static final int ID_STAR = 3;
	private static final int ID_CHARACTER = 4;
	private static final int ID_EMOTION = 6;

	public static final int FROM_STICKER = 0;
	public static final int FROM_LOCKER_LOVE = 1;
	public static final int FROM_LOCKER_NINE = 2;
	public static final int FROM_LOCKER_TWELVE = 3;
	public static final int FROM_LOCKER_LOVERS = 4;
	public static final int FROM_LOCKER_SLIDE = 5;
	public static final int FROM_LOCKER_IMAGEPASSWORD = 6;
	public static final int FROM_LOCKER_APPS = 7;

	private int firstVisibleItem = 0, visibleItemCount = 14;

	public ChooseStickerUtils(Context context) {
		this(context, 0);
	}

	public ChooseStickerUtils(Context context, int from) {
		LockApplication.getInstance().getConfig().setFrom_id(from);
		this.mContext = context;
		adapter = new StickerResourceAdapter(context);

		view = LayoutInflater.from(context).inflate(R.layout.choose_sticker_resource_layout, null);
		sticker_group_textview_1 = (TextView) view.findViewById(R.id.sticker_group_textview_1);
		sticker_group_textview_2 = (TextView) view.findViewById(R.id.sticker_group_textview_2);
		sticker_group_textview_3 = (TextView) view.findViewById(R.id.sticker_group_textview_3);
		sticker_group_textview_4 = (TextView) view.findViewById(R.id.sticker_group_textview_4);
		sticker_group_textview_5 = (TextView) view.findViewById(R.id.sticker_group_textview_5);
		sticker_group_textview_1.setSelected(true);

		LinearLayout sticker_group_1 = (LinearLayout) view.findViewById(R.id.sticker_group_1);
		LinearLayout sticker_group_2 = (LinearLayout) view.findViewById(R.id.sticker_group_2);
		LinearLayout sticker_group_3 = (LinearLayout) view.findViewById(R.id.sticker_group_3);
		LinearLayout sticker_group_4 = (LinearLayout) view.findViewById(R.id.sticker_group_4);
		LinearLayout sticker_group_5 = (LinearLayout) view.findViewById(R.id.sticker_group_5);

		sticker_group_1.setOnClickListener(this);
		sticker_group_2.setOnClickListener(this);
		sticker_group_3.setOnClickListener(this);
		sticker_group_4.setOnClickListener(this);
		sticker_group_5.setOnClickListener(this);

		listView = (TwoWayGridView) view.findViewById(R.id.choose_sticker_horizontal_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);

		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;
		msg.arg1 = ID_FEATURED;
		mHandler.sendMessage(msg);
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
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.d("STICKER_DIY", response.toString());
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Message msg = new Message();
					msg.arg1 = id;
                    msg.what = MSG_REQUEST_CACHED_JSON;
                    mHandler.sendMessage(msg);
				}
			});
			RequestQueue requestQueue = VolleyUtil.instance().getRequestQueue();
			if (requestQueue != null) {
				requestQueue.add(jsonObjectRequest);
			}
		}
	}

	private String getRequestUrl(int id) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETSTICKER + "?json=" + jsonObject.toString());
		RLog.i("STICKER_DIY_URL", url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray array = jsonObject.optJSONArray("json");
			if (null != array) {
				int count = array.length();
				RLog.d("STICKER_DIY", "array.length=" + count);
				ArrayList<StickerImageInfo> list = new ArrayList<StickerImageInfo>();
				for (int i = 0; i < count; i++) {
					JSONObject js = array.optJSONObject(i);
					String url = js.optString("img");
					if (null != url) {
						StickerImageInfo info = new StickerImageInfo();
						info.setImageUrl(url);
						list.add(info);
					}
				}

				adapter.setArrayList(list);

				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
			}
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

	public View getView() {
		if (listView != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				listView.setScrollX(0);
			}
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		sticker_group_textview_1.setSelected(false);
		sticker_group_textview_2.setSelected(false);
		sticker_group_textview_3.setSelected(false);
		sticker_group_textview_4.setSelected(false);
		sticker_group_textview_5.setSelected(false);

		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;

		int i = v.getId();
		if (i == R.id.sticker_group_1) {
			sticker_group_textview_1.setSelected(true);
			msg.arg1 = ID_FEATURED;
			mHandler.sendMessage(msg);

		} else if (i == R.id.sticker_group_2) {
			sticker_group_textview_2.setSelected(true);
			msg.arg1 = ID_EMOTION;
			mHandler.sendMessage(msg);

		} else if (i == R.id.sticker_group_3) {
			sticker_group_textview_3.setSelected(true);
			msg.arg1 = ID_CARTOON;
			mHandler.sendMessage(msg);

		} else if (i == R.id.sticker_group_4) {
			sticker_group_textview_4.setSelected(true);
			msg.arg1 = ID_STAR;
			mHandler.sendMessage(msg);

		} else if (i == R.id.sticker_group_5) {
			sticker_group_textview_5.setSelected(true);
			msg.arg1 = ID_CHARACTER;
			mHandler.sendMessage(msg);

		} else {
		}
	}

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			((Activity) mContext).startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER);
			((Activity) mContext).overridePendingTransition(R.anim.activity_in, 0);
			return;
		} else if (position == 1) {
			final EditTextDialog editTextDialog = new EditTextDialog(mContext);
			editTextDialog.setHintText(R.string.click_input_word);
			editTextDialog.setEditTextOkClickListener(new OnEditTextOkClickListener() {

				@Override
				public void OnEditTextOkClick(String string) {
					if (!TextUtils.isEmpty(string)) {

						Intent intent = new Intent(mContext, TextImageEditActivity.class);
						intent.putExtra("text", string);
						((Activity) mContext).startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
						((Activity) mContext).overridePendingTransition(R.anim.activity_in, 0);
						editTextDialog.dismiss();
					} else {
						SimpleToast.makeText(mContext, R.string.word_not_null, SimpleToast.LENGTH_SHORT).show();
					}

				}
			});
			editTextDialog.show();

			return;
		}

		StickerImageInfo imageInfo = (StickerImageInfo) adapter.getItem(position);
		if (imageInfo != null) {
			String url = imageInfo.getImageUrl();
			if (url != null) {
				mOnImageSelectorListener.selectImage(url);
			}
		}
	}

	public void setOnImageSelectorListener(OnImageSelectorListener onImageSelectorListener) {
		this.mOnImageSelectorListener = onImageSelectorListener;
	}

	@Override
	public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {
		switch (scrollState) {
			case SCROLL_STATE_IDLE:
				VolleyUtil.instance().cancelAll(TAG);
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
		}
	}

	@Override
	public void onScroll(TwoWayAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
	}

	public interface OnImageSelectorListener {
		void selectImage(String imageUrl);
	}

	class StickerResourceAdapter extends BaseAdapter {
		private ArrayList<StickerImageInfo> arrayList = new ArrayList<StickerImageInfo>();
		private LayoutInflater inflater;

		public StickerResourceAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void setArrayList(ArrayList<StickerImageInfo> arrayList) {
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
			if (arrayList.size() == 0) {
				return 14;
			}
			return arrayList.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			if (arrayList.size() == 0) {
				return null;
			}

			return arrayList.get(position - 2);
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
				convertView = inflater.inflate(R.layout.gridview_item_sticker_resource, parent, false);
				holder.resource_imageview = (ImageView) convertView.findViewById(R.id.resource_imageview);
				holder.resource_layout = (LinearLayout) convertView.findViewById(R.id.resource_layout);
				holder.resource_textview = (TextView) convertView.findViewById(R.id.resource_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == 0) {
				holder.resource_layout.setVisibility(View.GONE);
				holder.resource_textview.setVisibility(View.VISIBLE);
				holder.resource_textview.setText(R.string.open_album);
				return convertView;
			} else if (position == 1) {
				holder.resource_layout.setVisibility(View.GONE);
				holder.resource_textview.setVisibility(View.VISIBLE);
				holder.resource_textview.setText(R.string.text_image);
				return convertView;
			}

			holder.resource_layout.setVisibility(View.VISIBLE);
			holder.resource_textview.setVisibility(View.GONE);
			holder.resource_imageview.setBackgroundResource(android.R.color.transparent);

			if (arrayList.size() > 0) {
				final StickerImageInfo imageInfo = (StickerImageInfo) getItem(position);
				if (imageInfo != null && position >= firstVisibleItem && position < (firstVisibleItem + visibleItemCount)) {
					String url = imageInfo.getImageUrl();
					if (url != null) {
						VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), holder.resource_imageview, url, 0, 0, 0, 0, TAG);
					}
				}
			}
			return convertView;
		}

		class ViewHolder {
			public TextView resource_textview;
			public LinearLayout resource_layout;
			public ImageView resource_imageview;
		}

	}

}
