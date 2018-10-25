package com.yuan7.lockscreen.helper.donwload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/5/28.
 */

public interface DownloadApi {
    @GET
    Call<ResponseBody> retrofitDownload(@Url String path);
}
