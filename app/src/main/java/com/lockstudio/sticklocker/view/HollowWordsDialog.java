package com.lockstudio.sticklocker.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lockstudio.sticklocker.base.BaseDialog;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/19.
 */
public class HollowWordsDialog extends BaseDialog implements View.OnClickListener {

	private OnEditTextOkClickListener editTextOkClickListener = null;
	private EditText upEditText, downEditText;

	public HollowWordsDialog(Context context) {
		super(context);
		requestWindowFeature(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_hollowwords_layout, null);
		upEditText = (EditText) view.findViewById(R.id.up_edittext);
		downEditText = (EditText) view.findViewById(R.id.down_edittext);
		view.findViewById(R.id.dialog_button_ok).setOnClickListener(this);
		view.findViewById(R.id.dialog_button_cancel).setOnClickListener(this);
		setAlignTop(true);
		initViews(view, false);
	}

	public OnEditTextOkClickListener getEditTextOkClickListener() {
		return editTextOkClickListener;
	}

	public void setEditTextOkClickListener(OnEditTextOkClickListener editTextOkClickListener) {
		this.editTextOkClickListener = editTextOkClickListener;
	}

	@Override
	protected void onShow() {
		super.onShow();
	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(upEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(downEditText.getWindowToken(), 0);

		int i = v.getId();
		if (i == R.id.dialog_button_cancel) {
		} else if (i == R.id.dialog_button_ok) {
			String upText = upEditText.getText().toString();
			String downText = downEditText.getText().toString();
			if (!TextUtils.isEmpty(upText) && !TextUtils.isEmpty(downText)) {
				if (editTextOkClickListener != null) {
					editTextOkClickListener.OnEditTextOkClick(upText, downText);
				}
			} else {
				SimpleToast.makeText(mContext, R.string.hollow_words_not_null, SimpleToast.LENGTH_SHORT).show();
			}


		} else {
		}

		dismiss();
	}

	public void show(String upText, String downText) {
		upEditText.setText(upText);

		downEditText.setText(downText);
		show();
	}

	public interface OnEditTextOkClickListener {
		public void OnEditTextOkClick(String upText, String downText);
	}
}
