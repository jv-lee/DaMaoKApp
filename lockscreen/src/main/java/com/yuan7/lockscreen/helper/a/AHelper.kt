package com.yuan7.lockscreen.helper.a

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout

import com.qq.e.ads.banner.ADSize
import com.qq.e.ads.banner.AbstractBannerADListener
import com.qq.e.ads.banner.BannerView
import com.qq.e.ads.interstitial.AbstractInterstitialADListener
import com.qq.e.ads.interstitial.InterstitialAD
import com.qq.e.ads.nativ.NativeExpressAD
import com.qq.e.ads.nativ.NativeExpressADView
import com.qq.e.comm.util.AdError
import com.yuan7.lockscreen.BuildConfig
import com.yuan7.lockscreen.model.entity.LabelEntity
import com.yuan7.lockscreen.utils.LogUtil
import com.yuan7.lockscreen.utils.SizeUtil


/**
 * Created by Administrator on 2017/9/25.
 */

object AHelper {
    //    public static void toEvent(Context context, String id) {
    //        MobclickAgent.onEvent(context, id);
    //    }
    val TAG = "AHelper"

    fun showS(activity: Activity) {
        val intAd = InterstitialAD(activity, BuildConfig.gdt_appid, BuildConfig.gdt_screen)
        intAd.setADListener(object : AbstractInterstitialADListener() {
            override fun onADReceive() {
                LogUtil.i("LoadInterstitialAd onADReceive()")
                intAd.show()
            }

            override fun onNoAD(adError: AdError) {
                LogUtil.e(String.format("LoadInterstitialAd onNoAD, error code: %d, error msg: %s",
                        adError.errorCode, adError.errorMsg))
            }
        })
        intAd.loadAD()
    }

    fun showGDTBanner(activity: Activity) {
        val bannerView = BannerView(activity, ADSize.BANNER, BuildConfig.gdt_appid, BuildConfig.gdt_banner)
        (bannerView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
        bannerView.setRefresh(30)
        bannerView.setADListener(object : AbstractBannerADListener() {
            override fun onNoAD(adError: AdError) {
                LogUtil.e(String.format("Banner onNoAD，eCode = %d, eMsg = %s", adError.errorCode,
                        adError.errorMsg))
            }

            override fun onADReceiv() {
                LogUtil.i("Banner onADReceive")
            }
        })
        val frameLayout = activity.window.decorView.findViewById(android.R.id.content) as FrameLayout
        frameLayout.addView(bannerView)
        bannerView.loadAD()
    }

    fun showGDTBanner(activity: Activity, view: FrameLayout) {
        val bannerView = BannerView(activity, ADSize.BANNER, BuildConfig.gdt_appid, BuildConfig.gdt_banner)
        (bannerView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
        bannerView.setRefresh(30)
        bannerView.setADListener(object : AbstractBannerADListener() {
            override fun onNoAD(adError: AdError) {
                LogUtil.e(String.format("Banner onNoAD，eCode = %d, eMsg = %s", adError.errorCode,
                        adError.errorMsg))
            }

            override fun onADReceiv() {
                LogUtil.i("Banner onADReceive")
            }
        })
        view.addView(bannerView)
        bannerView.loadAD()
    }


    fun initNativeExpressAD(context: Context, frameLayout: FrameLayout) {
        val density = context.resources.displayMetrics.density
        val adSize = com.qq.e.ads.nativ.ADSize((context.resources.displayMetrics.widthPixels / density).toInt(), SizeUtil.dp2px(context, 100f)) // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
        val mADManager = NativeExpressAD(context, adSize, BuildConfig.gdt_appid, BuildConfig.gdt_android, object : NativeExpressAD.NativeExpressADListener {
            override fun onNoAD(adError: AdError) {
                Log.i(TAG, adError.errorCode.toString() + " " + adError.errorMsg)
            }

            override fun onADLoaded(list: List<NativeExpressADView>) {
                if (frameLayout.childCount == 0) {
                    frameLayout.addView(list[0])
                    list[0].render()
                }
            }

            override fun onRenderFail(nativeExpressADView: NativeExpressADView) {
                Log.i(TAG, "onRenderFail: " + nativeExpressADView.toString())
            }

            override fun onRenderSuccess(adView: NativeExpressADView) {
                Log.i(TAG, "onRenderSuccess: " + adView.toString())
            }

            override fun onADExposure(adView: NativeExpressADView) {
                Log.i(TAG, "onADExposure: " + adView.toString())
            }

            override fun onADClicked(adView: NativeExpressADView) {
                Log.i(TAG, "onADClicked: " + adView.toString())
            }

            override fun onADClosed(adView: NativeExpressADView) {
                Log.i(TAG, "onADClosed: " + adView.toString())

            }

            override fun onADLeftApplication(adView: NativeExpressADView) {
                Log.i(TAG, "onADLeftApplication: " + adView.toString())
            }

            override fun onADOpenOverlay(adView: NativeExpressADView) {
                Log.i(TAG, "onADOpenOverlay: " + adView.toString())
            }

            override fun onADCloseOverlay(nativeExpressADView: NativeExpressADView) {
                Log.i(TAG, "onADCloseOverlay: " + nativeExpressADView.toString())
            }
        })
        mADManager.loadAD(1)
    }

    fun getNativeExpressAD(context: Context, call: Call) {
        val density = context.resources.displayMetrics.density
        val adSize = com.qq.e.ads.nativ.ADSize((context.resources.displayMetrics.widthPixels / density).toInt(), SizeUtil.dp2px(context, 100f)) // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
        val mADManager = NativeExpressAD(context, adSize, BuildConfig.gdt_appid, BuildConfig.gdt_android, object : NativeExpressAD.NativeExpressADListener {
            override fun onNoAD(adError: AdError) {
                Log.i(TAG, adError.errorCode.toString() + " " + adError.errorMsg)
                call.responseEntity(null)
            }

            override fun onADLoaded(list: List<NativeExpressADView>) {
                val entity = LabelEntity(LabelEntity.Type.AD)
                entity.view = list[0]
                call.responseEntity(entity)
            }

            override fun onRenderFail(nativeExpressADView: NativeExpressADView) {
                Log.i(TAG, "onRenderFail: " + nativeExpressADView.toString())
            }

            override fun onRenderSuccess(adView: NativeExpressADView) {
                Log.i(TAG, "onRenderSuccess: " + adView.toString())
            }

            override fun onADExposure(adView: NativeExpressADView) {
                Log.i(TAG, "onADExposure: " + adView.toString())
            }

            override fun onADClicked(adView: NativeExpressADView) {
                Log.i(TAG, "onADClicked: " + adView.toString())
            }

            override fun onADClosed(adView: NativeExpressADView) {
                Log.i(TAG, "onADClosed: " + adView.toString())

            }

            override fun onADLeftApplication(adView: NativeExpressADView) {
                Log.i(TAG, "onADLeftApplication: " + adView.toString())
            }

            override fun onADOpenOverlay(adView: NativeExpressADView) {
                Log.i(TAG, "onADOpenOverlay: " + adView.toString())
            }

            override fun onADCloseOverlay(nativeExpressADView: NativeExpressADView) {
                Log.i(TAG, "onADCloseOverlay: " + nativeExpressADView.toString())
            }
        })
        mADManager.loadAD(1)
    }

}
