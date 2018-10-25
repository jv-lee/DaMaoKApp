package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.lockstudio.sticklocker.adapter.Adapter4Shape;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.view.IconImageEditView;

import java.io.IOException;
import java.util.ArrayList;

import cn.opda.android.activity.R;

/**
 * 自定义图片锁屏编辑页面
 * 
 * @author 庄宏岩
 * 
 */
public class IconImageEditActivity extends BaseActivity implements OnClickListener {
	private IconImageEditView iconImageEditView;
	private GridView shape_gridview;
	private String[] shapeNames = new String[] { "shape_chilun.png", "shape_huaban.png", "shape_liubianxing.png", "shape_maomi.png", "shape_sanjiaoxing.png",
			"shape_wawatou.png", "shape_wujiaoxing.png", "shape_xiaoji.png", "shape_xinxing.png", "shape_yezi.png", "shape_yuanxing.png", "shape_yunduo.png",
			"shape_zhengfangxing.png", "shape_zuanshi.png" };
	private int[] shapeResource = { R.drawable.shape_chilun, R.drawable.shape_huaban, R.drawable.shape_liubianxing, R.drawable.shape_maomi,
			R.drawable.shape_sanjiaoxing, R.drawable.shape_wawatou, R.drawable.shape_wujiaoxing, R.drawable.shape_xiaoji, R.drawable.shape_xinxing,
			R.drawable.shape_yezi, R.drawable.shape_yuanxing, R.drawable.shape_yunduo, R.drawable.shape_zhengfangxing, R.drawable.shape_zuanshi };
	private Context mContext;
	private AssetManager assetManager;
	private LinearLayout shape_btn, rotate_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icon_image_edit);
		mContext = IconImageEditActivity.this;
		initViewAndEvent();
	}

	private void initViewAndEvent() {

		findViewById(R.id.diy_ok_image).setOnClickListener(this);
		String resource_path = getIntent().getStringExtra("resource_path");
		String image_path = getFileStreamPath("temp.png").getAbsolutePath();
		iconImageEditView = (IconImageEditView) findViewById(R.id.iconImageEditView);
		iconImageEditView.setDrawIcon(resource_path, image_path);

		shape_gridview = (GridView) findViewById(R.id.shape_gridview);
		ArrayList<Bitmap> shapes = new ArrayList<Bitmap>();
		for (int i = 0; i < shapeResource.length; i++) {
			shapes.add(DrawableUtils.getBitmap(mContext, shapeResource[i]));
		}
		shape_gridview.setAdapter(new Adapter4Shape(mContext, shapes));
		shape_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String shapeFileName = shapeNames[position];
				if (assetManager == null) {
					assetManager = mContext.getAssets();
				}
				Bitmap bitmap = null;
				try {
					bitmap = DrawableUtils.getBitmap(mContext, assetManager.open("shapes/" + shapeFileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
				iconImageEditView.setShap(bitmap);
				iconImageEditView.invalidate();
			}
		});
		shape_btn = (LinearLayout) findViewById(R.id.shape_btn);
		shape_btn.setOnClickListener(this);
		rotate_btn = (LinearLayout) findViewById(R.id.rotate_btn);
		rotate_btn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.diy_ok_image) {
			Bitmap bitmap = iconImageEditView.creatNewImage();
			Intent intent = new Intent(mContext, DiyActivity.class);
			intent.putExtra("iconByte", DrawableUtils.bitmap2Byte(bitmap));
			setResult(MConstants.REQUEST_CODE_STICKER_EDIT, intent);
			finish();

		} else if (i == R.id.shape_btn) {
			startAnim(shape_gridview);

		} else if (i == R.id.rotate_btn) {
			iconImageEditView.rotate();

		} else {
		}
	}

	public void startAnim(View view) {
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom));
		} else {
			view.setVisibility(View.VISIBLE);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom));
		}

	}

}
