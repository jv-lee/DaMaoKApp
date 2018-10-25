package com.yuan7.lockscreen.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.lockstudio.sticklocker.fragment.ThemeFragment
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.command.LockScreenService
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityMainBinding
import com.yuan7.lockscreen.helper.glide.GlideUtils
import com.yuan7.lockscreen.helper.web.NWebDialog
import com.yuan7.lockscreen.utils.SPUtil
import com.yuan7.lockscreen.view.adapter.UiPagerAdapter
import com.yuan7.lockscreen.view.fragment.FindFragment
import com.yuan7.lockscreen.view.fragment.LandscapeFragment
import com.yuan7.lockscreen.view.fragment.MeFragment
import com.yuan7.lockscreen.view.fragment.PortraitFragment

class MainActivity : BaseVActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun bindData(savedInstanceState: Bundle?) {
        backExitEnable(true)
        startService(Intent(this, LockScreenService::class.java))

        val fragments = arrayOf<Fragment>(PortraitFragment(), LandscapeFragment(), ThemeFragment(), FindFragment.putBack(false), MeFragment.putBack(false))
        binding.vpContainer.adapter = UiPagerAdapter(supportFragmentManager, fragments)
        binding.vpContainer.setNoScroll(true)
        binding.vpContainer.offscreenPageLimit = fragments.size - 1

        binding.rgNav.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_home -> binding.vpContainer.setCurrentItem(0, false)
                R.id.rb_landscape -> binding.vpContainer.setCurrentItem(1, false)
                R.id.rb_find -> binding.vpContainer.setCurrentItem(3, false)
                R.id.rb_my -> binding.vpContainer.setCurrentItem(4, false)
            }
        }

        binding.ivDiy.setOnClickListener {
            binding.rbCenter.isChecked = true
            binding.vpContainer.setCurrentItem(2, false)
        }

        adFunction()
    }

    private fun adFunction() {
        val enable = SPUtil.get(Constants.GAME_ENABLE, false) as Boolean
        if (enable) {
            binding.ivFlow.setOnClickListener {
                NWebDialog(this@MainActivity, "http://engine.tuicoco.com/index/activity?appKey=Q4iHCZRJxk4hovWWcqGM7VP1Va4&adslotId=8478", null).show()
            }
            GlideUtils.loadImage("http://engine.tuicoco.com/index/image?appKey=Q4iHCZRJxk4hovWWcqGM7VP1Va4&adslotId=8478", binding.ivFlow)
        }
    }

}
