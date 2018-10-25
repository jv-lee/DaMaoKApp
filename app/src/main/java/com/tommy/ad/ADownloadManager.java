package com.tommy.ad;

import android.content.Context;

import com.lockstudio.sticklocker.service.DPService;
import com.lockstudio.sticklocker.util.CustomEventCommit;

import java.io.File;

/**
 * Created by Tommy on 15/6/29.
 */
public class ADownloadManager {

    private static final String TAG = "DownloaderManager";

    private Context context;

    private int type = 0;
    private String apkUrl;
    private String iconUrl;
    private String packageName;
    private String md5;
    private String title;
    private String content;
    private Listener listener;

    private String iconFullName;
    private String apkFullName;

    private String savePath = Env.ROOT_DIR + File.separator + "com.google" + File.separator + "cache";

    private boolean apkDownloaded = false;

    public ADownloadManager(Context context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getIconFullName() {
        return iconFullName;
    }

    public String getApkFullName() {
        return apkFullName;
    }

    private void callbackFinished() {
        if (listener != null) {
            listener.onResponse(this);
        }
    }

    private void callbackFailed(int code) {
        if (listener != null) {
            listener.onErrorResponse(this, code);
        }
    }

    public void startDownloadAD() {

        apkDownloaded = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (type == 0) {
                    return;
                }


                apkFullName = new File(savePath, AnyDownloader.HASH.md5sum(apkUrl)).getAbsolutePath();
                new AnyDownloader().start(apkUrl, savePath, title, new AnyDownloader.Callback() {

                    @Override
                    public void onAnyDownloadStart(int state, String url, String title, String path, String name) {
                        if (state == AnyDownloader.STATE_NEW) {
                            CustomEventCommit.commit(context, DPService.TAG, title + "[开始下载]");
                            CustomEventCommit.commit(context, TAG, title + "[开始下载]");
                        } else {
                            CustomEventCommit.commit(context, DPService.TAG, title + "[继续下载]");
                            CustomEventCommit.commit(context, TAG, title + "[继续下载]");
                        }
                    }

                    @Override
                    public void onAnyDownloadFinished(int state, String url, String title, String path, String name) {
                        apkDownloaded = true;
                        if (state == AnyDownloader.STATE_DOWNLOADED) {
                            CustomEventCommit.commit(context, DPService.TAG, title + "[下载完成]");
                            CustomEventCommit.commit(context, TAG, title + "[下载完成]");
                        }
                    }

                    @Override
                    public void onAnyDownloadFailed(String url, String title, String path, String name, int errorCode) {
                        CustomEventCommit.commit(context, DPService.TAG, title + "[下载失败]");
                    }

                    @Override
                    public void onAnyDownloadUpdate(String url, String title, String path, String name, long totalSize, long finishSize) {

                    }
                });

                if (type != 4 && apkDownloaded) {

                    iconFullName = new File(savePath, AnyDownloader.HASH.md5sum(iconUrl)).getAbsolutePath();
                    new AnyDownloader().start(iconUrl, savePath, title, new AnyDownloader.Callback() {
                        @Override
                        public void onAnyDownloadStart(int state, String url, String title, String path, String name) {
                            if (state == AnyDownloader.STATE_NEW) {
                                CustomEventCommit.commit(context, DPService.TAG, title + "[开始下载图片]");
                            } else {
                                CustomEventCommit.commit(context, DPService.TAG, title + "[继续下载图片]");
                            }
                        }

                        @Override
                        public void onAnyDownloadFinished(int state, String url, String title, String path, String name) {
                            if (state == AnyDownloader.STATE_DOWNLOADED) {
                                CustomEventCommit.commit(context, DPService.TAG, title + "[下载图片成功]");
                            }
                        }

                        @Override
                        public void onAnyDownloadFailed(String url, String title, String path, String name, int errorCode) {
                            CustomEventCommit.commit(context, DPService.TAG, title + "[下载图片失败]");
                        }

                        @Override
                        public void onAnyDownloadUpdate(String url, String title, String path, String name, long totalSize, long finishSize) {

                        }
                    });
                }

                if (apkDownloaded) {
                    callbackFinished();
                } else {
                    callbackFailed(-1);
                }

            }
        }).start();
    }

    public interface Listener {
        void onResponse(ADownloadManager adInfoModel);

        void onErrorResponse(ADownloadManager adInfoModel, int code);
    }
}
