package com.yuan7.lockscreen.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityLabelListBinding
import com.yuan7.lockscreen.model.entity.LabelEntity
import com.yuan7.lockscreen.view.adapter.LabelListAdapter
import com.yuan7.lockscreen.viewmodel.CategoryLabelViewModel
import java.util.ArrayList

/**
 * Created by Administrator on 2018/6/15.
 */
class LabelListActivity : BaseVMActivity<ActivityLabelListBinding, CategoryLabelViewModel>(R.layout.activity_label_list, CategoryLabelViewModel::class.java) {

    internal var adapter: LabelListAdapter? = null

    override fun bindData(savedInstanceState: Bundle?) {
        if (intent.getIntExtra(Constants.SCREEN, 0) == Constants.SCREEN_LANDSCAPE) {
            adapter = LabelListAdapter(R.layout.item_landscap_list, ArrayList())
        } else {
            adapter = LabelListAdapter(R.layout.item_portrait_list, ArrayList())
        }

        adapter!!.openLoadAnimation()
        adapter!!.setOnLoadMoreListener { viewModel.loadMore() }
        adapter!!.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(this@LabelListActivity, WallpaperActivity::class.java)
                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, (adapter as LabelListAdapter).data[position]))
        }
        adapter!!.setSpanSizeLookup { gridLayoutManager, position ->
            if (adapter!!.getItemViewType(position) == LabelEntity.CONTENT) 1 else 3
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.title = intent.getStringExtra(Constants.CATEGORY_NAME)
        binding.rvContainer.layoutManager = GridLayoutManager(this, 3)
        binding.rvContainer.adapter = adapter
        binding.viewModel = viewModel

        viewModel.setParams(this, this, adapter!!)
        viewModel.observable()
        viewModel.loadFirst()
    }

}