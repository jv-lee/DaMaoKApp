package com.yuan7.lockscreen.view.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVMDialogFragment
import com.yuan7.lockscreen.databinding.FragmentDonwloadDialogBinding
import com.yuan7.lockscreen.helper.donwload.DownloadProgressHandler
import com.yuan7.lockscreen.helper.donwload.ProgressHelper
import com.yuan7.lockscreen.view.listener.DownloadFileListener
import com.yuan7.lockscreen.viewmodel.DownloadViewModel

/**
 * Created by Administrator on 2018/6/15.
 */
class DownloadDialogFragment : BaseVMDialogFragment<FragmentDonwloadDialogBinding, DownloadViewModel>(R.layout.fragment_donwload_dialog, DownloadViewModel::class.java) {

    var listener: DownloadFileListener? = null
    var path: String? = null

    override fun bindData(savedInstanceState: Bundle?) {
        viewModel.setPath(path!!)

        ProgressHelper.setProgressHandler(object : DownloadProgressHandler() {
            override fun onProgress(progress: Long, total: Long, done: Boolean) {
                binding.progress.max = total.toInt() / 1024
                binding.progress.progress = progress.toInt() / 1024
                if (done) dismiss()
            }
        })
        observable()
    }

    override fun observable() {
        viewModel.fileObservable!!.observe(this, Observer {
            listener!!.response(it!!)
        })
    }

    companion object {
        fun getDownloadFragment(path: String, listener: DownloadFileListener): DownloadDialogFragment {
            var fragment = DownloadDialogFragment()
            fragment.path = path
            fragment.listener = listener
            return fragment
        }
    }

}