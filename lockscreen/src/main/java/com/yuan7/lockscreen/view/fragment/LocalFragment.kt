package com.yuan7.lockscreen.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMPagerFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentLocalBinding
import com.yuan7.lockscreen.helper.rx.EventBase
import com.yuan7.lockscreen.helper.rx.RxBus
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.view.activity.WallpaperLocalActivity
import com.yuan7.lockscreen.view.adapter.LocalListAdapter
import com.yuan7.lockscreen.viewmodel.LocalViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Administrator on 2018/6/15.
 */
class LocalFragment : BaseVMPagerFragment<FragmentLocalBinding, LocalViewModel>(R.layout.fragment_local, LocalViewModel::class.java) {

    var observable: Observable<EventBase>? = null
    var adapter: LocalListAdapter? = null

    override fun bindData(savedInstanceState: Bundle?) {
        if (arguments.getInt(Constants.SCREEN) == Constants.SCREEN_LANDSCAPE) {
            adapter = LocalListAdapter(R.layout.item_landscap_local, ArrayList())
        } else {
            adapter = LocalListAdapter(R.layout.item_portrait_local, ArrayList())
        }

        observable = RxBus.getInstance().register(this)
        observable!!.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.option != null) {
                        if (it.option == 0) {
                            adapter!!.removeEntity(it.obj as LabelDB)
                        }
                    }
                }

        adapter!!.openLoadAnimation()
        adapter!!.setLoadMoreView(object : LoadMoreView() {
            override fun getLayoutId(): Int {
                return R.layout.layout_load_more
            }

            override fun getLoadingViewId(): Int {
                return R.id.load_more_loading_view
            }

            override fun getLoadEndViewId(): Int {
                return 0
            }

            override fun getLoadFailViewId(): Int {
                return R.id.load_more_load_fail_view
            }
        })
        adapter!!.setOnLoadMoreListener { viewModel.loadMore() }

        adapter!!.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(activity, WallpaperLocalActivity::class.java)
                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, (adapter as LocalListAdapter).data[position]))
        }

        adapter!!.setOnItemLongClickListener { adapter, view, position ->
            val builder = AlertDialog.Builder(activity)
                    .setTitle(resources.getString(R.string.alert_title))
                    .setMessage(resources.getString(R.string.alert_message))
                    .setPositiveButton(resources.getString(R.string.alert_positive)) { dialog, which ->
                        val data = viewModel.deleteEntity((adapter as LocalListAdapter).data[position])
                        if (data != null) {
                            adapter.data.removeAt(position)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(activity, resources.getString(R.string.alert_delete_success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.alert_delete_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(resources.getString(R.string.alert_negative), null)
                    .create()
            builder.show()
            true
        }

        binding.rvContainer.layoutManager = GridLayoutManager(activity, 3)
        binding.rvContainer.adapter = adapter
        binding.viewModel = viewModel

        viewModel.setParams(this, this, adapter!!)
        viewModel.observable()
    }

    override fun lazyLoad() {
        viewModel.loadFist()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getInstance().unregister(this)
    }

    companion object {
        fun getLocalFragment(screen: Int): LocalFragment {
            var fragment = LocalFragment()
            var bundle = Bundle()

            bundle.putInt(Constants.SCREEN, screen)
            fragment.arguments = bundle
            return fragment
        }
    }

}