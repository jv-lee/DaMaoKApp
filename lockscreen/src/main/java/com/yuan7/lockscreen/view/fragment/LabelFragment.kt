package com.yuan7.lockscreen.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentLabelBinding
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import com.yuan7.lockscreen.viewmodel.LabelViewModel
import java.util.ArrayList
import com.yuan7.lockscreen.model.entity.*
import com.yuan7.lockscreen.view.activity.WallpaperActivity

/**
 * Created by Administrator on 2018/6/11.
 */
class LabelFragment : BaseVMPagerFragment<FragmentLabelBinding, LabelViewModel>(R.layout.fragment_label, LabelViewModel::class.java) {

    internal var adapter: LabelListAdapter? = null

    override fun bindData(savedInstanceState: Bundle?) {
        if (arguments.getInt(Constants.SCREEN) == Constants.SCREEN_LANDSCAPE) {
            adapter = LabelListAdapter(R.layout.item_landscap_list, ArrayList())
        } else {
            adapter = LabelListAdapter(R.layout.item_portrait_list, ArrayList())
        }

        adapter!!.openLoadAnimation()
        adapter!!.setOnLoadMoreListener { viewModel.loadMore() }
        adapter!!.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity, WallpaperActivity::class.java)
                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, (adapter as LabelListAdapter).data[position]))
        }

        binding.rvContainer.layoutManager = GridLayoutManager(activity, 3)
        binding.rvContainer.adapter = adapter
        adapter!!.setSpanSizeLookup { gridLayoutManager, position ->
            if (adapter!!.getItemViewType(position) == LabelEntity.CONTENT) {
                1
            } else {
                3
            }
        }
        adapter!!.setPreLoadNumber(10)

        binding.viewModel = viewModel
        viewModel.setParams(this, this, adapter!!)
        viewModel.observable()
    }

    override fun lazyLoad() {
        viewModel.loadFirst()
    }


    companion object {

        fun getLabelFragment(label: Int, screen: Int): LabelFragment {
            val fragment = LabelFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.LABEL, label)
            bundle.putInt(Constants.SCREEN, screen)

            fragment.arguments = bundle
            return fragment
        }
    }

}