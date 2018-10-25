package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.jess.ui.TwoWayGridView;
import com.lockstudio.sticklocker.Interface.FontDownloadListener;
import com.lockstudio.sticklocker.adapter.PluginFontAdapter;
import com.lockstudio.sticklocker.model.FontBean;
import com.lockstudio.sticklocker.view.ColorCircle;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar;
import com.lockstudio.sticklocker.view.ColorPickerSeekBar.OnColorSeekBarChangeListener;
import com.lockstudio.sticklocker.view.HollowWordsView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class PluginHollowWordsUtils implements OnClickListener, TwoWayAbsListView.OnScrollListener {

	private Context mContext;
	private RelativeLayout plugin_tab_font;
	private RelativeLayout plugin_tab_size;
	private RelativeLayout plugin_tab_color;
	private RelativeLayout plugin_tab_light;
	private RelativeLayout plugin_tab_offset;
	private LinearLayout tab_font_layout;
	private LinearLayout tab_size_layout;
	private LinearLayout tab_color_layout;
	private LinearLayout tab_light_layout;
	private LinearLayout tab_offset_layout;
	private LinearLayout font_setting_view;
	private LinearLayout size_setting_view;
	private LinearLayout color_setting_view;
	private LinearLayout light_setting_view;
	private LinearLayout offset_setting_view;
	private LinearLayout shadowpicker_layout, colorpicker_layout;

	private TextView font_up_textview, font_down_textview;
	private TextView size_up_textview, size_down_textview;
	private TextView color_up_textview, color_down_textview;
	private TextView shadowcolor_up_textview, shadowcolor_down_textview;

	private ArrayList<ColorCircle> textColorCircles = new ArrayList<ColorCircle>();
	private ArrayList<ColorCircle> textShadowCircles = new ArrayList<ColorCircle>();
	private int[] shadowColors = { 0x00ffffff, 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private int[] colors = { 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private OnPluginSettingChange onPluginSettingChange;
	private TwoWayGridView gv_text_font;

	private SeekBar sb_plugin_size, sb_plugin_offset, sb_plugin_alpha;
	private ColorPickerSeekBar sb_plugin_color;
	private ColorPickerSeekBar sb_plugin_light;

	private float upSize = 1.0f;
	private float downSize = 1.0f;
	private float upMaxSize = 2.0f;
	private float downMaxSize = 2.0f;
	private boolean changeUpSize = true;
	private int upAlpha = 255;
	private int downAlpha = 255;

	private float offset = 0.5f;
	private int upColor = Color.WHITE;
	private int downColor = Color.WHITE;
	private boolean changeUpColor = true;
	private int upShadowColor = Color.WHITE;
	private int downShadowColor = Color.WHITE;
	private boolean changeUpShadowColor = true;
	private View view;
	private PluginFontAdapter fontAdapter;
	private String upFontPath = null, downFontPath = null;
	private boolean changeUpFont = true;

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
				String plugin_fontPath = (String) msg.obj;
				if (!TextUtils.isEmpty(plugin_fontPath) && new File(plugin_fontPath).exists()) {
					if (changeUpFont) {
						upFontPath = plugin_fontPath;
					} else {
						downFontPath = plugin_fontPath;
					}
					onPluginSettingChange.changeFont(upFontPath, downFontPath);
				}
				break;
			case 3:
				if (changeUpFont) {
					upFontPath = null;
				} else {
					downFontPath = null;
				}
				onPluginSettingChange.changeFont(upFontPath, downFontPath);
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

	public PluginHollowWordsUtils(Context mContext) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.plugin_hollowwords_layout, null);
		plugin_tab_font = (RelativeLayout) view.findViewById(R.id.plugin_tab_font);
		plugin_tab_size = (RelativeLayout) view.findViewById(R.id.plugin_tab_size);
		plugin_tab_color = (RelativeLayout) view.findViewById(R.id.plugin_tab_color);
		plugin_tab_light = (RelativeLayout) view.findViewById(R.id.plugin_tab_light);
		plugin_tab_offset = (RelativeLayout) view.findViewById(R.id.plugin_tab_offset);
		tab_font_layout = (LinearLayout) view.findViewById(R.id.tab_font_layout);
		tab_font_layout.setSelected(true);
		tab_size_layout = (LinearLayout) view.findViewById(R.id.tab_size_layout);
		tab_color_layout = (LinearLayout) view.findViewById(R.id.tab_color_layout);
		tab_light_layout = (LinearLayout) view.findViewById(R.id.tab_light_layout);
		tab_offset_layout = (LinearLayout) view.findViewById(R.id.tab_offset_layout);

		font_setting_view = (LinearLayout) view.findViewById(R.id.font_setting_view);
		size_setting_view = (LinearLayout) view.findViewById(R.id.size_setting_view);
		color_setting_view = (LinearLayout) view.findViewById(R.id.color_setting_view);
		light_setting_view = (LinearLayout) view.findViewById(R.id.light_setting_view);
		offset_setting_view = (LinearLayout) view.findViewById(R.id.offset_setting_view);
		font_setting_view.setVisibility(View.VISIBLE);
		gv_text_font = (TwoWayGridView) view.findViewById(R.id.gv_text_font);
		fontAdapter = new PluginFontAdapter(mContext, new ArrayList<FontBean>(), mHandler);
		gv_text_font.setAdapter(fontAdapter);
		gv_text_font.setOnScrollListener(this);

		colorpicker_layout = (LinearLayout) view.findViewById(R.id.colorpicker_layout);
		shadowpicker_layout = (LinearLayout) view.findViewById(R.id.shadowpicker_layout);
		sb_plugin_size = (SeekBar) view.findViewById(R.id.sb_plugin_size);
		sb_plugin_alpha = (SeekBar) view.findViewById(R.id.sb_plugin_alpha);
		sb_plugin_offset = (SeekBar) view.findViewById(R.id.sb_plugin_offset);
		sb_plugin_color = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_color);
		sb_plugin_light = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_light);
		pluginSizeOrColerOrLightListener();
		initPickerView();

		plugin_tab_font.setOnClickListener(this);
		plugin_tab_size.setOnClickListener(this);
		plugin_tab_color.setOnClickListener(this);
		plugin_tab_light.setOnClickListener(this);
		plugin_tab_offset.setOnClickListener(this);

		font_up_textview = (TextView) view.findViewById(R.id.font_up_textview);
		font_down_textview = (TextView) view.findViewById(R.id.font_down_textview);
		size_up_textview = (TextView) view.findViewById(R.id.size_up_textview);
		size_down_textview = (TextView) view.findViewById(R.id.size_down_textview);
		color_up_textview = (TextView) view.findViewById(R.id.color_up_textview);
		color_down_textview = (TextView) view.findViewById(R.id.color_down_textview);
		shadowcolor_up_textview = (TextView) view.findViewById(R.id.shadowcolor_up_textview);
		shadowcolor_down_textview = (TextView) view.findViewById(R.id.shadowcolor_down_textview);
		font_up_textview.setOnClickListener(this);
		font_down_textview.setOnClickListener(this);
		size_up_textview.setOnClickListener(this);
		size_down_textview.setOnClickListener(this);
		color_up_textview.setOnClickListener(this);
		color_down_textview.setOnClickListener(this);
		shadowcolor_up_textview.setOnClickListener(this);
		shadowcolor_down_textview.setOnClickListener(this);

		mRequestHandler.sendEmptyMessage(MSG_REQUEST_URL_JSON);
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
		sb_plugin_size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if(fromUser){
					float plugin_size = size * 1.0f / 100;
					if (plugin_size < HollowWordsView.MIN_SCALE) {
						plugin_size = HollowWordsView.MIN_SCALE;
					}
					if (changeUpSize) {
						upSize = plugin_size;
					} else {
						downSize = plugin_size;
					}
					onPluginSettingChange.changeSize(upSize, downSize);
				}
			}
		});
		sb_plugin_alpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if(fromUser){
					int plugin_alpha = size;
					if (changeUpSize) {
						upAlpha = plugin_alpha;
					} else {
						downAlpha = plugin_alpha;
					}
					onPluginSettingChange.changeAlpha(upAlpha, downAlpha);
				}
			}
		});
		sb_plugin_offset.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int size, boolean fromUser) {
				if(fromUser){
					offset = size * 1.0f / 100;
					onPluginSettingChange.changeOffset(offset);
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
				int plugin_color = color;
				if (changeUpColor) {
					upColor = plugin_color;
				} else {
					downColor = plugin_color;
				}
				onPluginSettingChange.changeColor(upColor, downColor);
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
				int plugin_shadowColor = color;
				if (changeUpShadowColor) {
					upShadowColor = plugin_shadowColor;
				} else {
					downShadowColor = plugin_shadowColor;
				}
				onPluginSettingChange.changeShadowColor(upShadowColor, downShadowColor);
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
				int plugin_color = colors[id - 1];
				if (changeUpColor) {
					upColor = plugin_color;
				} else {
					downColor = plugin_color;
				}
				onPluginSettingChange.changeColor(upColor, downColor);
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
				int plugin_shadowColor = shadowColors[id - 1];
				if (changeUpShadowColor) {
					upShadowColor = plugin_shadowColor;
				} else {
					downShadowColor = plugin_shadowColor;
				}
				onPluginSettingChange.changeShadowColor(upShadowColor, downShadowColor);
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
			offset_setting_view.setVisibility(View.GONE);
			if (!tab_font_layout.isSelected()) {
				tab_font_layout.setSelected(true);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_offset_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_size) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.VISIBLE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			offset_setting_view.setVisibility(View.GONE);
			if (!tab_size_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(true);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_offset_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_color) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.VISIBLE);
			light_setting_view.setVisibility(View.GONE);
			offset_setting_view.setVisibility(View.GONE);
			if (!tab_color_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(true);
				tab_light_layout.setSelected(false);
				tab_offset_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_light) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.VISIBLE);
			offset_setting_view.setVisibility(View.GONE);
			if (!tab_light_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(true);
				tab_offset_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_offset) {
			font_setting_view.setVisibility(View.GONE);
			size_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			offset_setting_view.setVisibility(View.VISIBLE);
			if (!tab_offset_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_size_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
				tab_offset_layout.setSelected(true);
			}

		} else if (i == R.id.font_up_textview) {
			setFontPath(upFontPath, downFontPath, true);

		} else if (i == R.id.font_down_textview) {
			setFontPath(upFontPath, downFontPath, false);

		} else if (i == R.id.color_up_textview) {
			setTextColor(upColor, downColor, true);

		} else if (i == R.id.color_down_textview) {
			setTextColor(upColor, downColor, false);

		} else if (i == R.id.shadowcolor_up_textview) {
			setTextShadowColor(upShadowColor, downShadowColor, true);

		} else if (i == R.id.shadowcolor_down_textview) {
			setTextShadowColor(upShadowColor, downShadowColor, false);

		} else if (i == R.id.size_up_textview) {
			setScale(upMaxSize, upSize, downMaxSize, downSize, true);
			setAlpha(upAlpha, downAlpha);

		} else if (i == R.id.size_down_textview) {
			setScale(upMaxSize, upSize, downMaxSize, downSize, false);
			setAlpha(upAlpha, downAlpha);

		} else {
		}
	}

	public OnPluginSettingChange getOnPluginSettingChange() {
		return onPluginSettingChange;
	}

	public void setOnPluginSettingChange(OnPluginSettingChange onPluginSettingChange) {
		this.onPluginSettingChange = onPluginSettingChange;
	}

	public void setScale(float upMax, float upScale, float downMax, float downScale, boolean changeUpSize) {
		this.changeUpSize = changeUpSize;
		this.upSize = upScale;
		this.downSize = downScale;
		this.upMaxSize = upMax;
		this.downMaxSize = downMax;
		if (changeUpSize) {
			sb_plugin_size.setMax((int) (upMax * 100));
			sb_plugin_size.setProgress((int) (upScale * 100));
			size_up_textview.setSelected(true);
			size_down_textview.setSelected(false);
		} else {
			sb_plugin_size.setMax((int) (downMax * 100));
			sb_plugin_size.setProgress((int) (downScale * 100));
			size_up_textview.setSelected(false);
			size_down_textview.setSelected(true);
		}
	}

	public void setAlpha(int upAlpha, int downAlpha) {
		this.upAlpha = upAlpha;
		this.downAlpha = downAlpha;
		if (changeUpSize) {
			sb_plugin_alpha.setMax(255);
			sb_plugin_alpha.setProgress(upAlpha);
		} else {
			sb_plugin_alpha.setMax(255);
			sb_plugin_alpha.setProgress(downAlpha);
		}
	}

	public void setTextColor(int upColor, int downColor, boolean changeUpColor) {
		this.upColor = upColor;
		this.downColor = downColor;
		this.changeUpColor = changeUpColor;
		if (changeUpColor) {
			color_up_textview.setSelected(true);
			color_down_textview.setSelected(false);
		} else {
			color_up_textview.setSelected(false);
			color_down_textview.setSelected(true);
		}
	}

	public void setTextShadowColor(int upShadowColor, int downShadowColor, boolean changeUpShadowColor) {
		this.upShadowColor = upShadowColor;
		this.downShadowColor = downShadowColor;
		this.changeUpShadowColor = changeUpShadowColor;
		if (changeUpShadowColor) {
			shadowcolor_up_textview.setSelected(true);
			shadowcolor_down_textview.setSelected(false);
		} else {
			shadowcolor_up_textview.setSelected(false);
			shadowcolor_down_textview.setSelected(true);
		}
	}

	public void setOffset(float offset) {
		this.offset = offset;
		sb_plugin_offset.setMax(100);
		sb_plugin_offset.setProgress((int) (offset * 100));
	}

	public void setFontPath(String upFontPath, String downFontPath, boolean changeUpFont) {
		this.upFontPath = upFontPath;
		this.downFontPath = downFontPath;
		this.changeUpFont = changeUpFont;
		if (changeUpFont) {
			if (fontAdapter != null) {
				fontAdapter.setSelectFont(upFontPath);
			}
			font_up_textview.setSelected(true);
			font_down_textview.setSelected(false);
		} else {
			if (fontAdapter != null) {
				fontAdapter.setSelectFont(downFontPath);
			}
			font_up_textview.setSelected(false);
			font_down_textview.setSelected(true);
		}
	}

	public interface OnPluginSettingChange {
		public abstract void changeFont(String upFontPath, String downFontPath);

		public abstract void changeSize(float upSize, float downSize);

		public abstract void changeAlpha(int upAlpha, int downAlpha);

		public abstract void changeColor(int upColor, int downColor);

		public abstract void changeShadowColor(int upShadowColor, int downShadowColor);

		public abstract void changeOffset(float offset);
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

}
