package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class PluginStatusUtils/* extends BaseActivity */implements OnClickListener, TwoWayAbsListView.OnScrollListener {

	private Context mContext;
	private RelativeLayout plugin_tab_font;
	private RelativeLayout plugin_tab_content;
	private RelativeLayout plugin_tab_color;
	private RelativeLayout plugin_tab_light;
	private LinearLayout tab_font_layout;
	private LinearLayout tab_content_layout;
	private LinearLayout tab_color_layout;
	private LinearLayout tab_light_layout;
	private LinearLayout font_setting_view;
	private RelativeLayout status_setting_view;
	private LinearLayout color_setting_view;
	private LinearLayout light_setting_view;
	private LinearLayout shadowpicker_layout, colorpicker_layout;

	private ArrayList<ColorCircle> textColorCircles = new ArrayList<ColorCircle>();
	private ArrayList<ColorCircle> textShadowCircles = new ArrayList<ColorCircle>();
	private int[] shadowColors = { 0x00ffffff, 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private int[] colors = { 0xffffffff, 0xff000000, 0xfffe0071, 0xffff4800, 0xfff36c60, 0xffffba00, 0xffffff77, 0xff00b3fc, 0xff00d9e1, 0xff00b6a5,
			0xff00e676, 0xffbdf400, 0xff444f89, 0xff9a68f2, 0xffb68cd7, 0xffa1887f, 0xffffe195, 0xff595959, 0xffb3b3b3 };
	private OnPluginSettingChange onPluginSettingChange;

	private TwoWayGridView gv_text_font;

	private ColorPickerSeekBar sb_plugin_color;
	private ColorPickerSeekBar sb_plugin_light;

	private String plugin_text = null;
	private int plugin_color = Color.WHITE;
	private int plugin_shadowColor = Color.WHITE;
	private View view;

	private EditText content;// 定义一个文本输入框
	private PluginFontAdapter fontAdapter;
	private String plugin_fontPath = null;
	private String plugin_fontUrl = null;
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
                        mHandler.sendEmptyMessage(14);
                    }
                    break;
                case 2:
                	Bundle bundle=msg.getData();
    				
    				plugin_fontPath = bundle.getString("fontPath");
    				plugin_fontUrl = bundle.getString("fontUrl");
    				if (!TextUtils.isEmpty(plugin_fontPath) && new File(plugin_fontPath).exists()) {
    					onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
    				}
                    break;
                case 3:
                    plugin_fontPath = null;
                    onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
                    break;
                case 4:
                    plugin_fontPath = (String) msg.obj;
                    onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
                    break;
                case 5:
                    plugin_fontPath = (String) msg.obj;
                    onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
                    break;

                case 14:
                    if (fontAdapter != null) {
                        // fontAdapter.checkTheFont();
                        // TODO
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

	public PluginStatusUtils(Context mContext) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.plugin_status_layout, null);
		plugin_tab_font = (RelativeLayout) view.findViewById(R.id.plugin_tab_font);
		plugin_tab_content = (RelativeLayout) view.findViewById(R.id.plugin_tab_content);
		plugin_tab_color = (RelativeLayout) view.findViewById(R.id.plugin_tab_color);
		plugin_tab_light = (RelativeLayout) view.findViewById(R.id.plugin_tab_light);
		tab_font_layout = (LinearLayout) view.findViewById(R.id.tab_font_layout);
		tab_font_layout.setSelected(true);
		tab_content_layout = (LinearLayout) view.findViewById(R.id.tab_content_layout);
		tab_color_layout = (LinearLayout) view.findViewById(R.id.tab_color_layout);
		tab_light_layout = (LinearLayout) view.findViewById(R.id.tab_light_layout);

		font_setting_view = (LinearLayout) view.findViewById(R.id.font_setting_view);
		status_setting_view = (RelativeLayout) view.findViewById(R.id.status_setting_view);
		color_setting_view = (LinearLayout) view.findViewById(R.id.color_setting_view);
		light_setting_view = (LinearLayout) view.findViewById(R.id.light_setting_view);
		font_setting_view.setVisibility(View.VISIBLE);
		gv_text_font = (TwoWayGridView) view.findViewById(R.id.gv_text_font);
		fontAdapter = new PluginFontAdapter(mContext, new ArrayList<FontBean>(), mHandler);
		gv_text_font.setAdapter(fontAdapter);
		gv_text_font.setOnScrollListener(this);

		colorpicker_layout = (LinearLayout) view.findViewById(R.id.colorpicker_layout);
		shadowpicker_layout = (LinearLayout) view.findViewById(R.id.shadowpicker_layout);
		sb_plugin_color = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_color);
		sb_plugin_light = (ColorPickerSeekBar) view.findViewById(R.id.sb_plugin_light);
		pluginSizeOrColerOrLightListener();
		initPickerView();

		plugin_tab_font.setOnClickListener(this);
		plugin_tab_content.setOnClickListener(this);
		plugin_tab_color.setOnClickListener(this);
		plugin_tab_light.setOnClickListener(this);

		content = (EditText) view.findViewById(R.id.status_text);
		content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
				plugin_text = content.getText().toString();
				onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
			}

			public void afterTextChanged(Editable s) {
			}
		});
		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;
		mRequestHandler.sendMessage(msg);
	}

	public View getView() {
		if (gv_text_font != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				gv_text_font.setScrollX(0);
			}
		}
		return view;
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
		// sb_plugin_size.setOnSeekBarChangeListener(new
		// OnSeekBarChangeListener() {
		//
		// @Override
		// public void onStopTrackingTouch(SeekBar seekBar) {
		//
		// }
		//
		// @Override
		// public void onStartTrackingTouch(SeekBar seekBar) {
		//
		// }
		//
		// @Override
		// public void onProgressChanged(SeekBar seekBar, int size, boolean
		// fromUser) {
		// plugin_text = size;
		// onPluginSettingChange.change(plugin_fontPath, plugin_size,
		// plugin_color, plugin_shadowColor);
		// }
		// });

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
				onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
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
				onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
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
				onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
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
				onPluginSettingChange.change(plugin_fontPath, plugin_fontUrl, plugin_text, plugin_color, plugin_shadowColor);
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
			status_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			if (!tab_font_layout.isSelected()) {
				tab_font_layout.setSelected(true);
				tab_content_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_content) {
			font_setting_view.setVisibility(View.GONE);
			status_setting_view.setVisibility(View.VISIBLE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.GONE);
			if (!tab_content_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_content_layout.setSelected(true);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_color) {
			font_setting_view.setVisibility(View.GONE);
			status_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.VISIBLE);
			light_setting_view.setVisibility(View.GONE);
			if (!tab_color_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_content_layout.setSelected(false);
				tab_color_layout.setSelected(true);
				tab_light_layout.setSelected(false);
			}

		} else if (i == R.id.plugin_tab_light) {
			font_setting_view.setVisibility(View.GONE);
			status_setting_view.setVisibility(View.GONE);
			color_setting_view.setVisibility(View.GONE);
			light_setting_view.setVisibility(View.VISIBLE);
			if (!tab_light_layout.isSelected()) {
				tab_font_layout.setSelected(false);
				tab_content_layout.setSelected(false);
				tab_color_layout.setSelected(false);
				tab_light_layout.setSelected(true);
			}

		} else {
		}

	}

	public OnPluginSettingChange getOnPluginSettingChange() {
		return onPluginSettingChange;
	}

	public void setOnPluginSettingChange(OnPluginSettingChange onPluginSettingChange) {
		this.onPluginSettingChange = onPluginSettingChange;
	}

	public interface OnPluginSettingChange {
		public abstract void change(String fontPath, String fontUrl, String text, int color, int ShadowColor);
	}

	public void initSelectData(String font, int textColor, int shadowColor,String text) {
		this.plugin_fontPath = font;
		this.plugin_color = textColor;
		this.plugin_shadowColor = shadowColor;
		this.plugin_text = text;
		content.setText(plugin_text);

	}
	@Override
	public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
//			if (fontAdapter != null) {
//				fontAdapter.notifyDataSetChanged();
//			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onScroll(TwoWayAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

}
