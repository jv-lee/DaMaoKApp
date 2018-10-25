package com.lockstudio.sticklocker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.Tommy.VolleyUtil;
import com.lockstudio.sticklocker.model.ImageResource;
import com.lockstudio.sticklocker.util.DrawableUtils;

import java.util.ArrayList;

import cn.opda.android.activity.R;

public class ImageSelectAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ImageResource> arrayList = new ArrayList<ImageResource>();

	public ImageSelectAdapter(Context context, ArrayList<ImageResource> imageResources) {
		this.mContext = context;
		this.arrayList = imageResources;
	}

	public ArrayList<ImageResource> getList() {
		return arrayList;
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_gridview_image, null);
			holder.item_image_imageview = (ImageView) convertView.findViewById(R.id.item_image_imageview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ImageResource imageInfo = (ImageResource) getItem(position);
		if (imageInfo != null) {
			if (imageInfo.isLocal()) {
				if (imageInfo.isAssets()) {
					Drawable drawable = DrawableUtils.bitmap2Drawable(mContext, imageInfo.getBitmap());
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						holder.item_image_imageview.setBackground(drawable);
					} else {
						holder.item_image_imageview.setBackgroundDrawable(drawable);
					}
				} else {
					Drawable drawable = DrawableUtils.bitmap2Drawable(mContext, DrawableUtils.getBitmap(mContext, imageInfo.getPath()));
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						holder.item_image_imageview.setBackground(drawable);
					} else {
						holder.item_image_imageview.setBackgroundDrawable(drawable);
					}
				}
			} else {
				String url = imageInfo.getUrl();
				if (url != null) {
					VolleyUtil.instance().setUrlImage(VolleyUtil.instance().getRequestQueue(), holder.item_image_imageview, url, 0, 0, 0, 0);
				}
			}

		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView item_image_imageview;
	}
}
