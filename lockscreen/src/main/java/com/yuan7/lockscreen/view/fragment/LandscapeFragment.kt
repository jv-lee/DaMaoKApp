package com.yuan7.lockscreen.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentLandscapeBinding
import com.yuan7.lockscreen.view.activity.FindActivity
import com.yuan7.lockscreen.view.adapter.UiPagerAdapter

/**
 * Created by Administrator on 2018/6/13.
 */
class LandscapeFragment : BaseVPagerFragment<FragmentLandscapeBinding>(R.layout.fragment_landscape) {

    private var fragments: Array<Fragment>? = null
    private var titles: Array<String>? = null

    override fun bindData(savedInstanceState: Bundle?) {
        fragments = arrayOf(LabelFragment.getLabelFragment(Constants.LABEL_HOT, Constants.SCREEN_LANDSCAPE), CategoryFragment.getGategory(Constants.SCREEN_LANDSCAPE), LabelFragment.getLabelFragment(Constants.LABEL_NEW, Constants.SCREEN_LANDSCAPE))
        titles = resources.getStringArray(R.array.tab_landscape)

        binding.ivSearch.setOnClickListener { startActivity(Intent(activity, FindActivity::class.java)) }
    }

    override fun lazyLoad() {
        binding.vpContainer.adapter = UiPagerAdapter(childFragmentManager, fragments!!, titles!!)
        binding.vpContainer.offscreenPageLimit = fragments!!.size - 1
        binding.tab.setupWithViewPager(binding.vpContainer)
    }
}