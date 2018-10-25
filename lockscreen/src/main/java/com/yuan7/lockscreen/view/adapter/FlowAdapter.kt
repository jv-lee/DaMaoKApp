package com.yuan7.lockscreen.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View

import com.yuan7.lockscreen.BR
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.model.entity.SearchEntity
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

/**
 * Created by Administrator on 2018/6/4.
 */

class FlowAdapter(internal var context: Context, datas: List<SearchEntity>) : TagAdapter<SearchEntity>(datas) {

    override fun getView(parent: FlowLayout, position: Int, searchEntity: SearchEntity): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(context), R.layout.item_flow, parent, false)
        binding.setVariable(BR.search, searchEntity)
        binding.executePendingBindings()
        return binding.root
    }
}
