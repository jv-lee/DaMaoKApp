package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.lockstudio.sticklocker.Interface.FontDownloadListener;
import com.lockstudio.sticklocker.adapter.PluginFontAdapter;
import com.lockstudio.sticklocker.model.FontBean;
import com.lockstudio.sticklocker.model.ShapeInfo;
import com.lockstudio.sticklocker.view.ColorCircle;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar.OnColorSeekBarChangeListener;
import com.lockstudio.sticklocker.view.WordPasswordLockView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class WordPasswordLockUtils implements OnClickListener, TwoWayAbsListView.OnScrollListener, OnItemClickListener {

	private Context mContext;
	private RelativeLayout plugin_tab_font;
	private RelativeLayout plugin_tab_size;
	private RelativeLayout plugin_tab_color;
	private RelativeLayout plugin_tab_light;
	private RelativeLayout plugin_tab_shape;
	private LinearLayout tab_font_layout;
	private LinearLayout tab_size_layout;
	private LinearLayout tab_color_layout;
	private LinearLayout tab_light_layout;
	private LinearLayout tab_shape_layout;
	private LinearLayout font_setting_view;
	private LinearLayout size_setting_view;
	private LinearLayout color_setting_view;
	private LinearLayout light_setting_view;
	private LinearLayout shape_setting_view;
	private LinearLayout shadowpicker_layout, colorpicker_layout;

	private ArrayList<ColorCircle> textColorCircles = new ArrayList<ColorCircle>();
	private ArrayList<ColorCircle> textShadowCircles = new ArrayList<ColorCircle>();
	private int[] shadowColors = { 0x00ffffff, 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private int[] colors = { 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private OnLockSettingChange onLockSettingChange;

	private TwoWayGridView gv_text_font, gv_shape;

	private SeekBar size_seekbar, alpha_seekbar, shape_scale_seekbar;
	private ColorPickerSeekBar sb_plugin_color;
	private ColorPickerSeekBar sb_plugin_light;
	private TextView size_seekbar_info,alpha_seekbar_info;

	private float plugin_size = 1.0f;
	private float shape_scale = 1.0f;
	private int alpha = 255;
	private int plugin_color = Color.WHITE;
	private int plugin_shadowColor = Color.WHITE;
	private View view;
	private PluginFontAdapter fontAdapter;
	private String plugin_fontPath = null;
	private String plugin_fontUrl = null;
	private String shapeName;
	private int shapeId;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_REQUEST_URL_JSON = 102;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ArrayList<FontBean> arrayList = (ArrayList<FontBean>) msg.obj;
				fontAdapter.setArrayList(arrayList);
				fontAdapter.notifyDataSetChanged();
				for (int i = 2; i < arrayList.size(); i++) {
					final FontBean bean = arrayList.get(i);
					new Thread(new Runnable() {

						@Override
						public void run() {
							FontUtils.loadTtf(bean.getShowurl(), new FontDownloadListener() {

								@Override
								public void finish(String url, String path) {
									mHandler.sendEmptyMessage(14);
								}

								@Override
								public void error(String url) {

								}

								@Override
								public void downloading(String url, int size) {

								}
							});
						}
					}).start();
				}
				break;
			case 2:
//				plugin_fontPath = (String) msg.obj;
//				if (!TextUtils.isEmpty(plugin_fontPath) && new File(plugin_fontPath).exists()) {
//					onLockSettingChange.change(plugin_fontPath, plugin_size, plugin_color, plugin_shadowColor, alpha);
//				}
                Bundle bundle=msg.getData();
				plugin_fontPath = bundle.getString("fontPath");
				plugin_fontUrl = bundle.getString("fontUrl");
				if (!TextUtils.isEmpty(plugin_fontPath) && new File(plugin_fontPath).exists()) {
					onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				}
				break;
			case 3:
				plugin_fontPath = null;
				onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				break;

			case 14:
				if (fontAdapter != null) {
					fontAdapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
			return false;
		}
	});

	public WordPasswordLockUtils(Context mContext) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.wordlock_setting_layout, null);
		plugin_tab_font = (RelativeLayout) view.findViewById(R.id.plugin_tab_font);
		plugin_tab_size = (RelativeLayout) view.findViewById(R.id.plugin_tab_size);
		plugin_tab_color = (RelativeLayout) view.findViewById(R.id.plugin_tab_color);
		plugin_tab_light = (RelativeLayout) view.findViewById(R.id.plugin_tab_light);
		plugin_tab_shape = (RelativeLayout) view.findViewById(R.id.plugin_tab_shape);
		tab_font_layout = (LinearLayout) view.findViewById(R.id.tab_font_layout);
		tab_font_layout.setSelected(true);
		tab_size_layout = (LinearLayout) view.findViewById(R.id.tab_size_layout);
		tab_color_layout = (LinearLayout) view.findViewById(R.id.tab_color_layout);
		tab_light_layout = (LinearLayout) view.findViewById(R.id.tab_light_layout);
		tab_shape_layout = (LinearLayout) view.findViewById(R.id.tab_shape_layout);

		font_setting_view = (LinearLayout) view.findViewById(R.id.font_setting_view);
		size_setting_view = (LinearLayout) view.findViewById(R.id.size_setting_view);
		color_setting_view = (LinearLayout) view.findViewById(R.id.color_setting_view);
		light_setting_view = (LinearLayout) view.findViewById(R.id.light_setting_view);
		shape_setting_view = (LinearLayout) view.findViewById(R.id.shape_setting_view);
		font_setting_view.setVisibility(View.VISIBLE);
		gv_text_font = (TwoWayGridView) view.findViewById(R.id.gv_text_font);
		gv_shape = (TwoWayGridView) view.findViewById(R.id.gv_shape);
		fontAdapter = new PluginFontAdapter(mContext, new ArrayList<FontBean>(), mHandler);
		gv_text_font.setAdapter(fontAdapter);
		gv_text_font.setOnScrollListener(this);

		ShapeAdapter shapeAdapter = new ShapeAdapter(mContext);
		shapeAdapter.setArrayList(getShapeList());
		gv_shape.setAdapter(shapeAdapter);
		gv_shape.setOnScrollListener(this);
		gv_shape.setOnItemClickListener(this);

		colorpicker_layout = (LinearLayout) view.findViewById(R.id.colorpicker_layout);
		shadowpicker_layout = (LinearLayout) view.findViewById(R.id.shadowpicker_layout);
		size_seekbar = (SeekBar) view.findViewById(R.id.size_seekbar);
		alpha_seekbar = (SeekBar) view.findViewById(R.id.alpha_seekbar);
		size_seekbar_info = (TextView) view.findViewById(R.id.size_seekbar_info);
		alpha_seekbar_info = (TextView) view.findViewById(R.id.alpha_seekbar_info);
		shape_scale_seekbar = (SeekBar) view.findViewById(R.id.shape_scale_seekbar);
		sb_plugin_color = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_color);
		sb_plugin_light = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_light);
		pluginSizeOrColerOrLightListener();
		initPickerView();

		plugin_tab_font.setOnClickListener(this);
		plugin_tab_size.setOnClickListener(this);
		plugin_tab_color.setOnClickListener(this);
		plugin_tab_light.setOnClickListener(this);
		plugin_tab_shape.setOnClickListener(this);

		mRequestHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
	}

	private ArrayList<ShapeInfo> getShapeList() {
		ArrayList<ShapeInfo> shapeInfos = new ArrayList<ShapeInfo>();
		ShapeInfo shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.translucent_bitmap);
		shapeInfo.setShapeName("null");
		shapeInfos.add(shapeInfo);


		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_yuan);
		shapeInfo.setShapeName("yuan");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_xin);
		shapeInfo.setShapeName("xin");
		shapeInfos.add(shapeInfo);

		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_mao);
		shapeInfo.setShapeName("mao");
		shapeInfos.add(shapeInfo);
		
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_yazi);
		shapeInfo.setShapeName("yazi");
		shapeInfos.add(shapeInfo);
		
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_lingjie);
		shapeInfo.setShapeName("lingjie");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_shuidi);
		shapeInfo.setShapeName("shuidi");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_wubianxing);
		shapeInfo.setShapeName("wubianxing");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_wujiaoxing);
		shapeInfo.setShapeName("wujiaoxing");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_wujiaoxing2);
		shapeInfo.setShapeName("wujiaoxing2");
		shapeInfos.add(shapeInfo);
		
		

		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_xuehua);
		shapeInfo.setShapeName("xuehua");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_xuehua2);
		shapeInfo.setShapeName("xuehua2");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_huabian);
		shapeInfo.setShapeName("huabian");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_huabian2);
		shapeInfo.setShapeName("huabian2");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_huabian3);
		shapeInfo.setShapeName("huabian3");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_huabian4);
		shapeInfo.setShapeName("huabian4");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_huabian5);
		shapeInfo.setShapeName("huabian5");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_liujiaoxing);
		shapeInfo.setShapeName("liujiaoxing");
		shapeInfos.add(shapeInfo);
		
		shapeInfo = new ShapeInfo();
		shapeInfo.setShapeId(R.drawable.word_shape_dunpai);
		shapeInfo.setShapeName("dunpai");
		shapeInfos.add(shapeInfo);
		return shapeInfos;
	}

	private Handler mRequestHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			mHandler.removeMessages(msg.what);
			switch (what) {
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
			RequestQueue requestQueue = VolleyUtil.instance().getRequestQueue();
			if (requestQueue != null) {
				requestQueue.add(jsonObjectRequest);
			}
		}
	}

	private String getRequestUrl() {
		JSONObject jsonObject = new JSONObject();
		return HostUtil.getUrl(MConstants.URL_GETTTF + "?json=" + jsonObject.toString());
	}

	private void parseJson(JSONObject jsonObject) {
		ArrayList<FontBean> arrayList = new ArrayList<FontBean>();
		FontBean fontBean = new FontBean();
		fontBean.setName(mContext.getString(R.string.default_font));
		fontBean.setChecked(true);
		fontBean.setDownloaded(true);
		arrayList.add(fontBean);
		FontBean fontBean1 = new FontBean();
		fontBean1.setName("one");
		fontBean1.setChecked(false);
		fontBean1.setDownloaded(true);
		arrayList.add(fontBean1);
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray arr = jsonObject.optJSONArray("json");
			for (int i = 0; i < arr.length(); i++) {
				FontBean bean = new FontBean();
				JSONObject js = arr.optJSONObject(i);
				bean.setSuoxie(js.optString("shortname"));
				bean.setDownloadurl(js.optString("downloadurl"));
				bean.setName(js.optString("name"));
				bean.setShowurl(js.optString("showurl"));
				bean.setSize(js.optString("size"));

				String ttfPath = FontUtils.getFontPath(bean.getDownloadurl());
				bean.setPath(ttfPath);
				if (!TextUtils.isEmpty(ttfPath) && new File(ttfPath).exists()) {
					bean.setDownloaded(true);
				}
				arrayList.add(bean);
			}
		}
		Message msg = new Message();
		msg.what = 1;
		msg.obj = arrayList;
		mHandler.sendMessage(msg);
	}

	public View getView() {
		if (gv_text_font != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				gv_text_font.setScrollX(0);
			}
		}
		return view;
	}

	private void initPickerView() {
		LayoutParams layoutParams = new LayoutParams(100, 100);
		layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.color_circle_width);
		layoutParams.width = (int) mContext.getResources().getDimension(R.dimen.color_circle_width);
		layoutParams.leftMargin = 20;
		layoutParams.rightMargin = 20;

		for (int i = 0; i < colors.length; i++) {
			ColorCircle colorCircle = new ColorCircle(mContext);
			colorCircle.setLayoutParams(layoutParams);
			if (i == 0) {
				colorCircle.init(colors[i], true);
			} else {
				colorCircle.init(colors[i], false);
			}
			colorCircle.setTag("color");
			colorCircle.setId(i + 1);
			colorCircle.setOnClickListener(this);
			colorpicker_layout.addView(colorCircle);
			textColorCircles.add(colorCircle);
		}

		for (int i = 0; i < shadowColors.length; i++) {
			ColorCircle colorCircle = new ColorCircle(mContext);
			colorCircle.setLayoutParams(layoutParams);
			if (i == 0) {
				colorCircle.init(shadowColors[i], true);
			} else {
				colorCircle.init(shadowColors[i], false);
			}
			colorCircle.setTag("shadow");
			colorCircle.setId(i + 1);
			colorCircle.setOnClickListener(this);
			shadowpicker_layout.addView(colorCircle);
			textShadowCircles.add(colorCircle);
		}
	}

	private void pluginSizeOrColerOrLightListener() {
		size_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if (fromUser) {
					plugin_size = size / 100.0f;
					if (plugin_size < WordPasswordLockView.MIN_SCALE) {
						plugin_size = WordPasswordLockView.MIN_SCALE;
					}
					size_seekbar_info.setText((int)(plugin_size*100)+"%");
					onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				}
			}
		});
		alpha_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if (fromUser) {
					alpha = size;
					alpha_seekbar_info.setText((int)(alpha/255.0f*100)+"%");
					onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				}
			}
		});

		shape_scale_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if (fromUser) {
					 shape_scale = size / 100.0f;
					 if (shape_scale < WordPasswordLockView.SHAPE_MIN_SCALE) {
						 shape_scale = WordPasswordLockView.SHAPE_MIN_SCALE;
					 }
					 onLockSettingChange.changeShape(shapeName, shapeId, shape_scale);
				}
			}
		});
		sb_plugin_color.setOnColorSeekbarChangeListener(new OnColorSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onColorChanged(SeekBar seekBar, int color, boolean b) {
				plugin_color = color;
				onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				for (int i = 0; i < textColorCircles.size(); i++) {
					textColorCircles.get(i).setSelecter(false);
				}
			}
		});

		sb_plugin_light.setOnColorSeekbarChangeListener(new OnColorSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onColorChanged(SeekBar seekBar, int color, boolean b) {
				plugin_shadowColor = color;
				onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				for (int i = 0; i < textShadowCircles.size(); i++) {
					textShadowCircles.get(i).setSelecter(false);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {

		if ("color".equals(v.getTag())) {
			int id = v.getId();
			if (id <= colors.length) {
				plugin_color = colors[id - 1];
				onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				for (int i = 0; i < textColorCircles.size(); i++) {
					if (i == id - 1) {
						textColorCircles.get(i).setSelecter(true);
					} else {
						textColorCircles.get(i).setSelecter(false);
					}
				}
			}
			return;
		}

		if ("shadow".equals(v.getTag())) {
			int id = v.getId();
			if (id <= shadowColors.length) {
				plugin_shadowColor = shadowColors[id - 1];
				onLockSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_size, plugin_color, plugin_shadowColor, alpha);
				for (int i = 0; i < textShadowCircles.size(); i++) {
					if (i == id - 1) {
						textShadowCircles.get(i).setSelecter(true);
					} else {
						textShadowCircles.get(i).setSelecter(false);
					}
				}
			}
			return;
		}

		int i = v.getId();
		if (i == R.id.plugin_tab_font) {
			font_setting_view.setVisibility(View.VISIBLE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			shape_setting_view.setVisibility(View.GONE);
			if (!tab_font_layout.isSelected()) {
				tab_font_layout.setSelected(true);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_shape_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_size) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.VISIBLE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			shape_setting_view.setVisibility(View.GONE);
			if (!tab_size_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(true);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_shape_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_color) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.VISIBLE);
			light_setting_view.setVisibility(View.GONE);
			shape_setting_view.setVisibility(View.GONE);
			if (!tab_color_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(true);
				tab_light_layout.setSelected(false);
				tab_shape_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_light) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.VISIBLE);
			shape_setting_view.setVisibility(View.GONE);
			if (!tab_light_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(true);
				tab_shape_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_shape) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			shape_setting_view.setVisibility(View.VISIBLE);
			if (!tab_shape_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_shape_layout.setSelected(true);
			}

		} else {
		}

	}

	public OnLockSettingChange getOnLockSettingChange() {
		return onLockSettingChange;
	}

	public void setOnLockSettingChange(OnLockSettingChange onLockSettingChange) {
		this.onLockSettingChange = onLockSettingChange;
	}

	public void setScale(float max, float mScale) {
		this.plugin_size = mScale;
		size_seekbar.setMax((int) (max * 100));
		size_seekbar.setProgress((int) (mScale * 100));
	}
	
	public void setShapeScale(float max, float mScale) {
		this.shape_scale = mScale;
		shape_scale_seekbar.setMax((int) (max * 100));
		shape_scale_seekbar.setProgress((int) (mScale * 100));
	}
	public void setShape(String shapeName, int shapeId) {
		this.shapeName = shapeName;
		this.shapeId = shapeId;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
		alpha_seekbar.setMax(255);
		alpha_seekbar.setProgress(alpha);
	}

	public void initSelectData(String fontPath, int color, int shadowColor) {
		this.plugin_fontPath = fontPath;
		this.plugin_color = color;
		this.plugin_shadowColor = shadowColor;
	}

	public interface OnLockSettingChange {
		public abstract void change(String fontPath, String fontUrl, float size, int color, int shadowColor, int alpha);
		public abstract void changeShape(String shapeName, int shapeId, float shapeScale);
	}

	@Override
	public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
			// if (fontAdapter != null) {
			// fontAdapter.notifyDataSetChanged();
			// }
			break;

		default:
			break;
		}

	}

	@Override
	public void onScroll(TwoWayAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	class ShapeAdapter extends BaseAdapter {
		private ArrayList<ShapeInfo> arrayList = new ArrayList<ShapeInfo>();
		private LayoutInflater inflater;

		public ShapeAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void setArrayList(ArrayList<ShapeInfo> arrayList) {
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.plugin_shape_gridview_item, parent, false);
				holder.shape_imageview = (ImageView) convertView.findViewById(R.id.shape_imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(position!=0){
				ShapeInfo shapeInfo = (ShapeInfo) getItem(position);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					holder.shape_imageview.setBackground(mContext.getResources().getDrawable(shapeInfo.getShapeId()));
				} else {
					holder.shape_imageview.setBackgroundDrawable(mContext.getResources().getDrawable(shapeInfo.getShapeId()));
				}
			}else{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					holder.shape_imageview.setBackground(mContext.getResources().getDrawable(R.drawable.word_shape_null));
				} else {
					holder.shape_imageview.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.word_shape_null));
				}
			}

			return convertView;
		}

		class ViewHolder {
			public ImageView shape_imageview;
		}

	}

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
		ShapeInfo shapeInfo = (ShapeInfo) parent.getItemAtPosition(position);
		shapeName = shapeInfo.getShapeName();
		shapeId = shapeInfo.getShapeId();
		onLockSettingChange.changeShape(shapeName, shapeId, shape_scale);
	}

}
