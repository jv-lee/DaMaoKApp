package com.yuan7.lockscreen.view.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVFragment
import com.yuan7.lockscreen.base.BaseVPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentPortraitBinding
import com.yuan7.lockscreen.view.activity.FindActivity
import com.yuan7.lockscreen.view.adapter.UiPagerAdapter

/**
 * A simple [Fragment] subclass.
 */
class PortraitFragment : BaseVPagerFragment<FragmentPortraitBinding>(R.layout.fragment_portrait) {

    private var fragments: Array<Fragment>? = null
    private var titles: Array<String>? = null

    override fun bindData(savedInstanceState: Bundle?) {
        fragments = arrayOf(LabelFragment.getLabelFragment(Constants.LABEL_RECOMMEND, Constants.SCREEN_PORTRAIT), CategoryFragment.getGategory(Constants.SCREEN_PORTRAIT))
        titles = resources.getStringArray(R.array.tab_home)

        binding.ivSearch.setOnClickListener { startActivity(Intent(activity, FindActivity::class.java)) }
    }

    override fun lazyLoad() {
        binding.vpContainer.adapter = UiPagerAdapter(childFragmentManager, fragments!!, titles!!)
        binding.vpContainer.offscreenPageLimit = fragments!!.size - 1
        binding.tab.setupWithViewPager(binding.vpContainer)
    }

}
