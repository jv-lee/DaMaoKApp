package com.lockstudio.sticklocker.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lockstudio.sticklocker.base.BaseDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.opda.android.activity.R;

/**
 * Created by Tommy on 15/3/19.
 */
public class TimerSettingDialog extends BaseDialog {

	private OnEditTextOkClickListener editTextOkClickListener = null;
	private Button bt_year, bt_mouth, bt_day, bt_hour, bt_minutes, bt_am_pm, bt_cancel, bt_sure;
	private EditText ed_title;

	private final int DATE_DIALOG = 1;
	private final int TIME_DIALOG = 2;
	private String text_mouth, text_day, text_hour;

	Calendar calendar = Calendar.getInstance();
	private StringBuffer sb = new StringBuffer();
	private String currentTime;
	private TextView myTimer_title;

	public TimerSettingDialog(Context context) {
		super(context);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_timer_layout, null);

		myTimer_title = (TextView) view.findViewById(R.id.myTimer_title);

		bt_year = (Button) view.findViewById(R.id.timer_year);
		bt_hour = (Button) view.findViewById(R.id.timer_hour);
		bt_mouth = (Button) view.findViewById(R.id.timer_mouth);
		bt_day = (Button) view.findViewById(R.id.timer_day);
		bt_minutes = (Button) view.findViewById(R.id.timer_minutes);
		bt_am_pm = (Button) view.findViewById(R.id.timer_am_pm);
		bt_cancel = (Button) view.findViewById(R.id.timer_cancel);
		bt_sure = (Button) view.findViewById(R.id.timer_sure);

		ed_title = (EditText) view.findViewById(R.id.timer_title);

		View.OnClickListener dateBtnListener = new BtnOnClickListener(DATE_DIALOG);
		bt_year.setOnClickListener(dateBtnListener);
		bt_mouth.setOnClickListener(dateBtnListener);
		bt_day.setOnClickListener(dateBtnListener);
		View.OnClickListener timeBtnListener = new BtnOnClickListener(TIME_DIALOG);
		bt_hour.setOnClickListener(timeBtnListener);
		bt_minutes.setOnClickListener(timeBtnListener);
		bt_am_pm.setOnClickListener(timeBtnListener);

		bt_sure.setOnClickListener(new MyClick());
		bt_cancel.setOnClickListener(new MyClick());

		bt_year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
		if ((calendar.get(Calendar.MONTH) + 1) < 10) {
			text_mouth = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
		} else {
			text_mouth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		}
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			text_day = "0" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		} else {
			text_day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		}
		bt_mouth.setText(text_mouth);
		bt_day.setText(text_day);
		bt_hour.setText("00");
		bt_minutes.setText("00");

		currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));

		ed_title.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					ed_title.setText("");
					bt_year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
					if ((calendar.get(Calendar.MONTH) + 1) < 10) {
						text_mouth = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
					} else {
						text_mouth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
					}
					if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
						text_day = "0" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
					} else {
						text_day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
					}
					bt_mouth.setText(text_mouth);
					bt_day.setText(text_day);
					text_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
					if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
						text_hour = "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
					} else {
						text_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
					}
					bt_hour.setText(text_hour);
					if (calendar.get(Calendar.MINUTE) < 10) {
						bt_minutes.setText("0" + String.valueOf(calendar.get(Calendar.MINUTE)));
					} else {
						bt_minutes.setText(String.valueOf(calendar.get(Calendar.MINUTE)));
					}
				}
			}
		});
		setAlignTop(true);
		initViews(view);
	}

	/**
	 * 完成、取消按钮单击事件
	 */
	private class MyClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == bt_sure) {
				if (TextUtils.isEmpty(ed_title.getText().toString())) {
					SimpleToast.makeText(mContext, "计时器标题内容不能为空", SimpleToast.LENGTH_SHORT).show();
				} else {
					sb.append(bt_year.getText().toString()).append(bt_mouth.getText().toString()).append(bt_day.getText().toString())
							.append(bt_hour.getText().toString()).append(bt_minutes.getText().toString()).append(bt_am_pm.getText().toString()).toString();

					if (editTextOkClickListener != null) {
						editTextOkClickListener.OnEditTextOkClick(ed_title.getText().toString(), sb.toString());
					}
				}

			}
			if (v == bt_cancel) {
				dismiss();
			}
		}

	}

	/**
	 * 时间选择器
	 */
	private Dialog getDialog(int id) {

		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG:
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
					bt_year.setText(String.valueOf(year));
					if (month + 1 < 10) {
						text_mouth = "0" + String.valueOf(month + 1);
					} else {
						text_mouth = String.valueOf(month + 1);
					}
					if (dayOfMonth < 10) {
						text_day = "0" + String.valueOf(dayOfMonth);
					} else {
						text_day = String.valueOf(dayOfMonth);
					}
					bt_mouth.setText(text_mouth);
					bt_day.setText(text_day);

					checkout();
				}
			};
			dialog = new DatePickerDialog(mContext, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));

			break;
		case TIME_DIALOG:
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timerPicker, int hourOfDay, int minute) {
					if (hourOfDay < 10) {
						text_hour = "0" + String.valueOf(hourOfDay);
					} else {
						text_hour = String.valueOf(hourOfDay);
					}
					bt_hour.setText(text_hour);
					if (minute < 10) {
						bt_minutes.setText("0" + String.valueOf(minute));
					} else {
						bt_minutes.setText(String.valueOf(minute));
					}
					checkout();
				}
			};
			dialog = new TimePickerDialog(mContext, timeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true); // �Ƿ�Ϊ��ʮ����
			break;
		default:
			break;
		}
		return dialog;
	}

	private class BtnOnClickListener implements View.OnClickListener {

		private int dialogId = 0;

		public BtnOnClickListener(int dialogId) {
			this.dialogId = dialogId;
		}

		@Override
		public void onClick(View view) {
			showDialog(dialogId);
		}
	}

	private void showDialog(int dialogId2) {
		getDialog(dialogId2).show();
	}

	public OnEditTextOkClickListener getEditTextOkClickListener() {
		return editTextOkClickListener;
	}

	public void setEditTextOkClickListener(OnEditTextOkClickListener editTextOkClickListener) {
		this.editTextOkClickListener = editTextOkClickListener;
	}

	public void show(String title, String date) {
		ed_title.setText(title);
		ed_title.setSelected(true);
		if (date != null && date.length() == 14) {
			bt_year.setText(date.substring(0, 4));
			bt_mouth.setText(date.substring(4, 6));
			bt_day.setText(date.substring(6, 8));
			bt_hour.setText(date.substring(8, 10));
			bt_minutes.setText(date.substring(10, 12));
			bt_am_pm.setText(date.substring(12, 14));
		}
		checkout();
		show();
	}

	private void checkout() {
		sb.append(bt_year.getText().toString()).append(bt_mouth.getText().toString()).append(bt_day.getText().toString()).append(bt_hour.getText().toString())
				.append(bt_minutes.getText().toString()).append(bt_am_pm.getText().toString()).toString();
		if ((sb.toString()).compareTo(currentTime) > 0) {
			myTimer_title.setText("倒计时");
		} else {
			myTimer_title.setText("正计时");
		}
		sb.delete(0, sb.length());
	}

	public interface OnEditTextOkClickListener {
		public void OnEditTextOkClick(String text, String date);
	}
}
