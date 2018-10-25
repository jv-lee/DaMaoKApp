package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/27.
 */
public class ContributeDialog extends BaseDialog implements View.OnClickListener {

	private ContributeClickListener contributeClickListener = null;
	private EditText nameEdit = null;
	private EditText authorEdit = null;
	private EditText contactEdit = null;
	private TextView button_textview = null;
	private TextView tips_textview = null;
	private TextView text_work_author = null;
	private TextView text_work_contact = null;
	private SharedPreferences sp;

	public ContributeDialog(Context context) {
		super(context);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = mContext.getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = mContext.getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_contribute_layout, null);
		view.findViewById(R.id.upload).setOnClickListener(this);
		nameEdit = (EditText) view.findViewById(R.id.edit_text_name);
		authorEdit = (EditText) view.findViewById(R.id.edit_text_author);
		contactEdit = (EditText) view.findViewById(R.id.edit_text_contact);
		button_textview = (TextView) view.findViewById(R.id.button_textview);
		tips_textview = (TextView) view.findViewById(R.id.tips_textview);
		text_work_author = (TextView) view.findViewById(R.id.text_work_author);
		text_work_contact = (TextView) view.findViewById(R.id.text_work_contact);
		
		if(!sp.getString("author_name", "").equals("")){
			authorEdit.setVisibility(View.GONE);
			contactEdit.setVisibility(View.GONE);
			text_work_author.setVisibility(View.GONE);
			text_work_contact.setVisibility(View.GONE);
		}
		
		setAlignTop(true);
		initViews(view);
	}

	@Override
	public void onClick(View v) {
		String name = nameEdit.getText().toString();
		String author = authorEdit.getText().toString().equals("")?sp.getString("author_name", ""):authorEdit.getText().toString();
		String contact = contactEdit.getText().toString().equals("")?sp.getString("author_phone", ""):contactEdit.getText().toString();

		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(author) && !TextUtils.isEmpty(contact)) {
			if (contributeClickListener != null) {
				contributeClickListener.OnContributeClickListener(name, author, contact);
				Log.i("debug", "name-->"+name+"  author-->"+author+"  contact-->"+contact);
				sp.edit().putString("author_name", author).commit();
				sp.edit().putString("author_phone", contact).commit();
			}
			dismiss();
		} else {
			SimpleToast.makeText(mContext, R.string.theme_name_not_null, SimpleToast.LENGTH_SHORT).show();
		}

	}

	/**
	 * dismiss回调
	 */
	@Override
	protected void onDismissed() {
		super.onDismissed();
	}

	public ContributeClickListener getContributeClickListener() {
		return contributeClickListener;
	}

	public void setContributeClickListener(ContributeClickListener contributeClickListener) {
		this.contributeClickListener = contributeClickListener;
	}

	public interface ContributeClickListener {
		public void OnContributeClickListener(String name, String author, String contact);
	}

	public void setTipsTextType(int id) {
		if (id == 5) {
			tips_textview.setText(R.string.upload_tips_1);
			button_textview.setText(R.string.button_upload);
		} else {
			tips_textview.setText(R.string.upload_tips_2);
			button_textview.setText(R.string.button_competition);
		}
	}
}
