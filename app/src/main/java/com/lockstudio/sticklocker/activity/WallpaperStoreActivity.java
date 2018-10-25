package com.lockstudio.sticklocker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.lockstudio.sticklocker.model.OwnWallpaperImageInfo;
import com.lockstudio.sticklocker.model.WallpaperItemInfo;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.FileUtils;
import com.lockstudio.sticklocker.util.HostUtil;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.opda.android.activity.R;

public class WallpaperStoreActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private ViewPager viewPager;

	private GridView localGridView;
	private GridView wallpaper_gridview;
	private ArrayList<OwnWallpaperImageInfo> wallPaperLists = new ArrayList<OwnWallpaperImageInfo>();
	private WallpaperListAdapter wallpaperListAdapter;

	private WallPaperAdapter adapter;
	private final int MSG_REQUEST_URL_JSON = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_NOTIFY_CHANGED = 102;

	private ImageView cursor;// 动画图片
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private int screenW;
	private int one, two;
	private TextView viewpager_tab_1, viewpager_tab_2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = WallpaperStoreActivity.this;
		setContentView(R.layout.activity_wallpaper_store);

		// 设置左上角按钮
		TextView title_bar_left_tv = (TextView) findViewById(R.id.title_bar_left_tv);
		TextView title_bar_right_tv = (TextView) findViewById(R.id.title_bar_right_tv);
		title_bar_right_tv.setText("壁纸");

		title_bar_left_tv.setOnClickListener(this);

		initView();
		initTabTextView();
		initIndView();

		IntentFilter intentFilter = new IntentFilter(MConstants.ACTION_UPDATE_LOCAL_WALLPAPER);
		registerReceiver(broadcastReceiver, intentFilter);
		//FancyLauncherApplication.getInstance().getActivityStackManager().addActivity(this);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && MConstants.ACTION_UPDATE_LOCAL_WALLPAPER.equals(intent.getAction())) {
				wallPaperLists.clear();
				OwnWallpaperImageInfo ownWallpaperImageInfo = new OwnWallpaperImageInfo();
				ownWallpaperImageInfo.setImageBitmap(DrawableUtils.getBitmap(mContext, R.drawable.wallpaper_in_album));
				wallPaperLists.add(ownWallpaperImageInfo);
				getLocalWallpaper(MConstants.USER_PHOTO_PATH);
			}
		}
	};

	public void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		OwnWallpaperImageInfo ownWallpaperImageInfo = new OwnWallpaperImageInfo();
		ownWallpaperImageInfo.setImageBitmap(DrawableUtils.getBitmap(mContext,
				R.drawable.wallpaper_in_album));
		wallPaperLists.add(ownWallpaperImageInfo);
		wallpaperListAdapter = new WallpaperListAdapter(mContext,
				wallPaperLists);
		localGridView = (GridView) inflater.inflate(R.layout.wallpaper_local_layout, null);
		localGridView.setAdapter(wallpaperListAdapter);
		localGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 0) {
					OwnWallpaperImageInfo ownWallpaperImageInfo = (OwnWallpaperImageInfo) parent
							.getItemAtPosition(position);
					Intent intent = new Intent(mContext,
							PaperEditActivity.class);
					intent.putExtra("image_path",
							ownWallpaperImageInfo.getImageUrl());
					intent.putExtra("Local", true);
					startActivityForResult(intent,
							MConstants.REQUEST_CODE_EDIT);
				} else {// 进入系统相册------------------------------------------------------------------
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent,
							MConstants.REQUEST_CODE_ALBUM);
				}
			}
		});
		getLocalWallpaper(MConstants.USER_PHOTO_PATH);
		
		adapter = new WallPaperAdapter(mContext);
		wallpaper_gridview = (GridView) inflater.inflate(R.layout.wallpaper_group_layout, null);
		wallpaper_gridview.setAdapter(adapter);
		if (adapter.getItemInfoArrayList().size() == 0) {
			mHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
		}
		
		ArrayList<View> gridViews = new ArrayList<View>();
		//Gavan 屏蔽壁纸商店
