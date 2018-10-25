package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.Interface.ImageSelectListener;
import com.lockstudio.sticklocker.adapter.ImageSelectAdapter;
import com.lockstudio.sticklocker.model.ImageResource;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.opda.android.activity.R;

public class ImageItemPager implements OnItemClickListener {
	private Context mContext;
	private GridView gridView;
	private ImageSelectAdapter adapter;
	private View view;
	private ImageSelectListener mImageSelectListener;
	private ArrayList<ImageResource> imageResources = new ArrayList<ImageResource>();
	private final int MSG_NOTIFY_CHANGED = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_REQUEST_URL_JSON = 102;
	private int id = -1;
	private String path;
	private boolean temp = false;
	private boolean assets = false;

	public ImageItemPager(Context context, int id, ImageSelectListener imageSelectListener) {
		this.id = id;
		this.mContext = context;
		this.mImageSelectListener = imageSelectListener;
		view = initView();
	}

	public ImageItemPager(Context context, String path, ImageSelectListener imageSelectListener) {
		this.path = path;
		this.mContext = context;
		this.mImageSelectListener = imageSelectListener;
		view = initView();
	}

	public ImageItemPager(Context context, String path, boolean assets, ImageSelectListener imageSelectListener) {
		this.assets = assets;
		this.path = path;
		this.mContext = context;
		this.mImageSelectListener = imageSelectListener;
		view = initView();
	}

	public View initView() {
		view = View.inflate(mContext, R.layout.item_image_paper, null);
		gridView = (GridView) view.findViewById(R.id.gd_image);
		return view;
	}

	public void initData() {
		if (adapter == null) {
			adapter = new ImageSelectAdapter(mContext, imageResources);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(ImageItemPager.this);
		}
		if (imageResources.size() == 0) {
			if (id >= 0) {
				Message msg = new Message();
				msg.arg1 = id;
				msg.what = MSG_REQUEST_URL_JSON;
				mHandler.sendMessage(msg);
			} else {
				if (assets) {
					initAssetsImage(path);
				} else {
					initLocalImage(path);
				}
			}
		}
	}

	/**
	 * 加载内置贴纸
	 * @param path
	 */
	private void initAssetsImage(final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				AssetManager assetManager = mContext.getAssets();
				String[] paths;
				try {
					paths = assetManager.list(path);
					for(int i = 0 ;i<paths.length;i++){
						if (paths[i].equals("icon.png")) {
							continue;
						}
						ImageResource imageResource = new ImageResource();
						imageResource.setLocal(true);
						imageResource.setPath(path + "/"  + paths[i]);
						imageResource.setAssets(true);
						imageResource.setBitmap(DrawableUtils.getBitmap(mContext, assetManager.open(imageResource.getPath())));
						imageResources.add(imageResource);
					}
					mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 加载已下载的贴纸和最近使用的贴纸
	 * @param path
	 */
	private void initLocalImage(final String path) {
		if (path.equals(MConstants.TEMP_IMAGE_PATH)) {
			temp = true;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				File fileDir = new File(path);
				if (fileDir.exists()) {
					File[] files = fileDir.listFiles();
					if (files != null && files.length > 0) {
						for (int i = 0; i < files.length; i++) {
							if (null == files[i]) {
								continue;
							}
							if (!files[i].isFile()) {
								continue;
							}
							if (files[i].getName().equals("icon")) {
								continue;
							}
							ImageResource imageResource = new ImageResource();
							imageResource.setLocal(true);
							imageResource.setPath(files[i].getAbsolutePath());
							imageResource.setCreateTime(files[i].lastModified());
							imageResource.setTemp(temp);
							imageResources.add(imageResource);
						}
						if (temp) {
							//只有最近使用的贴纸才需要时间排序
							Collections.sort(imageResources, new TimeComparator());
						}
						mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
					}
				}
			}
		}).start();
	}

	class TimeComparator implements Comparator<ImageResource> {

		@Override
		public int compare(ImageResource o1, ImageResource o2) {
			long num1 = o1.getCreateTime();
			long num2 = o2.getCreateTime();
			if (num1 < num2) {
				return 1;
			} else if (num1 == num2) {
				return 0;
			} else if (num1 > num2) {
				return -1;
			}
			return 0;
		}
	}

	public View getRootView() {
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ImageResource imageResource = (ImageResource) parent.getItemAtPosition(position);
		if (imageResource == null) {
			return;
		}
		if (imageResource.isLocal()) {
			if (imageResource.isAssets()) {
				DrawableUtils.saveTempImage(imageResource.getBitmap(), imageResource.getPath());
				mImageSelectListener.selectImage(imageResource.getBitmap());
			} else {
				String path = imageResource.getPath();
				if (!imageResource.isTemp()) {
					DrawableUtils.saveTempImage(DrawableUtils.getBitmap(mContext, path), path);
				}
				mImageSelectListener.selectImage(DrawableUtils.getBitmap(mContext, path));
			}
			return;
		} else {
			Bitmap bitmap = VolleyUtil.instance().getBitmapForUrl(imageResource.getUrl());
			if (bitmap != null) {
				DrawableUtils.saveTempImage(bitmap, imageResource.getUrl());
				mImageSelectListener.selectImage(bitmap);
			}

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
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					RLog.d("image response", response.toString());
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					RLog.e("image error", error.getMessage());
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
		RLog.i("STICKER_DIY_URL", "请求壁纸URL=" + url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray array = jsonObject.optJSONArray("json");
			if (null != array) {
				int count = array.length();
				for (int i = 0; i < count; i++) {
					JSONObject js = array.optJSONObject(i);
					String url = js.optString("img");
					if (null != url) {
						ImageResource info = new ImageResource();
						info.setUrl(url);
						imageResources.add(info);
					}
				}

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
				imageResources.clear();
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
}
