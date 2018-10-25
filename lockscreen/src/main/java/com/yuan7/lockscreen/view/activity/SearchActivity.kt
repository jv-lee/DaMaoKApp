package com.yuan7.lockscreen.view.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivitySearchBinding
import com.yuan7.lockscreen.view.adapter.UiPagerAdapter
import com.yuan7.lockscreen.view.fragment.SearchLabelFragment

/**
 * Created by Administrator on 2018/6/15.
 */
class SearchActivity : BaseVActivity<ActivitySearchBinding>(R.layout.activity_search) {

    var fragments: Array<Fragment>? = null
    var titles: Array<String>? = null

    override fun bindData(savedInstanceState: Bundle?) {
        fragments = arrayOf(SearchLabelFragment.getSearchLabelFragment(Constants.SCREEN_LANDSCAPE), SearchLabelFragment.getSearchLabelFragment(Constants.SCREEN_PORTRAIT))
        titles = resources.getStringArray(R.array.tab_me)

        binding.etSearch.setText(intent.getStringExtra(Constants.ACTIVITY_INTENT_SEARCH))
        binding.etSearch.setSelection(binding.etSearch.text.toString().length)
        binding.vpContainer.adapter = UiPagerAdapter(supportFragmentManager, fragments!!, titles!!)
        binding.vpContainer.offscreenPageLimit = fragments!!.size - 1
        binding.tab.setupWithViewPager(binding.vpContainer)

        binding.ivBack.setOnClickListener { finish() }
        binding.ivSearch.setOnClickListener {
            var text = binding.etSearch.text.toString()
            binding.etSearch.setSelection(binding.etSearch.text.toString().length)
            if (text != null && !text.equals("")) {
                intent.putExtra(Constants.ACTIVITY_INTENT_SEARCH, text)
                (fragments!!.get(0) as SearchLabelFragment).findSearch()
                (fragments!!.get(1) as SearchLabelFragment).findSearch()
            } else {
                Toast.makeText(this, resources.getString(R.string.search_null), Toast.LENGTH_SHORT).show()
            }
        }
    }

}