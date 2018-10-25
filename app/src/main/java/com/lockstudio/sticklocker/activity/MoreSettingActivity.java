package com.lockstudio.sticklocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lockstudio.sticklocker.base.BaseFragmentActivity;
import com.lockstudio.sticklocker.util.CustomEventCommit;
import com.lockstudio.sticklocker.util.RLog;

import cn.opda.android.activity.R;

public class MoreSettingActivity extends BaseFragmentActivity {

	private static final String TAG = "V5_FRAGMENT_MORE";
	private SharedPreferences sp;
	private ImageView ic_more_tutorial_new;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_more);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			sp=mContext.getSharedPreferences("default.cfg", Context.MODE_MULTI_PROCESS);
		}else {
			sp = mContext.getSharedPreferences("default.cfg", Context.MODE_PRIVATE);
		}
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ImageView more_setting = (ImageView) findViewById(R.id.more_setting);
		ImageView more_help = (ImageView) findViewById(R.id.more_help);
		ImageView more_feedback = (ImageView) findViewById(R.id.more_feedback);
		ImageView more_about = (ImageView) findViewById(R.id.more_about);
		more_setting.setOnClickListener(new MoreClickListener());
		more_help.setOnClickListener(new MoreClickListener());
		more_feedback.setOnClickListener(new MoreClickListener());
		more_about.setOnClickListener(new MoreClickListener());

		RelativeLayout more_activities = (RelativeLayout) findViewById(R.id.more_activities);
		RelativeLayout more_tutorial = (RelativeLayout) findViewById(R.id.more_tutorial);
		RelativeLayout more_community = (RelativeLayout) findViewById(R.id.more_community);
		RelativeLayout more_person = (RelativeLayout) findViewById(R.id.more_person);
		RelativeLayout more_recommended = (RelativeLayout) findViewById(R.id.more_recommended);
//		RelativeLayout more_competition = (RelativeLayout) findViewById(R.id.more_competition);
//		RelativeLayout more_find = (RelativeLayout) findViewById(R.id.more_find);
		
		//Gavan 去除设置中美化，福利，应用推荐
		more_activities.setVisibility(View.GONE);
		more_tutorial.setVisibility(View.GONE);
		more_recommended.setVisibility(View.GONE);
		
		ic_more_tutorial_new=(ImageView)findViewById(R.id.ic_more_tutorial_new);
		
		more_activities.setOnClickListener(new MoreClickListener());
		more_tutorial.setOnClickListener(new MoreClickListener());
		more_community.setOnClickListener(new MoreClickListener());
		more_person.setOnClickListener(new MoreClickListener());
		more_recommended.setOnClickListener(new MoreClickListener());
//		more_competition.setOnClickListener(new MoreClickListener());
		
//		if(MConstants.SHOWDAMAO){
//			more_find.setVisibility(View.VISIBLE);
//			more_find.setOnClickListener(new MoreClickListener());
//		}
		
		if(!sp.getBoolean("moreTutorialTextview", false)){
			ic_more_tutorial_new.setVisibility(View.VISIBLE);
		}
	}

	private class MoreClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.more_setting) {
				Intent i0 = new Intent(mActivity, SettingActivity.class);
				mActivity.startActivity(i0);

			} else if (i == R.id.more_help) {
				Intent intent2 = new Intent(mActivity, WebviewActivity.class);
				intent2.putExtra("title", "faq");
				intent2.putExtra("url", "http://mp.weixin.qq.com/s?__biz=MzAxODMyMzI3OA==&mid=205838294&idx=1&sn=b433aae0d17df38a605f9295acf1c67d#rd");
				startActivity(intent2);

			} else if (i == R.id.more_feedback) {
				try {
					mActivity.overridePendingTransition(R.anim.activity_in, 0);
				} catch (Exception e) {
					RLog.e("意见反馈", "" + e.toString());
					e.printStackTrace();
				}

			} else if (i == R.id.more_about) {
				Intent i1 = new Intent(mActivity, AboutActivity.class);
				mActivity.startActivity(i1);

			} else if (i == R.id.more_community) {
				CustomEventCommit.commit(mContext, TAG, "WEISHEQU");

			} else if (i == R.id.more_activities) {
				CustomEventCommit.commit(mContext, TAG, "FULI");
				Intent activities = new Intent(mActivity, WelfareActivity.class);
				mActivity.startActivity(activities);

			} else if (i == R.id.more_tutorial) {
				ic_more_tutorial_new.setVisibility(View.GONE);
				sp.edit().putBoolean("moreTutorialTextview", true).commit();
				CustomEventCommit.commit(mContext, TAG, "JIAOCHENG");
				Intent tutorial = new Intent(mActivity, WebviewActivity.class);
				tutorial.putExtra("title", "美化教程");
				tutorial.putExtra("isJiaocheng", true);
				tutorial.putExtra("url", "http://a.opda.com/wzspCourse/list");
				mActivity.startActivity(tutorial);

			} else if (i == R.id.more_recommended) {
				CustomEventCommit.commit(mContext, TAG, "JINGCAITUIJIAN");
				mActivity.startActivity(new Intent(mActivity, AdListActivity.class));

//			case R.id.more_find:
//				DmLbx.showFindCenter(mContext);
//				break;
//			case R.id.more_competition:
//				CustomEventCommit.commit(mContext, TAG, "美美搭");
//				Intent intent = new Intent(mActivity, WebviewActivity.class);
//				intent.putExtra("title", "美美搭");
//				intent.putExtra("url", "http://m.lockstudio.com/wx/index.html?from=singlemessage&isappinstalled=0");
//				mActivity.startActivity(intent);
//				break;
			} else {
			}
		}

	}

}
