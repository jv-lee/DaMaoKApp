package com.yuan7.lockscreen.view.activity

import android.os.Bundle
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.databinding.ActivityFindBinding
import com.yuan7.lockscreen.view.fragment.FindFragment

/**
 * Created by Administrator on 2018/6/15.
 */
class FindActivity : BaseVActivity<ActivityFindBinding>(R.layout.activity_find) {

    override fun bindData(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction().add(binding.flContainer.id, FindFragment.putBack(true)).commit()
    }

}