package com.lockstudio.sticklocker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.opda.android.activity.R;

public class PluginChooseWordUtils implements OnItemClickListener {
	private Context mContext;
	private View view;
	private ListView choose_word_listview;
	private OnTextChangeListener mOnTextChangeListener;
	private ArrayList<WordInfo> wordInfos = new ArrayList<WordInfo>();

	public PluginChooseWordUtils(Context mContext) {
		this.mContext = mContext;
		view = LayoutInflater.from(mContext).inflate(R.layout.plugin_choose_word_layout, null);
		choose_word_listview = (ListView) view.findViewById(R.id.choose_word_listview);
		choose_word_listview.setOnItemClickListener(this);
		initData();
	}

	private void initData() {

		WordInfo wordInfo = new WordInfo();
		wordInfo.setText("自定义字中字");
		wordInfos.add(wordInfo);
		
		try {
			InputStream inputStream = mContext.getAssets().open("hollowwords");
			int len = 0;
			byte[] buff = new byte[4096];
			StringBuffer sb = new StringBuffer();
			while ((len = inputStream.read(buff)) != -1) {
				sb.append(new String(buff, 0, len));
			}
			inputStream.close();
			String[] strings = sb.toString().split("\n");
			for (String string : strings) {
				wordInfo = new WordInfo();
				wordInfo.setText(string);
				wordInfos.add(wordInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		wordInfos.get(1).setSelected(true);
		WordsAdapter wordsAdapter = new WordsAdapter(mContext, wordInfos);
		choose_word_listview.setAdapter(wordsAdapter);
	}

	public View getView() {
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WordInfo wordInfo = wordInfos.get(position);
		mOnTextChangeListener.textChange(wordInfo.getText());
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
