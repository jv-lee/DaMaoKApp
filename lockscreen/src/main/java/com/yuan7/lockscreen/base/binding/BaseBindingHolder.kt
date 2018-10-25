package com.yuan7.lockscreen.base.binding

import android.databinding.ViewDataBinding
import android.view.View
import com.chad.library.adapter.base.BaseViewHolder
import com.yuan7.lockscreen.R

/**
 * Created by Administrator on 2018/6/6.
 */
class BaseBindingHolder(view: View) : BaseViewHolder(view) {
    val binding: ViewDataBinding
        get() = itemView.getTag(R.id.BaseQuickAdapter_databinding_support) as ViewDataBinding
}
