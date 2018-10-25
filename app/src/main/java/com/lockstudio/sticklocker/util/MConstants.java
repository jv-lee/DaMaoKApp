package com.lockstudio.sticklocker.util;

import android.os.Environment;

public class MConstants {
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/.StickLocker/";
    //	public static final String USER_PHOTO_PATH = Environment.getExternalStorageDirectory() + "/StickerLock/";
    public static final String USER_PHOTO_PATH = Environment.getExternalStorageDirectory() + "/androidscreen";

    public static final String IMAGECACHE_PATH = FILE_PATH + "imagecache/";
    public static final String DOWNLOAD_PATH = FILE_PATH + "download/";
    public static final String TTF_PATH = FILE_PATH + "font/";
    public static final String THEME_PATH = FILE_PATH + "theme/";
    public static final String nomedia = IMAGECACHE_PATH + ".nomedia";
    public static final String isCloud = ".isCloud";
    public static final String uploaded = ".uploaded";
    public static final String uploading = ".uploading";
    public static final String config = "config.json";
    public static final String fonts = "fonts.json";
    public static final String autor = "autor.txt";

    public static final String UPDATE_APPCODE = "64";

    public static final String DEFAULTIMAGE_PATH = IMAGECACHE_PATH + "DEFAULTIMAGE_PATH";


    //	public static String[] uploadHosts = { "http://a.lockstudio.com/","http://a.opda.com/"};
//	public static String[] hosts = { "http://wzsp.lockstudio.com/", "http://ri.opda.co/"};
    public static String[] uploadHosts = {"http://a.lockstudio.com/"};
    public static String[] hosts = {"http://wzsp.lockstudio.com/"};
    public static final String URL_UPLOADTHEME = "wzsp/uploadimg?json=1";
    public static final String URL_PRAISE = "praise/add";
    public static final String URL_GETTHEME = "MasterLockNew/fileupload";
    public static final String URL_GETNEWTHEME = "MasterLockNew/fileuploadtime";
    public static final String URL_GETNEWTHEME2 = "MasterLockNew/fileuploadtimeup";
    public static final String URL_GETNEWTHEME3 = "MasterLockNew/fileuploadcul";
    public static final String URL_GETSTICKER = "MasterLockNew/finish";
    public static final String URL_GETSTICKER_WORD = "MasterLockNew/word";
    public static final String URL_GETWALLPAPER = "MasterLockNew/fontlocknew";
    public static final String URL_GETWALLPAPER_DIY = "MasterLockNew/selected";
    public static final String URL_GETDAILYTEXT = "/wzsponeword/index";
    public static final String URL_GETWALLPAPER_ZONE = "MasterLockNew/typelistnew";
    public static final String URL_GETTTF = "fontwzsp/changelist51";
    public static final String URL_GETBOON = "welfarebig/index";
    public static final String ACTION_UPDATE_LOCAL_THEME = "action.update.local.theme";
    public static final String ACTION_LOCK_NOW = "com.lockstudio.sticklocker.LockNow";
    public static final String ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String ACTION_CLOSE_FAKE_ACTIVITY = "com.lockstudio.sticklocker.CloseFake";
    public static final String ACTION_FAKE_ACTIVITY_CREAT_DONE = "com.lockstudio.sticklocker.FakeActivityDone";
    public static final String ACTION_DISABLE_KEYGUARD = "com.lockstudio.sticklocker.DisableKeyguard";
    public static final String ACTION_ENABLE_KEYGUARD = "com.lockstudio.sticklocker.EnableKeyguard";
    public static final String TIMING_KEY = "gettiming";
    public static final String COUNTDOWN_KEY = "getcountdown";

    public static final String ACTION_UPDATE_IMAGE_PAGE = "ACTION_UPDATE_IMAGE_PAGE";//更新贴纸页面
    public static final String TEMP_IMAGE_PATH = FILE_PATH + "temp_image/";
    public static final String IMAGE_PATH = FILE_PATH + "image/";//存放贴纸的图片目录
    public static final String ACTION_REMOVE_IMAGE = "ACTION_REMOVE_IMAGE";//删除贴纸
    public static final String URL_STICKER_ALL = "deskFontPaster/paster";
    public static final String nomedia_file = ".nomedia";

    public static final String ACTION_UPDATE_LOCAL_WALLPAPER = "ACTION_UPDATE_LOCAL_WALLPAPER";//更新本地壁纸

    public static final int DEFAULT_SCREEN_OFF_TIMEOUT = 120000;
    public static final int MIN_SCREEN_OFF_TIMEOUT = 15000;

//    public static final int MAX_RELOAD = 4;
//    public static final int RELOAD_INTERVAL = 300;

    public static final String WX_APP_ID = "wx0fcc7840090084da";    //cn.opda.android.activity
    //TODO 更新小米新包名的微信分享key
    public static final String WX_APP_ID_1 = "wxfe5f7562dc443574";    // com.lockscreen.wenzisuoping
    public static final String WX_APP_ID_2 = "wx29514d8589eba232";    //com.lockstudio.sticklocker

    public static final String WEIBO_APP_KEY = "306349145";

    public static final String PREFENCEKEY = "lockkey";
    public static final boolean SHOWFA = false;
    public static final boolean SHOWDAMAO = false;
    public static final boolean PUSH_2000_ENABEL = true;


    public static final int REQUEST_CODE_ALBUM = 10000;
    public static final int RESULT_CODE_ALBUM = 10001;
    public static final int REQUEST_CODE_WALLPAPER = 10002;
    public static final int REQUEST_CODE_EDIT = 10003;
    public static final int RESULT_CODE_EDIT = 10004;
    public static final int REQUEST_CODE_STICKER = 10005;
    public static final int REQUEST_CODE_STICKER_EDIT = 10006;
    public static final int REQUEST_CODE_WALLPAPER_LIST = 10007;
    public static final int RESULT_CODE_WALLPAPER_LIST = 10008;
    public static final int REQUEST_CODE_LOCAL_WALLPAPER = 10009;

}
