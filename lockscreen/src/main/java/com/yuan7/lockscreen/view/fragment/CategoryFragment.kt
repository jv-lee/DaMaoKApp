package com.yuan7.lockscreen.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentCategoryBinding
import com.yuan7.lockscreen.view.activity.LabelListActivity
import com.yuan7.lockscreen.view.adapter.CategoryAdapter
import com.yuan7.lockscreen.viewmodel.CategoryViewModel

/**
 * Created by Administrator on 2018/6/13.
 */
class CategoryFragment : BaseVMPagerFragment<FragmentCategoryBinding, CategoryViewModel>(R.layout.fragment_category, CategoryViewModel::class.java) {

    var adapter: CategoryAdapter? = null

    override fun bindData(savedInstanceState: Bundle?) {
        if (arguments.getInt(Constants.SCREEN) == Constants.SCREEN_PORTRAIT) {
            adapter = CategoryAdapter(R.layout.item_category_portrait_list, ArrayList())
            binding.rvContainer.layoutManager = GridLayoutManager(activity, 3)
        } else {
            adapter = CategoryAdapter(R.layout.item_category_landscap_list, ArrayList())
            binding.rvContainer.layoutManager = GridLayoutManager(activity, 2)
        }

        adapter!!.openLoadAnimation()
        adapter!!.setOnItemClickListener { adapter, view, position ->
            var entity = (adapter as CategoryAdapter).data.get(position)
            startActivity(Intent(activity, LabelListActivity::class.java)
                    .putExtra(Constants.CATEGORY_ID, entity.categoryId)
                    .putExtra(Constants.CATEGORY_NAME, entity.categoryName)
                    .putExtra(Constants.SCREEN, arguments.getInt(Constants.SCREEN)))
        }

        binding.rvContainer.adapter = adapter
        binding.viewModel = viewModel
        viewModel.setParams(this, this, adapter!!)
    }

    override fun lazyLoad() {
        viewModel.observable()
    }

    companion object {
        fun getGategory(screen: Int): CategoryFragment {
            val fragment = CategoryFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.SCREEN, screen)

            fragment.arguments = bundle
            return fragment
        }
    }

}