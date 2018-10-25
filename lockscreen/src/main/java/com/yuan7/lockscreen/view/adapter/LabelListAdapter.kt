package com.yuan7.lockscreen.view.adapter

import android.widget.FrameLayout
import com.qq.e.ads.nativ.NativeExpressADView
import com.yuan7.lockscreen.BR
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.binding.BaseBindingHolder
import com.yuan7.lockscreen.base.binding.BaseBindingMultiAdapter
import com.yuan7.lockscreen.model.entity.*

/**
 * Created by Administrator on 2018/5/25.
 */

class LabelListAdapter(layoutResId: Int, data: List<LabelEntity>?) : BaseBindingMultiAdapter<LabelEntity>(data) {

    init {
        addItemType(LabelEntity.CONTENT, layoutResId)
        addItemType(LabelEntity.AD, R.layout.item_ad)
    }

    override fun convert(helper: BaseBindingHolder, item: LabelEntity) {
        if (helper.itemViewType == LabelEntity.AD) {
            var layout = helper.getView<FrameLayout>(R.id.fl_ad)
            layout.removeAllViews()
            layout.addView(item.view as NativeExpressADView)
            (item.view as NativeExpressADView).render()
        }
        if (helper.binding != null) {
            helper.binding.setVariable(BR.label, item)
            helper.binding.executePendingBindings()
        }

    }

}
