package com.yuan7.lockscreen.view.activity

import android.os.Bundle
import android.view.WindowManager
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityLockBinding
import com.yuan7.lockscreen.helper.glide.GlideUtils
import com.yuan7.lockscreen.utils.SPUtil
import com.yuan7.lockscreen.utils.TimeUtil
import com.yuan7.lockscreen.view.widget.UnlockView
import java.util.*

/**
 * Created by Administrator on 2018/6/15.
 */
class LockActivity : BaseVActivity<ActivityLockBinding>(R.layout.activity_lock) {

    override fun bindData(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        backCloseEnable(true)

        GlideUtils.getInstance(this).loadImages(SPUtil.get(Constants.LOCK_PATH, ""), binding.ivImage)

        binding.tvTime.text = TimeUtil.milliseconds2String(System.currentTimeMillis(), TimeUtil.DEFAULT_SDF_TIME)
        binding.tvDate.text = TimeUtil.milliseconds2String(System.currentTimeMillis(), TimeUtil.DEFAULT_SDF_DATE)
        binding.tvWeek.text = TimeUtil.getWeek(Date())

        binding.custom.setmCallBack(object : UnlockView.CallBack {
            override fun onUnlocked() {
                finish()
            }
            override fun onSlide(distance: Int) {
            }
        })
    }

}