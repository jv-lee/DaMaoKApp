package com.yuan7.lockscreen.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentSearchBinding
import com.yuan7.lockscreen.view.activity.WallpaperActivity
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import com.yuan7.lockscreen.viewmodel.SearchLabelViewModel
import java.util.ArrayList

/**
 * Created by Administrator on 2018/6/15.
 */
class SearchLabelFragment : BaseVMPagerFragment<FragmentSearchBinding, SearchLabelViewModel>(R.layout.fragment_search, SearchLabelViewModel::class.java) {

    var adapter: LabelListAdapter? = null

    override fun bindData(savedInstanceState: Bundle?) {
        if (arguments.getInt(Constants.SCREEN) == Constants.SCREEN_LANDSCAPE) {
            adapter = LabelListAdapter(R.layout.item_landscap_list, ArrayList())
        } else {
            adapter = LabelListAdapter(R.layout.item_portrait_list, ArrayList())
        }

        adapter!!.openLoadAnimation()
        adapter!!.setOnLoadMoreListener { viewModel.loadMore() }
        adapter!!.setOnItemChildClickListener { adapter, view, position ->
            startActivity(Intent(activity,WallpaperActivity::class.java)
                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, (adapter as LabelListAdapter).data[position]))
        }

        binding.rvContainer.layoutManager = GridLayoutManager(activity, 3)
        binding.rvContainer.adapter = adapter
        binding.viewModel = viewModel

        viewModel.setParams(this, this, adapter!!)
        viewModel.observable()
    }

    override fun lazyLoad() {
        viewModel.loadFirst()
    }

    fun findSearch() {
        viewModel.loadFirst()
    }

    companion object {
        fun getSearchLabelFragment(screen: Int): SearchLabelFragment {
            var fragment = SearchLabelFragment()
            var bundle = Bundle()

            bundle.putInt(Constants.SCREEN, screen)
            fragment.arguments = bundle
            return fragment
        }
    }

}