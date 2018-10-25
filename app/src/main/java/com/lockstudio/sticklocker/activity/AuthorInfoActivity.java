package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lockstudio.sticklocker.base.BaseActivity;

import cn.opda.android.activity.R;

public class AuthorInfoActivity extends BaseActivity implements OnClickListener  {

	private TextView author_view_back,author_view_add;
	private EditText author_edit_name,author_edit_phone;
	private ImageView author_edit_name_delete,author_edit_phone_delete;
	private Button author_button_sure;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_author_info);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			sp = getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		} else {
			sp = getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
//		if(!sp.getBoolean("newGuide", false)){
//			Intent intent = new Intent(mContext, MiuiDetailsActivity.class);
//			intent.putExtra("flag", 8);
//			startActivity(intent);
//			sp.edit().putBoolean("newGuide", true).commit();
//		}
		
//		sp.getString("author_name", "");
//		sp.getString("author_phone", "");
		
		author_view_back=(TextView)findViewById(R.id.author_view_back);
		author_view_add=(TextView)findViewById(R.id.author_view_add);
		author_edit_name=(EditText)findViewById(R.id.author_edit_name);
		author_edit_phone=(EditText)findViewById(R.id.author_edit_phone);
		author_edit_name_delete=(ImageView)findViewById(R.id.author_edit_name_delete);
		author_edit_phone_delete=(ImageView)findViewById(R.id.author_edit_phone_delete);
		author_button_sure=(Button)findViewById(R.id.author_button_sure);
		
		author_view_back.setOnClickListener(this);
		author_view_add.setOnClickListener(this);
		author_edit_name_delete.setOnClickListener(this);
		author_edit_phone_delete.setOnClickListener(this);
		author_button_sure.setOnClickListener(this);
		
		if(sp.getString("author_name", "").equals("")){
			author_view_add.setText("添加");
		}else{
			author_view_add.setText("修改");
			author_edit_name.setText(sp.getString("author_name", ""));
			author_edit_phone.setText(sp.getString("author_phone", ""));
		}
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int i = v.getId();
		if (i == R.id.author_view_back) {
			finish();

		} else if (i == R.id.author_view_add) {
			if (author_view_add.getText().toString().equals("添加")) {
				if (!author_edit_name.getText().toString().equals("") && !author_edit_phone.getText().toString().equals("")) {
					sp.edit().putString("author_name", author_edit_name.getText().toString()).commit();
					sp.edit().putString("author_phone", author_edit_phone.getText().toString()).commit();
					Toast.makeText(mContext, "添加个人信息成功！", 1).show();
					author_view_add.setText("修改");
				} else {
					Toast.makeText(mContext, "作者或者联系方式为空！", 1).show();
				}
			} else if (author_view_add.getText().toString().equals("修改")) {
				author_view_add.setText("取消");
				author_edit_name_delete.setVisibility(View.VISIBLE);
				author_edit_phone_delete.setVisibility(View.VISIBLE);
				author_button_sure.setVisibility(View.VISIBLE);
			} else if (author_view_add.getText().toString().equals("取消")) {
				author_view_add.setText("修改");
				author_edit_name_delete.setVisibility(View.INVISIBLE);
				author_edit_phone_delete.setVisibility(View.INVISIBLE);
				author_button_sure.setVisibility(View.GONE);
			}


		} else if (i == R.id.author_edit_name_delete) {
			author_edit_name.setText("");

		} else if (i == R.id.author_edit_phone_delete) {
			author_edit_phone.setText("");

		} else if (i == R.id.author_button_sure) {
			if (!author_edit_name.getText().toString().equals("") && !author_edit_phone.getText().toString().equals("")) {
				sp.edit().putString("author_name", author_edit_name.getText().toString()).commit();
				sp.edit().putString("author_phone", author_edit_phone.getText().toString()).commit();
				Toast.makeText(mContext, "修改个人信息成功！", 1).show();
				author_view_add.setText("修改");
				author_edit_name_delete.setVisibility(View.INVISIBLE);
				author_edit_phone_delete.setVisibility(View.INVISIBLE);
				author_button_sure.setVisibility(View.GONE);
			} else {
				Toast.makeText(mContext, "作者或者联系方式为空！", 1).show();
			}

		} else {
		}
		
	}

}
