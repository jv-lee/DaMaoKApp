package com.yuan7.lockscreen.view.activity

import android.content.Intent
import android.os.Bundle
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError
import com.yuan7.lockscreen.BuildConfig
import com.yuan7.lockscreen.Config
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVFullActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityWelcomeBinding
import com.yuan7.lockscreen.model.entity.InitEntity
import com.yuan7.lockscreen.model.entity.Response
import com.yuan7.lockscreen.model.entity.ResponseEntity
import com.yuan7.lockscreen.model.service.APIService
import com.yuan7.lockscreen.model.service.EnableService
import com.yuan7.lockscreen.utils.AddressUtil
import com.yuan7.lockscreen.utils.LogUtil
import com.yuan7.lockscreen.utils.ParameterUtil
import com.yuan7.lockscreen.utils.SPUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/8.
 */
class WelcomeActivity : BaseVFullActivity<ActivityWelcomeBinding>(R.layout.activity_welcome) {

    @Inject
    lateinit var enableService: EnableService
    @Inject
    lateinit var service: APIService
    val SKIP_TEXT: String = "点击跳过 %d"
    var adFlag = false
    var configFlag = false
    var time = 0

    override fun bindData(savedInstanceState: Bundle?) {
        val params = HashMap<String, Any>()
        params["id"] = BuildConfig.GAME_ENABLE_ID
        params["imei"] = ParameterUtil.getIMEI(this)
        params["packageName"] = applicationContext.packageName
        params["type"] = 1
        params["serviceProvider"] = AddressUtil.getProviderAddress(this)

        val jsonObject = JSONObject(params)
        enableService.doPostGameEnable(RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
                .enqueue(object : Callback<ResponseEntity> {
                    override fun onFailure(call: Call<ResponseEntity>?, t: Throwable?) {
                        init()
                    }

                    override fun onResponse(call: Call<ResponseEntity>?, response: retrofit2.Response<ResponseEntity>?) {
                        if (response!!.body() != null) {
                            SPUtil.save(Constants.GAME_ENABLE, response!!.body().obj)
                            init()
                        } else {
                            init()
                        }
                    }
                })
    }

    private fun init() {
        loadParams()
        var enable = SPUtil.get(Constants.GAME_ENABLE, false) as Boolean
        if (enable) {
            try {
                SplashAD(this@WelcomeActivity, binding.content, binding.skipView, BuildConfig.gdt_appid, BuildConfig.gdt_splan, object : SplashADListener {
                    override fun onADDismissed() {
                        adFlag = true
                        startNext()
                    }

                    override fun onADPresent() {
                    }

                    override fun onNoAD(p0: AdError?) {
                        LogUtil.i("code:${p0!!.errorCode}  msg:${p0!!.errorMsg}")
                        time = 1000
                        adFlag = true
                        startNext()
                    }

                    override fun onADClicked() {
                    }

                    override fun onADTick(p0: Long) {
                        binding.skipView.text = String.format(SKIP_TEXT, Math.round(p0 / 1000f))
                    }

                }, 3000)
            } catch (e: Exception) {
                time = 1000
                adFlag = true
                startNext()
            }
        } else {
            time = 1000
            adFlag = true
            startNext()
        }
    }

    private fun loadParams() {
        val params = HashMap<String, Any>()
        params["id"] = BuildConfig.GAME_ENABLE_ID_YQ
        params["imei"] = ParameterUtil.getIMEI(this)
        params["packageName"] = applicationContext.packageName
        params["type"] = 0
        params["serviceProvider"] = AddressUtil.getProviderAddress(this)

        val jsonObject = JSONObject(params)
        enableService.doPostGameEnable(RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
                .enqueue(object : Callback<ResponseEntity> {
                    override fun onFailure(call: Call<ResponseEntity>?, t: Throwable?) {
                        initConfig()
                    }

                    override fun onResponse(call: Call<ResponseEntity>?, response: retrofit2.Response<ResponseEntity>?) {
                        if (response!!.body() != null) {
                            SPUtil.save(Constants.GAME_ENABLE_YQ, response!!.body().obj)
                            initConfig()
                        } else {
                            initConfig()
                        }
                    }
                })
    }

    fun initConfig(){
        service!!.init(Config.imei, ParameterUtil.getHeight(this), ParameterUtil.getWidth(this))
                .enqueue(object : Callback<Response<InitEntity>> {
                    override fun onResponse(call: Call<Response<InitEntity>>, response: retrofit2.Response<Response<InitEntity>>) {
                        configFlag = true
                        startNext()
                    }

                    override fun onFailure(call: Call<Response<InitEntity>>, t: Throwable) {
                        configFlag = true
                        startNext()
                    }
                })
    }

    fun startNext() {
        if (!adFlag || !configFlag) {
            return
        }
        Observable.timer(time.toLong(), TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                    finish()
                }
    }

}