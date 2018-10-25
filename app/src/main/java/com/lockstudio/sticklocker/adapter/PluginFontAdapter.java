package com.lockstudio.sticklocker.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lockstudio.sticklocker.Interface.FontDownloadListener;
import com.lockstudio.sticklocker.model.FontBean;
import com.lockstudio.sticklocker.util.FontUtils;
import com.lockstudio.sticklocker.util.MConstants;
import com.lockstudio.sticklocker.util.RLog;
import com.lockstudio.sticklocker.view.SimpleToast;
import com.lockstudio.sticklocker.view.SquareWheelProcessView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.opda.android.activity.R;

public class PluginFontAdapter extends BaseAdapter {

	private List<FontBean> lists = new ArrayList<FontBean>();
	private Context mContext;
	private LayoutInflater inflater;
	private Handler mHandler;
	private LruCache<String, Typeface> typefaceLruCache;

	public PluginFontAdapter(Context _context, List<FontBean> _list, Handler _handler) {
		this.lists = _list;
		this.mContext = _context;
		this.mHandler = _handler;
		inflater = LayoutInflater.from(mContext);
		if (Build.VERSION.SDK_INT >= 9) {
			typefaceLruCache = new LruCache<String, Typeface>(40);
		}
	}

	public void setArrayList(List<FontBean> _list) {
		this.lists = _list;
	}

