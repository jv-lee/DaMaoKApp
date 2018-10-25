package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Tommy.VolleyUtil;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lockstudio.sticklocker.view.SimpleToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.opda.android.activity.R;

public class PluginTextEditUtils implements OnClickListener, OnItemClickListener {
	private Context mContext;
	private View view;
	private ListView plug_text_listview1,plug_text_listview2,plug_text_listview3,plug_text_listview4,plug_text_listview5;
	private EditText plugin_edittext;
	private Button button_ok;
	private OnTextChangeListener mOnTextChangeListener;
	private ArrayList<WordInfo> wordInfos = new ArrayList<WordInfo>();
	
	private ViewPager viewPager;//页卡内容
	private TextView textView1,textView2,textView3,textView4,textView5;
	private List<View> views;// Tab页面列表
	private View view1,view2,view3,view4,view5;//各个页卡
	
	private static final int ID_POP = 1;
	private static final int ID_LYRICS = 2;
	private static final int ID_LOVE = 3;
	private static final int ID_INSPIRATIONAL = 4;
	private static final int ID_POETRY = 5;
	private final int MSG_NOTIFY_CHANGED = 100;
	private final int MSG_REQUEST_CACHED_JSON = 101;
	private final int MSG_REQUEST_URL_JSON = 102;

