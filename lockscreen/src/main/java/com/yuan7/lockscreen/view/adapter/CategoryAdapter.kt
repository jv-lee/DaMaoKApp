package com.yuan7.lockscreen.view.adapter

import com.yuan7.lockscreen.BR
import com.yuan7.lockscreen.base.binding.BaseBindingAdapter
import com.yuan7.lockscreen.base.binding.BaseBindingHolder
import com.yuan7.lockscreen.model.entity.CategoryEntity

/**
 * Created by Administrator on 2018/5/29.
 */

class CategoryAdapter(layoutResId: Int, data: List<CategoryEntity>?) : BaseBindingAdapter<CategoryEntity>(layoutResId, data) {

    override fun convert(helper: BaseBindingHolder, item: CategoryEntity) {
        if (helper.binding != null) {
            helper.binding.setVariable(BR.category, item)
            helper.binding.executePendingBindings()
        }
    }
}
