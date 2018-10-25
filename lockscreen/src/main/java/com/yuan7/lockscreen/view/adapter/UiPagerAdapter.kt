package com.yuan7.lockscreen.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter


/**
 * Created by Administrator on 2017/5/17.
 */

class UiPagerAdapter : FragmentStatePagerAdapter {
    private var fragmentList: List<Fragment>? = null
    private var fragments: Array<Fragment>? = null
    private var titles: Array<String>? = null

    constructor(fm: FragmentManager, fragments: Array<Fragment>) : super(fm) {
        this.fragments = fragments
    }

    constructor(fm: FragmentManager, fragmentList: List<Fragment>) : super(fm) {
        this.fragmentList = fragmentList
    }

    constructor(fm: FragmentManager, fragments: Array<Fragment>, titles: Array<String>) : super(fm) {
        this.fragments = fragments
        this.titles = titles
    }

    constructor(fm: FragmentManager, fragmentList: List<Fragment>, titles: Array<String>) : super(fm) {
        this.fragmentList = fragmentList
        this.titles = titles
    }


    override fun getItem(position: Int): Fragment {
        return if (fragmentList == null) fragments!![position] else fragmentList!![position]
    }

    override fun getCount(): Int {
        return if (fragmentList == null) fragments!!.size else fragmentList!!.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (titles != null && titles!!.size > position) {
            titles!![position]
        } else ""
    }
}
