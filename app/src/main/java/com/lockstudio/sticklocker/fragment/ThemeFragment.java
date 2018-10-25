package com.lockstudio.sticklocker.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.activity.AuthorInfoActivity;
import com.lockstudio.sticklocker.activity.DiyActivity;
import com.lockstudio.sticklocker.activity.LockThemePreviewActivity;
import com.lockstudio.sticklocker.activity.MainActivity;
import com.lockstudio.sticklocker.activity.MiuiDetailsActivity;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseFragment;
import com.lockstudio.sticklocker.model.ThemeConfig;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.MConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.opda.android.activity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends BaseFragment implements OnItemClickListener {

	private ThemeAdapter themeAdapter;
	private SharedPreferences sp;
	private ArrayList<ThemeConfig> allThemeConfigs = new ArrayList<ThemeConfig>();

	private BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && MConstants.ACTION_UPDATE_LOCAL_THEME.equals(intent.getAction())) {
				String theme_path = intent.getStringExtra("theme_path");
				if (!TextUtils.isEmpty(theme_path)) {
					String previewPath = new File(theme_path, "preview").getAbsolutePath();
					Bitmap bitmap = DrawableUtils.getBitmap(mContext, previewPath);
					if (bitmap != null) {
						bitmap = DrawableUtils.scaleTo(bitmap, 0.5f, 0.5f);
						bitmap = DrawableUtils.getRoundedCornerBitmap(bitmap, 6);
						VolleyUtil.instance().putBitmap(previewPath, bitmap);
					}
				}
				new GetThemeThread().start();
			}
		}
	};

	public void showThemeGuide() {
		// TODO Auto-generated method stub
		if(!sp.getBoolean("themeGuide", false)){
			Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
			intent.putExtra("flag", 7);
			startActivity(intent);
			sp.edit().putBoolean("themeGuide", true).commit();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getActivity().getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getActivity().getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		IntentFilter intentFilter = new IntentFilter(MConstants.ACTION_UPDATE_LOCAL_THEME);
		mContext.registerReceiver(updateBroadcastReceiver, intentFilter);
		themeAdapter = new ThemeAdapter(mContext, allThemeConfigs);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_theme, container, false);
		GridView theme_gridview = (GridView) view.findViewById(R.id.theme_gridview);
		theme_gridview.setOnItemClickListener(ThemeFragment.this);
		theme_gridview.setAdapter(themeAdapter);
		if (themeAdapter.getCount() == 0) {
			new GetThemeThread().start();
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 0) {
			startActivity(new Intent(mContext, DiyActivity.class));
			CustomEventCommit.commit(mContext.getApplicationContext(), MainActivity.TAG, "MAKE_NEW_THEME");
			return;
		}
		ThemeConfig themeConfig = (ThemeConfig) parent.getItemAtPosition(position);
		Intent intent = new Intent(mContext, LockThemePreviewActivity.class);
		intent.putExtra("theme_path", themeConfig.getThemePath());
		startActivity(intent);
	}

	class GetThemeThread extends Thread {
		private ArrayList<ThemeConfig> themeConfigs;

		@Override
		public void run() {
			super.run();

			themeConfigs = new ArrayList<ThemeConfig>();

			File folder = new File(MConstants.THEME_PATH);
			if (folder.exists()) {
				File[] files = folder.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						if (file.isDirectory()) {
							if (new File(file.getAbsolutePath(), MConstants.config).exists()) {
								ThemeConfig themeConfig = new ThemeConfig();
								themeConfig.setThemePath(file.getAbsolutePath());
								themeConfig.setCreateTime(new File(file.getAbsolutePath(), MConstants.config).lastModified());

								if (new File(LockApplication.getInstance().getConfig().getThemeName()).exists()
										&& new File(themeConfig.getThemePath()).getName().equals(
												new File(LockApplication.getInstance().getConfig().getThemeName()).getName())) {
									themeConfig.setChecked(true);
									themeConfig.setCreateTime(System.currentTimeMillis());
								}
								File previewFile = new File(file.getAbsolutePath(), "preview");
								themeConfig.setThemePreviewPath(previewFile.getAbsolutePath());

								Bitmap bitmap = VolleyUtil.instance().getBitmap(themeConfig.getThemePreviewPath());
								if (bitmap == null) {
									bitmap = DrawableUtils.getBitmap(mContext, themeConfig.getThemePreviewPath());
									if (bitmap != null) {
										bitmap = DrawableUtils.scaleTo(bitmap, 0.5f, 0.5f);
										bitmap = DrawableUtils.getRoundedCornerBitmap(bitmap, 6);
										VolleyUtil.instance().putBitmap(themeConfig.getThemePreviewPath(), bitmap);
									}
								}
								themeConfigs.add(themeConfig);
							}
						}
					}
				}
			}

			Collections.sort(themeConfigs, new TimeComparator());

			ThemeConfig themeConfig = new ThemeConfig();
			themeConfigs.add(0, themeConfig);
			handler.sendEmptyMessage(0);

		}

		Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == 0 && themeConfigs != null) {
					allThemeConfigs.clear();
					allThemeConfigs.addAll(themeConfigs);
					themeAdapter.notifyDataSetChanged();

				}
				return false;
			}
		});

	}

	class TimeComparator implements Comparator<ThemeConfig> {

		@Override
		public int compare(ThemeConfig o1, ThemeConfig o2) {
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			mContext.unregisterReceiver(updateBroadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ThemeAdapter extends BaseAdapter {
		private ArrayList<ThemeConfig> themeConfigs = new ArrayList<ThemeConfig>();
		private LayoutInflater inflater;

		public ThemeAdapter(Context mContext, ArrayList<ThemeConfig> themeConfigs) {
			this.themeConfigs = themeConfigs;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return themeConfigs.size();
		}

		@Override
		public Object getItem(int position) {
			return themeConfigs.get(position);
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
				convertView = inflater.inflate(R.layout.gridview_item_theme, parent, false);
				holder.theme_imageview = (ImageView) convertView.findViewById(R.id.theme_imageview);
				holder.theme_checked_imageview = (ImageView) convertView.findViewById(R.id.theme_checked_imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ThemeConfig themeConfig = themeConfigs.get(position);

			if (themeConfig.getThemePreviewPath() != null) {
				Bitmap bitmap = VolleyUtil.instance().getBitmap(themeConfig.getThemePreviewPath());
				if (bitmap == null) {
					bitmap = DrawableUtils.getBitmap(mContext, themeConfig.getThemePreviewPath());
					if (bitmap != null) {
						bitmap = DrawableUtils.scaleTo(bitmap, 0.5f, 0.5f);
						bitmap = DrawableUtils.getRoundedCornerBitmap(bitmap, 6);
						VolleyUtil.instance().putBitmap(themeConfig.getThemePreviewPath(), bitmap);
					}
				}
				if (bitmap != null) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						holder.theme_imageview.setBackground(DrawableUtils.bitmap2Drawable(mContext, bitmap));
					} else {
						holder.theme_imageview.setBackgroundDrawable(DrawableUtils.bitmap2Drawable(mContext, bitmap));
					}
				} else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						holder.theme_imageview.setBackground(mContext.getResources().getDrawable(R.drawable.diy_my_locker));
					} else {
						holder.theme_imageview.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.diy_my_locker));
					}
				}
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					holder.theme_imageview.setBackground(mContext.getResources().getDrawable(R.drawable.diy_my_locker));
				} else {
					holder.theme_imageview.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.diy_my_locker));
				}
			}
			if (themeConfig.isChecked()) {
				holder.theme_checked_imageview.setVisibility(View.VISIBLE);
			} else {
				holder.theme_checked_imageview.setVisibility(View.GONE);
			}
			return convertView;
		}

		class ViewHolder {
			public ImageView theme_imageview;
			public ImageView theme_checked_imageview;
		}
	}
}