	public void clearList() {
		if (lists != null) {
			lists.clear();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.plugin_font_gridview_item, parent, false);
			holder.select_imageview = (ImageView) convertView.findViewById(R.id.select_imageview);
			holder.tv_font_show = (TextView) convertView.findViewById(R.id.tv_font_show);
			holder.tv_font_show_2 = (TextView) convertView.findViewById(R.id.tv_font_show_2);
			holder.tv_font_size = (TextView) convertView.findViewById(R.id.tv_font_size);
			holder.swp_progress = (SquareWheelProcessView) convertView.findViewById(R.id.swp_progress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final FontBean bean = lists.get(position);
		holder.tv_font_size.setText(bean.getSize());
		if (position % 2 == 0) {
			holder.tv_font_show.setText("文");
			holder.tv_font_show_2.setVisibility(View.GONE);
		} else {
			holder.tv_font_show.setText("A");
			holder.tv_font_show_2.setVisibility(View.VISIBLE);
			holder.tv_font_show_2.setText("a");
		}
		if (bean.isDownloading()) {
			holder.select_imageview.setVisibility(View.GONE);
			holder.tv_font_size.setVisibility(View.VISIBLE);
			holder.swp_progress.setIfDownloaded(false);
		} else {
			if (bean.isDownloaded()) {
				if (bean.isChecked()) {
					holder.select_imageview.setVisibility(View.VISIBLE);
					holder.tv_font_size.setVisibility(View.GONE);
				} else {
					holder.select_imageview.setVisibility(View.GONE);
					holder.tv_font_size.setVisibility(View.VISIBLE);
				}
				holder.swp_progress.setIfDownloaded(true);
				if (position != 0) {
					holder.tv_font_size.setVisibility(View.GONE);
				}
			} else {
				// 进度 0% 对号隐藏
				holder.select_imageview.setVisibility(View.GONE);
				holder.swp_progress.setIfDownloaded(false);
				holder.tv_font_size.setVisibility(View.VISIBLE);
			}
		}
		if (position == 0) {
			holder.tv_font_show.setTypeface(Typeface.DEFAULT_BOLD);
		} else if (position == 1) {
			if (new File(MConstants.TTF_PATH + "en_one.ttf").exists()) {
				Typeface typeface = null;
				if (typefaceLruCache != null) {
					typeface = typefaceLruCache.get("en_one.ttf");
				}
				if (typeface == null) {
					try {
						typeface = Typeface.createFromFile(MConstants.TTF_PATH + "en_one.ttf");
						typefaceLruCache.put("en_one.ttf", typeface);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (typeface != null) {
					holder.tv_font_show.setTypeface(typeface);
					holder.tv_font_show_2.setTypeface(typeface);
				} else {
					holder.tv_font_show.setTypeface(Typeface.DEFAULT_BOLD);
					holder.tv_font_show_2.setTypeface(Typeface.DEFAULT_BOLD);
				}
			} else {
				holder.tv_font_show.setTypeface(Typeface.DEFAULT_BOLD);
				holder.tv_font_show_2.setTypeface(Typeface.DEFAULT_BOLD);
			}
		} else {
			String ttfPath = FontUtils.getFontPath(bean.getShowurl());
			if (new File(ttfPath).exists()) {
				Typeface typeface = null;

				if (typefaceLruCache != null) {
					typeface = typefaceLruCache.get(bean.getShowurl());
				}
				if (typeface == null) {
					try {
						typeface = Typeface.createFromFile(ttfPath);
						typefaceLruCache.put(bean.getShowurl(), typeface);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (typeface != null) {
					holder.tv_font_show.setTypeface(typeface);
					holder.tv_font_show_2.setTypeface(typeface);
				} else {
					holder.tv_font_show.setTypeface(Typeface.DEFAULT_BOLD);
					holder.tv_font_show_2.setTypeface(Typeface.DEFAULT_BOLD);
				}

			} else {
				holder.tv_font_show.setTypeface(Typeface.DEFAULT_BOLD);
				holder.tv_font_show_2.setTypeface(Typeface.DEFAULT_BOLD);
			}
		}

		holder.swp_progress.setProgress(bean.getProgress());
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RLog.i("isDownloaded", bean.isDownloaded());
				RLog.i("isDownloading", bean.isDownloading());
				if (bean.isDownloaded() && !bean.isChecked()) {
					bean.setChecked(true);
					Message msg = new Message();
					if (position == 0) {
						msg.what = 3;
					} else if (position == 1) {
						msg.what = 2;
						Bundle bundle=new Bundle();
						bundle.putString("fontPath", MConstants.TTF_PATH + "en_one.ttf");
						msg.setData(bundle);
					} else {
						msg.what = 2;
						Bundle bundle=new Bundle();
						bundle.putString("fontPath", FontUtils.getFontPath(bean.getDownloadurl()));
						bundle.putString("fontUrl", bean.getDownloadurl());
						msg.setData(bundle);
					}
					mHandler.sendMessage(msg);

					for (int i = 0; i < getCount(); i++) {
						if (i != position) {
							((FontBean) getItem(i)).setChecked(false);
						}
					}
					notifyDataSetChanged();
				} else {
					final Handler xHandler = new Handler() {

						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							switch (msg.what) {
							case 0:
								if (msg.obj.equals(bean.getDownloadurl())) {
									bean.setDownloading(false);
									bean.setDownloaded(true);
									SimpleToast.makeText(mContext, R.string.download_succsed, SimpleToast.LENGTH_SHORT).show();
									notifyDataSetChanged();
								}
								break;
							case 1:
								if (msg.obj.equals(bean.getDownloadurl())) {
									bean.setDownloading(false);
									bean.setDownloaded(false);
									notifyDataSetChanged();
									SimpleToast.makeText(mContext, R.string.download_faild, SimpleToast.LENGTH_SHORT).show();
								}
								break;
							case 2:
								if (msg.obj.equals(holder.swp_progress.getTag())) {
									bean.setProgress(msg.arg1);
									notifyDataSetChanged();
								}
								break;

							default:
								break;
							}
						}
					};

					if (!bean.isDownloading() && !bean.isDownloaded() && bean.getDownloadurl() != null) {
						SimpleToast.makeText(mContext, R.string.download_start, SimpleToast.LENGTH_SHORT).show();
						holder.swp_progress.setTag(bean.getDownloadurl());
						bean.setDownloading(true);
						notifyDataSetChanged();
						new Thread(new Runnable() {

							@Override
							public void run() {
								FontUtils.loadTtf(bean.getDownloadurl(), new FontDownloadListener() {

									@Override
									public void finish(String downloadUrl, String path) {
										Message message = new Message();
										message.obj = downloadUrl;
										message.what = 0;
										xHandler.sendMessage(message);

									}

									@Override
									public void error(String downloadUrl) {
										Message message = new Message();
										message.obj = downloadUrl;
										message.what = 1;
										xHandler.sendMessage(message);
									}

									@Override
									public void downloading(String downloadUrl, int size) {
										Message message = new Message();
										message.what = 2;
										message.arg1 = size;
										message.obj = downloadUrl;
										xHandler.sendMessage(message);
									}
								});

							}
						}).start();
					}
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		public TextView tv_font_show;
		public TextView tv_font_show_2;
		public TextView tv_font_size;
		public SquareWheelProcessView swp_progress;
		public ImageView select_imageview;
	}

	public void setSelectFont(String selectFontPath) {
		if (TextUtils.isEmpty(selectFontPath)) {
			if (lists != null && lists.size() > 0) {
				for (int i = 0; i < lists.size(); i++) {
					FontBean fontBean = lists.get(i);
					if (i == 0) {
						fontBean.setChecked(true);
					} else {
						fontBean.setChecked(false);
					}
				}
			}
			notifyDataSetChanged();
		} else {
			if (lists != null && lists.size() > 0) {
				lists.get(0).setChecked(false);
				for (int i = 1; i < lists.size(); i++) {
					FontBean fontBean = lists.get(i);
					if (TextUtils.isEmpty(fontBean.getDownloadurl())) {
						if (selectFontPath.equals(MConstants.TTF_PATH + "en_one.ttf")) {
							fontBean.setChecked(true);
						} else {
							fontBean.setChecked(false);
						}
					} else {
						if (selectFontPath.equals(FontUtils.getFontPath(fontBean.getDownloadurl()))) {
							fontBean.setChecked(true);
						} else {
							fontBean.setChecked(false);
						}
					}
				}
			}
			notifyDataSetChanged();
		}
	}

}
