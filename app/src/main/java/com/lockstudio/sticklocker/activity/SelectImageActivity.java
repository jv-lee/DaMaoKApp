package com.lockstudio.sticklocker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lockstudio.launcher.fancy.view.tabpageindicator.TabPageIndicator;
import com.lockstudio.sticklocker.Interface.ImageSelectListener;
import com.lockstudio.sticklocker.application.LockApplication;
import com.lockstudio.sticklocker.base.BaseActivity;
import com.lockstudio.sticklocker.model.LocalImageInfo;
import com.lockstudio.sticklocker.util.ChooseStickerUtils;
import com.lockstudio.sticklocker.util.DensityUtil;
import com.lockstudio.sticklocker.util.DrawableUtils;
import com.lockstudio.sticklocker.util.MConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.opda.android.activity.R;


public class SelectImageActivity extends BaseActivity implements View.OnClickListener, ImageSelectListener {
    private TabPageIndicator indicator;
    private Context mContext;
    private ArrayList<ImageItemPager> pagerList = new ArrayList<ImageItemPager>();
    private ViewPager pager_seletImage;
    private MyPagerAdapter myPagerAdapter;
    private TextView title_bar_left_tv;
    private ArrayList<LocalImageInfo> localImageInfos = new ArrayList<LocalImageInfo>();
    private String selectPath;
    private boolean initOver;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_select_image);
        this.mContext = SelectImageActivity.this;
        initView();

        initData(true);


        LockApplication.getInstance().getConfig().setFrom_id(getIntent().getIntExtra("from", ChooseStickerUtils.FROM_STICKER));
        pager_seletImage.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                indicator.onPageSelected(arg0);
                switchPage(arg0);
                if (initOver) {
                    selectPath = localImageInfos.get(arg0).getPath();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                indicator.onPageScrolled(arg0, arg1, arg2);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                indicator.onPageScrollStateChanged(arg0);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MConstants.ACTION_UPDATE_IMAGE_PAGE);
        registerReceiver(broadcastReceiver, intentFilter);
