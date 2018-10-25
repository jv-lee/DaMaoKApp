package com.yuan7.lockscreen.view.adapter

import com.yuan7.lockscreen.BR
import com.yuan7.lockscreen.base.binding.BaseBindingAdapter
import com.yuan7.lockscreen.base.binding.BaseBindingHolder
import com.yuan7.lockscreen.model.entity.LabelDB

/**
 * Created by Administrator on 2018/5/25.
 */

class LocalListAdapter(layoutResId: Int, data: List<LabelDB>?) : BaseBindingAdapter<LabelDB>(layoutResId, data) {

    override fun convert(helper: BaseBindingHolder, item: LabelDB) {
        if (helper.binding != null) {
            helper.binding.setVariable(BR.label, item)
            helper.binding.executePendingBindings()
        }
    }

    fun removeEntity(entity: LabelDB) {
        for (i in mData.indices) {
            if (mData[i].id == entity.id) {
                mData.removeAt(i)
                notifyDataSetChanged()
            }
        }
    }
}
