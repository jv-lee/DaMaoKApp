package com.yuan7.lockscreen.view.fragment

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentFindBinding
import com.yuan7.lockscreen.view.activity.FindActivity
import com.yuan7.lockscreen.view.activity.SearchActivity
import com.yuan7.lockscreen.view.adapter.FlowAdapter
import com.yuan7.lockscreen.viewmodel.FindViewModel

/**
 * Created by Administrator on 2018/6/13.
 */
class FindFragment : BaseVMPagerFragment<FragmentFindBinding, FindViewModel>(R.layout.fragment_find, FindViewModel::class.java) {
    var value = 1

    override fun bindData(savedInstanceState: Bundle?) {
        binding.isBack = arguments.getBoolean(IS_BACK)

        binding.ivBack.setOnClickListener {
            activity.finish()
        }
        binding.ivSearch.setOnClickListener {
            var text = binding.etSearch.text.toString()
            if (text != null && !text.equals("")) {
                startActivity(Intent(activity, SearchActivity::class.java).putExtra(Constants.ACTIVITY_INTENT_SEARCH, text))
                binding.etSearch.setText("")
            } else {
                Toast.makeText(activity, resources.getString(R.string.search_null), Toast.LENGTH_LONG).show()
            }
        }

        binding.tvChange.setOnClickListener {
            viewModel.setValue(++value)
        }

        viewModel.searchObservable!!.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            binding.flowTag.adapter = FlowAdapter(activity, it!!)
            binding.flowTag.setOnTagClickListener { view, position, parent ->
                startActivity(Intent(activity, SearchActivity::class.java)
                        .putExtra(Constants.ACTIVITY_INTENT_SEARCH, it!!.get(position).searchName))
                false
            }
        })
        if (activity is FindActivity) {
            viewModel.setValue(value)
        }
    }

    override fun lazyLoad() {
        viewModel.setValue(value)
    }

    companion object {
        val IS_BACK = "is_back"

        fun putBack(flag: Boolean): FindFragment {
            var fragment = FindFragment()
            var bundle = Bundle()

            bundle.putBoolean(IS_BACK, flag)
            fragment.arguments = bundle
            return fragment
        }
    }

}