//		FancyLauncherApplication.getInstance().getActivityStackManager().addActivity(this);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (MConstants.ACTION_UPDATE_IMAGE_PAGE.equals(intent.getAction())) {
                if (!TextUtils.isEmpty(selectPath) && !selectPath.startsWith("image/")) {
                    if (!new File(selectPath).exists()) {
                        selectPath = null;
                        index = 0;
                    }
                }
                initData(false);
            }
        }
    };

    private void initView() {
        pager_seletImage = (ViewPager) findViewById(R.id.pager_seletImage);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        findViewById(R.id.open_album_imageview).setOnClickListener(this);
        title_bar_left_tv = (TextView) findViewById(R.id.title_bar_left_tv);
        title_bar_left_tv.setOnClickListener(this);

    }


    private void initData(boolean first) {
        loadLocal();
        initViewPager();
        myPagerAdapter = new MyPagerAdapter();
        pager_seletImage.setAdapter(myPagerAdapter);
        if (first) {
            indicator.setViewPager(pager_seletImage);
        } else {
            indicator.notifyDataSetChanged();
        }

        // 默认都是第一个出现
        pager_seletImage.setCurrentItem(index);
        indicator.setCurrentItem(index);
        switchPage(index);
    }

    /**
     * 加载本地贴纸
     */
    private void loadLocal() {
        localImageInfos.clear();
        //最近使用贴纸
        LocalImageInfo localImageInfo = new LocalImageInfo();
        Bitmap bitmap = DrawableUtils.getBitmap(mContext, R.drawable.tab_paster_iconpast_focus);
        localImageInfo.setIcon(DrawableUtils.scaleTo(bitmap, DensityUtil.dip2px(mContext, 32), DensityUtil.dip2px(mContext, 32)));
        localImageInfo.setPath(MConstants.TEMP_IMAGE_PATH);
        localImageInfo.setName("最近使用");
        localImageInfos.add(localImageInfo);

        //内置贴纸
        AssetManager assetManager = getAssets();
        try {
            String[] paths = assetManager.list("image");
            for (int i = 0; i < paths.length; i++) {

                localImageInfo = new LocalImageInfo();
                localImageInfo.setName(paths[i]);
                localImageInfo.setPath("image/" + paths[i]);
                localImageInfo.setAssets(true);
                bitmap = DrawableUtils.getBitmap(mContext, assetManager.open("image/" + paths[i] + "/icon.png"));
                localImageInfo.setIcon(DrawableUtils.scaleTo(bitmap, DensityUtil.dip2px(mContext, 32), DensityUtil.dip2px(mContext, 32)));
                localImageInfos.add(localImageInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //已下载的本地贴纸
        File fileDir = new File(MConstants.IMAGE_PATH);
        if (fileDir.exists()) {
            if (fileDir.isDirectory()) {
                File[] files = fileDir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        localImageInfo = new LocalImageInfo();
                        localImageInfo.setName(file.getName());
                        localImageInfo.setPath(file.getAbsolutePath());
                        bitmap = DrawableUtils.getBitmap(mContext, new File(file.getAbsolutePath(), "/icon").getAbsolutePath());
                        if (bitmap != null) {
                            localImageInfo.setIcon(DrawableUtils.scaleTo(bitmap, DensityUtil.dip2px(mContext, 32), DensityUtil.dip2px(mContext, 32)));
                            localImageInfos.add(localImageInfo);
                        }
                    }
                }
            }
        }
    }

    protected void switchPage(int arg0) {
        pagerList.get(arg0).initData();
    }

    private void initViewPager() {
        pagerList.clear();
        initOver = false;
        for (int i = 0; i < localImageInfos.size(); i++) {
            if (localImageInfos.get(i).getPath().equals(selectPath)) {
                index = i;
            }
            if (localImageInfos.get(i).isAssets()) {
                pagerList.add(new ImageItemPager(mContext, localImageInfos.get(i).getPath(), true, this));
            } else {
                pagerList.add(new ImageItemPager(mContext, localImageInfos.get(i).getPath(), this));
            }
        }
        initOver = true;
    }

    public class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return localImageInfos.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position).getRootView());
            return pagerList.get(position).getRootView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return localImageInfos.get(position).getName();
        }

        public Bitmap getBitmap(int position) {
            return localImageInfos.get(position).getIcon();
            //return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.about_circle);
        }

        public Bitmap getBitmap() {
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.about_circle);
            //return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.open_album_imageview) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER);

        }  else if (i == R.id.title_bar_left_tv) {
            finish();

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }

        switch (requestCode) {
            case MConstants.REQUEST_CODE_STICKER:
                String picturePath;
                Uri u = data.getData();
                if (!TextUtils.isEmpty(u.getAuthority())) {
                    Cursor cursor = getContentResolver().query(u, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (null == cursor) {
                        Toast.makeText(this, R.string.resource_not_found, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cursor.moveToFirst();
                    picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                } else {
                    picturePath = u.getPath();
                }

                Intent intent = new Intent(mContext, IconImageEditActivity.class);
                intent.putExtra("resource_path", picturePath);
                startActivityForResult(intent, MConstants.REQUEST_CODE_STICKER_EDIT);
                break;
            case MConstants.REQUEST_CODE_STICKER_EDIT:
                byte[] iconByte = data.getByteArrayExtra("iconByte");
                if (iconByte != null) {
                    Bitmap bitmap = DrawableUtils.byte2Bitmap(mContext, iconByte);
                    DrawableUtils.saveTempImage(bitmap, "image" + System.currentTimeMillis());
                    selectImage(bitmap);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void selectImage(Bitmap bitmap) {
        if (bitmap != null) {
            Intent intent = new Intent(mContext, DiyActivity.class);
            intent.putExtra("iconByte", DrawableUtils.bitmap2Byte(bitmap));
            setResult(MConstants.REQUEST_CODE_STICKER_EDIT, intent);
        }
        finish();
    }
}