	public PluginTextEditUtils(Context mContext, String text) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.plugin_textedit_layout, null);
		plugin_edittext = (EditText) view.findViewById(R.id.plugin_edittext);
		LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.linearLayout1);
		button_ok = (Button) view.findViewById(R.id.button_ok);
		button_ok.setOnClickListener(this);
		
		viewPager=(ViewPager) view.findViewById(R.id.vPager);
		views=new ArrayList<View>();
		view1=LayoutInflater.from(mContext).inflate(R.layout.plugin_word_list, null);
		view2=LayoutInflater.from(mContext).inflate(R.layout.plugin_word_list, null);
		view3=LayoutInflater.from(mContext).inflate(R.layout.plugin_word_list, null);
		view4=LayoutInflater.from(mContext).inflate(R.layout.plugin_word_list, null);
		view5=LayoutInflater.from(mContext).inflate(R.layout.plugin_word_list, null);
		plug_text_listview1 = (ListView) view1.findViewById(R.id.plug_text_listview);
		plug_text_listview1.setOnItemClickListener(this);
		plug_text_listview2 = (ListView) view2.findViewById(R.id.plug_text_listview);
		plug_text_listview2.setOnItemClickListener(this);
		plug_text_listview3 = (ListView) view3.findViewById(R.id.plug_text_listview);
		plug_text_listview3.setOnItemClickListener(this);
		plug_text_listview4 = (ListView) view4.findViewById(R.id.plug_text_listview);
		plug_text_listview4.setOnItemClickListener(this);
		plug_text_listview5 = (ListView) view5.findViewById(R.id.plug_text_listview);
		plug_text_listview5.setOnItemClickListener(this);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		textView1 = (TextView) view.findViewById(R.id.text_pop);
		textView2 = (TextView) view.findViewById(R.id.text_lyrics);
		textView3 = (TextView) view.findViewById(R.id.text_love);
		textView4 = (TextView) view.findViewById(R.id.text_inspirational);
		textView5 = (TextView) view.findViewById(R.id.text_poetry);
		textView1.setSelected(true);
		LinearLayout word_group_1 = (LinearLayout) view.findViewById(R.id.word_group_1);
		LinearLayout word_group_2 = (LinearLayout) view.findViewById(R.id.word_group_2);
		LinearLayout word_group_3 = (LinearLayout) view.findViewById(R.id.word_group_3);
		LinearLayout word_group_4 = (LinearLayout) view.findViewById(R.id.word_group_4);
		LinearLayout word_group_5 = (LinearLayout) view.findViewById(R.id.word_group_5);
		word_group_1.setOnClickListener(new MyOnClickListener(0));
		word_group_2.setOnClickListener(new MyOnClickListener(1));
		word_group_3.setOnClickListener(new MyOnClickListener(2));
		word_group_4.setOnClickListener(new MyOnClickListener(3));
		word_group_5.setOnClickListener(new MyOnClickListener(4));

		initData(text);
	}
	
	/** 
	 *  viewpage的相关设置   
	 * 头标点击监听 3 */
	private class MyOnClickListener implements OnClickListener{
        private int index=0;
        public MyOnClickListener(int i){
        	index=i;
        }
		public void onClick(View v) {
			viewPager.setCurrentItem(index);			
		}
		
	}
	
	public class MyViewPagerAdapter extends PagerAdapter{
		private List<View> mListViews;
		
		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) 	{	
			container.removeView(mListViews.get(position));
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {			
			 container.addView(mListViews.get(position), 0);
			 return mListViews.get(position);
		}

		@Override
		public int getCount() {			
			return  mListViews.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {			
			return arg0==arg1;
		}
	}

    public class MyOnPageChangeListener implements OnPageChangeListener{
    	
		public void onPageScrollStateChanged(int arg0) {
			
			
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}

		public void onPageSelected(int arg0) {
			Message msg = new Message();
			msg.what = MSG_REQUEST_URL_JSON;
			msg.arg1 = arg0+1;
			mHandler.sendMessage(msg);
			textView1.setSelected(false);
			textView2.setSelected(false);
			textView3.setSelected(false);
			textView4.setSelected(false);
			textView5.setSelected(false);
			switch (arg0) {
			case 0:
				textView1.setSelected(true);
				break;
			case 1:
				textView2.setSelected(true);
				break;
			case 2:
				textView3.setSelected(true);
				break;
			case 3:
				textView4.setSelected(true);
				break;
			case 4:
				textView5.setSelected(true);
				break;
			default:
				break;
			}
		}
    	
    }
    /*****************************************viewpage设置*****************************************************************/

    WordsAdapter wordsAdapter;
	private void initData(String text) {
		wordsAdapter = new WordsAdapter(mContext, wordInfos);
		
		Message msg = new Message();
		msg.what = MSG_REQUEST_URL_JSON;
		msg.arg1 = ID_POP;
		mHandler.sendMessage(msg);

		if (!TextUtils.isEmpty(text)) {
			plugin_edittext.setText(text);
		} else {
			wordsAdapter.notifyDataSetChanged();
			plugin_edittext.setText("");
		}
	}
	
	/**
	 * 数据解析
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			int what = msg.what;
			int id = msg.arg1;
			mHandler.removeMessages(msg.what);
			switch (what) {
			case MSG_NOTIFY_CHANGED:
				wordsAdapter.notifyDataSetChanged();
				break;

			case MSG_REQUEST_URL_JSON:
				wordsAdapter.clearList();
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
					RLog.d("STICKER_DIY", response.toString());
					parseJson(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
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
			jsonObject.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = HostUtil.getUrl(MConstants.URL_GETSTICKER_WORD + "?json=" + jsonObject.toString());
		RLog.i("STICKER_DIY_URL", url);
		return url;
	}

	private void parseJson(JSONObject jsonObject) {
		if (jsonObject.optInt("code") == 200 && jsonObject.has("json")) {
			JSONArray array = jsonObject.optJSONArray("json");
			if (null != array) {
				int count = array.length();
				RLog.d("STICKER_DIY", "array.length=" + count);
				for (int i = 0; i < count; i++) {
					JSONObject js = array.optJSONObject(i);
					String content = js.optString("content");
					if (null != content) {
						WordInfo wordInfo = new WordInfo();
						wordInfo.setText(content);
						wordInfos.add(wordInfo);
					}
				}

				wordsAdapter.setArrayList(wordInfos);
				/*文字选中，显示
				wordInfos.get(0).setSelected(true);
				plugin_edittext.setText("");
				*/
				plug_text_listview1.setAdapter(wordsAdapter);
				plug_text_listview2.setAdapter(wordsAdapter);
				plug_text_listview3.setAdapter(wordsAdapter);
				plug_text_listview4.setAdapter(wordsAdapter);
				plug_text_listview5.setAdapter(wordsAdapter);

				mHandler.sendEmptyMessage(MSG_NOTIFY_CHANGED);
			}
		}
	}
	 /*****************************************数据解析*****************************************************************/

	public View getView() {
		return view;
	}

	@Override
	public void onClick(View v) {
		String text = plugin_edittext.getText().toString();
		if (!TextUtils.isEmpty(text)) {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(plugin_edittext.getWindowToken(), 0);
			mOnTextChangeListener.textChange(text);
		} else {
			SimpleToast.makeText(mContext, R.string.word_not_null, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WordInfo wordInfo = wordInfos.get(position);
		plugin_edittext.setText(wordInfo.getText());
		for (int i = 0; i < wordInfos.size(); i++) {
			wordInfo = wordInfos.get(i);
			if (i == position) {
				wordInfo.setSelected(true);
			} else {
				wordInfo.setSelected(false);
			}
			((WordsAdapter) parent.getAdapter()).notifyDataSetChanged();
		}
	}

	public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
		this.mOnTextChangeListener = onTextChangeListener;
	}

	public interface OnTextChangeListener {
		public void textChange(String text);
	}

	public class WordsAdapter extends BaseAdapter {
		private ArrayList<WordInfo> mWordInfos = new ArrayList<WordInfo>();
		private LayoutInflater inflater;

		public WordsAdapter(Context mContext, ArrayList<WordInfo> wordInfos) {
			this.mWordInfos = wordInfos;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return mWordInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return mWordInfos.get(position);
		}
		
		public void clearList() {
			if (mWordInfos != null) {
				mWordInfos.clear();
				notifyDataSetChanged();
			}
		}
		public void setArrayList(ArrayList<WordInfo> arrayList) {
			this.mWordInfos = arrayList;
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
				convertView = inflater.inflate(R.layout.listview_item_words, parent, false);
				holder.selected_imageview = (ImageView) convertView.findViewById(R.id.selected_imageview);
				holder.word_textview = (TextView) convertView.findViewById(R.id.word_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final WordInfo wordInfo = mWordInfos.get(position);
			if (wordInfo.isSelected()) {
				holder.selected_imageview.setVisibility(View.VISIBLE);
				holder.word_textview.setTextColor(0xffffffff);
			} else {
				holder.selected_imageview.setVisibility(View.INVISIBLE);
				holder.word_textview.setTextColor(0xffbbbbbb);
			}
			holder.word_textview.setText(wordInfo.getText());
			return convertView;
		}

		class ViewHolder {
			public ImageView selected_imageview;
			public TextView word_textview;
		}
	}

	class WordInfo {
		private String text;
		private boolean selected;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}

}
