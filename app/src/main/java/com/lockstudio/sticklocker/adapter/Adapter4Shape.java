package com.lockstudio.sticklocker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lockstudio.sticklocker.view.FullWidthImageView;

import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 形状的adapter
 * @author 庄宏岩
 * 
 */
public class Adapter4Shape extends BaseAdapter {
	private ArrayList<Bitmap> shapes;
	private Context mContext;

	public Adapter4Shape(Context context, ArrayList<Bitmap> shapes) {
		this.shapes = shapes;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return shapes.size();
	}

	@Override
	public Object getItem(int position) {
		return shapes.get(position);
	}

	public ArrayList<Bitmap> getList() {
		return shapes;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder mHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.gridview_item_shape, parent, false);
			mHolder = new Holder();
			mHolder.shape_imageview = (FullWidthImageView) convertView.findViewById(R.id.shape_imageview);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		final Bitmap bitmap = shapes.get(position);
		mHolder.shape_imageview.setImageBitmap(bitmap);
		return convertView;
	}

	class Holder {
		private FullWidthImageView shape_imageview;
	}
}