//		gridViews.add(wallpaper_gridview);
		gridViews.add(localGridView);
		viewPager.setAdapter(new ThemePagerAdapter(gridViews));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initTabTextView() {
		viewpager_tab_1 = (TextView) findViewById(R.id.viewpager_tab_1);
		//Gavan 屏蔽壁纸商店
		viewpager_tab_1.setVisibility(View.GONE);
		viewpager_tab_2 = (TextView) findViewById(R.id.viewpager_tab_2);

		viewpager_tab_1.setOnClickListener(this);
		viewpager_tab_2.setOnClickListener(this);
	}

	/**
	 * 初始化动画
	 */
	private void initIndView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.indicator_blue_small).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;// 获取分辨率宽度
		offset = 0;// 计算偏移量
		one = offset + screenW / 2;
		two = offset + (screenW / 2) * 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * ViewPager适配器
	 */
	public class ThemePagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public ThemePagerAdapter(List<View> mListViews) {
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

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				viewpager_tab_1.setTextColor(Color.RED);
				viewpager_tab_2.setTextColor(Color.parseColor("#929292"));
				break;
			case 1:
				viewpager_tab_2.setTextColor(Color.RED);
				viewpager_tab_1.setTextColor(Color.parseColor("#929292"));
				break;

			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Matrix matrix = new Matrix();
			switch (arg0) {
			case 0:
				matrix.postTranslate(offset + (one - offset) * arg1, 0);
//				viewpager_tab_1.setTextColor(Color.RED);
//				viewpager_tab_2.setTextColor(Color.parseColor("#929292"));
				break;
			case 1:
				matrix.postTranslate(one + (two - one) * arg1, 0);
//				viewpager_tab_2.setTextColor(Color.RED);
//				viewpager_tab_1.setTextColor(Color.parseColor("#929292"));
				break;

			default:
				break;
			}
			cursor.setImageMatrix(matrix);
		}

		@Override
		public void onPageScrollStateChanged(int position) {

		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				OwnWallpaperImageInfo ownWallpaperImageInfo = (OwnWallpaperImageInfo) msg.obj;
				if (ownWallpaperImageInfo != null) {
					wallPaperLists.add(ownWallpaperImageInfo);
					wallpaperListAdapter.notifyDataSetChanged();
				}
				break;
			case 2:
				break;
			default:
				break;
			}
		}

	};

	public class WallpaperListAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private ArrayList<OwnWallpaperImageInfo> arrayList;

		public WallpaperListAdapter(Context context, ArrayList<OwnWallpaperImageInfo> arrayList) {
			layoutInflater = LayoutInflater.from(context);
			this.arrayList = arrayList;
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.gridview_item_paper_detail, parent, false);
				holder.imageView = (ImageView) convertView.findViewById(R.id.gridview_paper_detail_iv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (holder != null) {
				OwnWallpaperImageInfo ownWallpaperImageInfo = (OwnWallpaperImageInfo) getItem(position);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					holder.imageView.setImageDrawable(DrawableUtils.bitmap2Drawable(mContext, ownWallpaperImageInfo.getImageBitmap()));
				} else {
					holder.imageView.setImageDrawable(DrawableUtils.bitmap2Drawable(mContext, ownWallpaperImageInfo.getImageBitmap()));
				}
			}
			return convertView;
		}

		public class ViewHolder {
			public ImageView imageView;
		}

		public void removeItem(String delete_path) {
			for (int i = 0; i < arrayList.size(); i++) {
				OwnWallpaperImageInfo imageInfo = arrayList.get(i);
				if (delete_path.equals(imageInfo.getImageUrl())) {
					arrayList.remove(i);
					notifyDataSetChanged();
					break;
				}
			}
		}
	}

	// 获取当前目录下所有的壁纸文件
	public void getLocalWallpaper(final String fileAbsolutePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(fileAbsolutePath);
				File[] subFile = file.listFiles();
				if (subFile != null && subFile.length > 0) {
					for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
						// 判断是否为文件夹
						if (!subFile[iFileLength].isDirectory()) {
							String filename = subFile[iFileLength].getName();
							if (filename.trim().toLowerCase().endsWith(".jpg")) {
								OwnWallpaperImageInfo ownWallpaperImageInfo = new OwnWallpaperImageInfo();
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inSampleSize = 4;
								Bitmap bitmap = DrawableUtils.getBitmap(mContext, fileAbsolutePath + "/" + filename, options);

								ownWallpaperImageInfo.setImageBitmap(bitmap);
								ownWallpaperImageInfo.setImageUrl(fileAbsolutePath + "/" + filename);
								Message msg = new Message();
								msg.obj = ownWallpaperImageInfo;
								msg.what = 1;
								handler.sendMessage(msg);
							}
						}
					}
				}
			}
		}).start();
	}
	
	
	
	
	/**
	 * 请求壁纸缓存数据
	 */
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

	/**
	 * 请求壁纸数据
	 */
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

	/**
	 * 拼装请求json
	 * 
	 * @return
	 */
	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0);
			jsonObject.put("version_code", packageInfo.versionCode);
			jsonObject.put("screen_width",LockApplication.getInstance().getConfig().getScreenWidth());
			jsonObject.put("screen_height", LockApplication.getInstance().getConfig().getScreenHeight());
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
				RLog.d("wallpaper group response", jsonObject.toString());
				JSONArray array = jsonObject.optJSONArray("json");
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.optJSONObject(i);
					WallpaperItemInfo itemInfo = new WallpaperItemInfo();
					itemInfo.setId(js.optInt("id"));
					itemInfo.setTitle(js.optString("title"));
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
				convertView = layoutInflater.inflate(
						R.layout.gridview_item_wallpaper, parent, false);
				holder.gridview_item_wallpaper_imageview = (ImageView) convertView
						.findViewById(R.id.gridview_item_wallpaper_imageview);
				holder.gridview_item_wallpaper_textview = (TextView) convertView
						.findViewById(R.id.gridview_item_wallpaper_textview);
				holder.gridview_item_wallpaper_layout = (LinearLayout) convertView
						.findViewById(R.id.gridview_item_wallpaper_layout);

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
								Intent more_intent = new Intent(mContext, WallpaperListActivity.class);
								more_intent.putExtra("id", itemInfo.getId());
								more_intent.putExtra("title", itemInfo.getTitle());
								more_intent.putExtra("from_diy", true);
								((Activity) mContext).startActivityForResult(more_intent, MConstants.REQUEST_CODE_WALLPAPER_LIST);
								((Activity) mContext).overridePendingTransition(R.anim.activity_in, 0);
								CustomEventCommit.commit(mContext.getApplicationContext(), MainActivity.TAG, "WALLPAPER_MORE:" + itemInfo.getTitle());
							}
						});
			}

			return convertView;
		}

		class ViewHolder {
			ImageView gridview_item_wallpaper_imageview;
			TextView gridview_item_wallpaper_textview;
			LinearLayout gridview_item_wallpaper_layout;
		}

		public ArrayList<WallpaperItemInfo> getItemInfoArrayList() {
			return itemInfoArrayList;
		}
	}
	
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
			// 点击本地壁纸-----------
			Uri uri = data.getData();
			RLog.i("DEBUG", "MConstants.REQUEST_CODE_ALBUMuri.getAuthority()=====" + uri.getAuthority());
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
				if (null == cursor) {
					return;
				}
				cursor.moveToFirst();
				natrue_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				cursor.close();
			} else {
				natrue_path = uri.getPath();
			}
			// 选择的最新的图片
			String goal1 = MConstants.IMAGECACHE_PATH + "natureCrop";
			RLog.i("DEBUG", "natrue_path===" + natrue_path);
			FileUtils.copyFile(natrue_path, goal1);// 复制文件
			Intent intent1 = new Intent(mContext, PaperEditActivity.class);
			if (!TextUtils.isEmpty(natrue_path)) {
				intent1.putExtra("image_path", natrue_path);
				intent1.putExtra("Local", true);
				startActivityForResult(intent1, MConstants.REQUEST_CODE_EDIT);
			}

			break;
		case MConstants.REQUEST_CODE_LOCAL_WALLPAPER:
			String delete_path = data.getStringExtra("delete_path");
			if (!TextUtils.isEmpty(delete_path)) {
				wallpaperListAdapter.removeItem(delete_path);
			}
			break;
		default:
			break;
		}
	}

	private String natrue_path;

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.title_bar_left_tv) {
			finish();

		} else if (i == R.id.viewpager_tab_1) {
			viewPager.setCurrentItem(0);

		} else if (i == R.id.viewpager_tab_2) {
			viewPager.setCurrentItem(1);

		} else {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

}
