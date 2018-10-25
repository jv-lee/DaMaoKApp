package com.yuan7.lockscreen.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentMeBinding
import com.yuan7.lockscreen.view.activity.LocalActivity
import com.yuan7.lockscreen.view.adapter.UiPagerAdapter

/**
 * Created by Administrator on 2018/6/14.
 */
class MeFragment : BaseVPagerFragment<FragmentMeBinding>(R.layout.fragment_me) {

    var fragments: Array<Fragment>? = null
    var titles: Array<String>? = null

    override fun bindData(savedInstanceState: Bundle?) {
        fragments = arrayOf(LocalFragment.getLocalFragment(Constants.SCREEN_LANDSCAPE), LocalFragment.getLocalFragment(Constants.SCREEN_PORTRAIT))
        titles = resources.getStringArray(R.array.tab_me)

        binding.isBack = arguments.getBoolean(IS_BACK)
        binding.ivBack.setOnClickListener { activity.finish() }

        if (activity is LocalActivity) {
            lazyLoad()
        }
    }

    override fun lazyLoad() {
        binding.vpContainer.adapter = UiPagerAdapter(childFragmentManager, fragments!!, titles!!)
        binding.vpContainer.offscreenPageLimit = fragments!!.size - 1
        binding.tab.setupWithViewPager(binding.vpContainer)
    }


    companion object {
        val IS_BACK = "is_back"
        fun putBack(flag: Boolean): MeFragment {
            var fragment = MeFragment()
            var bundle = Bundle()

            bundle.putBoolean(IS_BACK, flag)
            fragment.arguments = bundle
            return fragment
        }
    }
}