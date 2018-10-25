package com.lockstudio.sticklocker.view;

import android.content.Context;
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
public class EditTextDialog extends BaseDialog implements View.OnClickListener {

	private OnEditTextOkClickListener editTextOkClickListener = null;
	private EditText editText;

	public EditTextDialog(Context context) {
		super(context);
		requestWindowFeature(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_edittext_layout, null);
		editText = (EditText) view.findViewById(R.id.dialog_edit_text);
		view.findViewById(R.id.dialog_button_ok).setOnClickListener(this);
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
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		if (editTextOkClickListener != null) {
			editTextOkClickListener.OnEditTextOkClick(editText.getText().toString());
		}
		dismiss();
	}

	public void setHintText(String string) {
		editText.setHint(string);
	}

	public void setHintText(int strRes) {
		editText.setHint(strRes);
	}

	public void show(String string) {
		editText.setText(string);
		editText.setSelected(true);
		editText.setSelection(string.length());
		show();
	}

	public interface OnEditTextOkClickListener {
		public void OnEditTextOkClick(String string);
	}
}
