package com.yuan7.lockscreen.view.activity

import android.os.Bundle
import android.view.WindowManager
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityImageBinding
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.view.fragment.ImageLandscapeFragment
import com.yuan7.lockscreen.view.fragment.ImagePortraitFragment

/**
 * Created by Administrator on 2018/6/15.
 */
class ImageActivity : BaseVActivity<ActivityImageBinding>(R.layout.activity_image) {

    override fun bindData(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if ((intent.getSerializableExtra(Constants.ACTIVITY_INTENT_ENTITY) as LabelDB).type == 1)
            supportFragmentManager.beginTransaction().add(binding.flContainer.id, ImagePortraitFragment()).commit()
        else
            supportFragmentManager.beginTransaction().add(binding.flContainer.id, ImageLandscapeFragment()).commit()
    }

}