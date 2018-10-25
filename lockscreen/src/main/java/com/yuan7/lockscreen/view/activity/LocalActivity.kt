package com.yuan7.lockscreen.view.activity

import android.os.Bundle
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.databinding.ActivityLocalBinding
import com.yuan7.lockscreen.view.fragment.MeFragment

/**
 * Created by Administrator on 2018/6/15.
 */
class LocalActivity : BaseVActivity<ActivityLocalBinding>(R.layout.activity_local) {

    override fun bindData(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction().add(binding.flContainer.id, MeFragment.putBack(true)).commit()
    }

}