package com.lockstudio.sticklocker.view;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.BindShortcutListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.CustomPopupView;
import com.lockstudio.sticklocker.model.AppInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.opda.android.activity.R;

/**
 * 
 * 选择APP弹出框
 * 
 */
public class SelectAppPopup extends CustomPopupView implements
		View.OnClickListener {
	private TextView title_bar_right_tv;
	private Context mContext;
	private OnAddAppItemListener mListener;
	private LayoutInflater inflater;
	// ok按钮
	private Button select_ok_btn;
	// 每个页中的ViewPager对象
	private GridView app_grid_child_item;
	private AppInfo mAppInfo;

	private CustomPopupView popup_allapp;
	private View view_allapp;
	private TextView title_bar_left_tv;
	private BindShortcutListener mBindShortcutListener;
	private OnImageSelectorListener mOnImageSelectorListener;
	private ArrayList<AppInfo> selectResolveInfo = new ArrayList<AppInfo>();

	@SuppressWarnings("deprecation")
	public SelectAppPopup(Context context, View view_parent,
			OnAddAppItemListener listener) {
		this.mContext = context;
		mListener = listener;
		inflater = LayoutInflater.from(mContext);
		view_allapp = inflater.inflate(R.layout.dialog_add_application_layout,null);

		
		title_bar_right_tv = (TextView) view_allapp
				.findViewById(R.id.title_bar_right_tv);
		title_bar_right_tv.setText("应用");
		select_ok_btn = (Button) view_allapp.findViewById(R.id.select_ok_btn);
		title_bar_left_tv = (TextView) view_allapp
				.findViewById(R.id.title_bar_left_tv);
		select_ok_btn.setOnClickListener(this);
		title_bar_left_tv.setOnClickListener(this);
		app_grid_child_item = (GridView) view_allapp
				.findViewById(R.id.grid_child_item);
//		LockContainer lockContainer = LauncherUtils.getInstance(mContext).getCurContainer();
//		String wallpaper = lockContainer.tempWallpaper;
//		if(!TextUtils.isEmpty(wallpaper)){
//			Drawable drawable = DrawableUtils.bitmap2Drawable(mContext, DrawableUtils.getBitmap(mContext, wallpaper));
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//				app_grid_child_item.setBackground(drawable);
//			} else {
//				app_grid_child_item.setBackgroundDrawable(drawable);
//			}
//		}
		initAllApplicationData();
		popup_allapp = getPopupWindow(context, view_allapp);
	}

	public void setBindShortcutListener(
			BindShortcutListener mBindShortcutListener) {
		this.mBindShortcutListener = mBindShortcutListener;
	}

	/**
	 * 初始化applicaiton
	 */
	private void initAllApplicationData() {
		// 若当前所有应用信息未全部加载,则暂时返回
		if (LockApplication.getInstance().getAppManagerUtils()
				.getAppList().size() == 0) {
			return;
		}

		// AppInfo mAAppInfo=new AppInfo();
		// mAAppInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.all_apps));
		// mAAppInfo.setAppName("全部应用");
		// mAAppInfo.setChecked(false);
		// mAAppInfo.setPackageName("system.system.allapp");
		// mAAppInfo.setSystemApp(true);//

		// HashMap<String, AppInfo>
		// before_map_allapps=FancyLauncherApplication.getInstance().getAppManagerUtils().getAppList();
		// HashMap<String, AppInfo> map_allapps=new HashMap<String, AppInfo>();
		// map_allapps.put("system.system.allapp", mAppInfo);
		// map_allapps.putAll(before_map_allapps);

		// can't isMultiple
		final SelectAppImageAdapter app_ImageAdapter = new SelectAppImageAdapter(
				mContext, LockApplication.getInstance()
						.getAppManagerUtils().getAppList(), false);
		mAppInfo = app_ImageAdapter.getItem(0);
		app_grid_child_item.setAdapter(app_ImageAdapter);
		app_grid_child_item.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				单选
				app_ImageAdapter.chcekItem(position);
				mAppInfo = app_ImageAdapter.getItem(position);
				title_bar_right_tv.setText(mAppInfo.getAppName());
//				多选
//				AppInfo appInfo = app_ImageAdapter.getItem(position);
//				if(appInfo.isChecked()){
//					appInfo.setChecked(false);
//					selectResolveInfo.remove(appInfo);
//				}else{
//					appInfo.setChecked(true);
//					selectResolveInfo.add(appInfo);
//				}
				app_ImageAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.title_bar_left_tv) {
			popup_allapp.dismiss();

		} else if (i == R.id.select_ok_btn) {
			if (mBindShortcutListener != null) {
				mBindShortcutListener.bindShortcut(mAppInfo.getPackageName(),
						mAppInfo.getComponentName().getClassName());
				LockApplication.getInstance().getConfig().setFrom_id(ChooseStickerUtils.FROM_LOCKER_APPS);
				BitmapDrawable bd = (BitmapDrawable) mAppInfo.getAppIcon();
				Bitmap bitmap = bd.getBitmap();
				if (bitmap != null) {
					mOnImageSelectorListener.selectImage(bitmap);
				}
//				Intent intent = new Intent(mContext, DiyActivity.class);
//				intent.putExtra("iconByte", DrawableUtils.bitmap2Byte(bitmap));
//				((Activity) mContext).setResult(MConstants.REQUEST_CODE_STICKER_EDIT, intent);
			} else {
				mListener.OnAddAppItem(mAppInfo);
			}
			popup_allapp.dismiss();

		} else {
		}
	}
	
	public void setOnImageSelectorListener(OnImageSelectorListener onImageSelectorListener) {
		this.mOnImageSelectorListener = onImageSelectorListener;
	}
	
	public interface OnImageSelectorListener {
		void selectImage(Bitmap bitmap);
	}

	public interface OnAddAppItemListener {
		public void OnAddAppItem(AppInfo appInfo);
	}

	class SelectAppImageAdapter extends BaseAdapter {
		private boolean isMultiple = false;// 是否多选
		private Context mContext;
		private List<AppInfo> appsInfos = new ArrayList<AppInfo>();
		private List<AppInfo> appsAllInfos = new ArrayList<AppInfo>();
		private Map<ComponentName, AppInfo> map_apps;
		private int index = -1;

		public SelectAppImageAdapter(Context context,
				Map<ComponentName, AppInfo> map_apps, boolean isMultiple) {
			this.mContext = context;
			this.map_apps = map_apps;
			this.isMultiple = isMultiple;
			initDate();
		}

		// 初始化isSelected的数据
		private void initDate() {
			Iterator mIterator = map_apps.keySet().iterator();
			while (mIterator.hasNext()) {
				Object key = mIterator.next();
				appsInfos.add(map_apps.get(key));
			}

//			AppInfo mAppInfo = new AppInfo();
//			mAppInfo.setAppIcon(mContext.getResources().getDrawable(
//					R.drawable.all_apps));
//			mAppInfo.setAppName("全部应用");
//			mAppInfo.setPackageName("system.system.system");
//			mAppInfo.setComponentName(new ComponentName("system.system.system", "system"));
//			mAppInfo.setSystemApp(true);
//			appsAllInfos.add(mAppInfo);
			appsAllInfos.addAll(appsInfos);
//			多选
//			for (AppInfo appInfo : appsAllInfos) {
//				appInfo.setChecked(false);
//			}
		}

		public void chcekItem(int position) {
			index = position;
		}

		@Override
		public int getCount() {
			return appsAllInfos.size();
		}

		@Override
		public AppInfo getItem(int position) {
			return appsAllInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void remove(int position) {
			appsAllInfos.remove(position);
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppItemHolder mHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_app_view, null);
				mHolder = new AppItemHolder();
				mHolder.tiv = (ImageView) convertView
						.findViewById(R.id.all_imageview);
				mHolder.tv = (TextView) convertView
						.findViewById(R.id.theme_text_tv);
				mHolder.app_image = (ImageView) convertView
						.findViewById(R.id.app_badger);
				convertView.setTag(mHolder);
			} else {
				mHolder = (AppItemHolder) convertView.getTag();
			}
			AppInfo mAppInfo = appsAllInfos.get(position);
			mHolder.tiv.setImageDrawable(mAppInfo.getAppIcon());
			mHolder.tv.setText(mAppInfo.getAppName());

			if (isMultiple) {
				// 多选
				if (mAppInfo.isChecked()) {
					mHolder.app_image.setVisibility(View.VISIBLE);
				} else {
					mHolder.app_image.setVisibility(View.INVISIBLE);
				}
			} else {
				mHolder.app_image.setVisibility(View.GONE);
				if (index == position) {
					mHolder.app_image.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}

		class AppItemHolder {
			public ImageView app_image;
			public TextView tv;
			public ImageView tiv;
		}
	}
}
