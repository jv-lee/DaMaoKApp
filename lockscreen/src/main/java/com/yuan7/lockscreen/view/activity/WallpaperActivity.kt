package com.yuan7.lockscreen.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityWallpaperBinding
import com.yuan7.lockscreen.helper.a.AHelper
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.entity.LabelEntity
import com.yuan7.lockscreen.model.repository.WallpaperRepository
import com.yuan7.lockscreen.utils.*
import com.yuan7.lockscreen.view.fragment.DownloadDialogFragment
import com.yuan7.lockscreen.view.listener.DownloadFileListener
import java.io.File
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class WallpaperActivity : BaseVActivity<ActivityWallpaperBinding>(R.layout.activity_wallpaper), View.OnClickListener {

    @Inject
    lateinit var dataBase: AppDataBase
    var entity: LabelEntity? = null
    var repository: WallpaperRepository? = null

    override fun bindData(savedInstanceState: Bundle?) {
        repository = WallpaperRepository(dataBase)
        entity = intent.getSerializableExtra(Constants.ACTIVITY_INTENT_ENTITY) as LabelEntity?

        binding.entity = entity
        binding.onClick = this
        binding.rbNice.setOnCheckedChangeListener { buttonView, isChecked ->
            var count = binding.tvNiceCount.text.toString().toInt() + 1
            if (isChecked) binding.tvNiceCount.setText(count.toString())
        }

        val enable = SPUtil.get(Constants.GAME_ENABLE_YQ, false) as Boolean
        if (enable) {
            AHelper.showGDTBanner(this, binding.flAd)
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_search -> startActivity(Intent(this, FindActivity::class.java))
            R.id.iv_file -> startActivity(Intent(this, LocalActivity::class.java))
            R.id.iv_pic -> FragmentUtil.showFragmentDialog(supportFragmentManager.beginTransaction(), null,
                    DownloadDialogFragment.getDownloadFragment(entity!!.oldimgUrl.substring(43, entity!!.oldimgUrl.length), object : DownloadFileListener {
                        override fun response(file: File) {
                            startActivity(Intent(this@WallpaperActivity, ImageActivity::class.java)
                                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, EntityUtil.LabelEntityToDB(entity, file.absolutePath)))
                        }
                    }))
            R.id.iv_share -> ShareUtil.shareSingleImage(this, BitmapUtil.saveFile(BitmapUtil.drawableToBitmap(binding.ivPic.drawable), "icon.jpg"))
            R.id.btn_set_wallpaper -> FragmentUtil.showFragmentDialog(supportFragmentManager.beginTransaction(), null,
                    DownloadDialogFragment.getDownloadFragment(entity!!.oldimgUrl.substring(43, entity!!.oldimgUrl.length), object : DownloadFileListener {
                        override fun response(file: File) {
                            repository!!.getWallpaperSetResponse(this@WallpaperActivity, EntityUtil.LabelEntityToDB(entity, file.absolutePath), null)
                                    .subscribe {
                                        if (it) Toast.makeText(this@WallpaperActivity, resources.getString(R.string.wallpaper_set_success), Toast.LENGTH_SHORT).show()
                                        else Toast.makeText(this@WallpaperActivity, resources.getString(R.string.wallpaper_set_failed), Toast.LENGTH_SHORT).show()
                                    }
                        }
                    }))
            R.id.btn_set_lock -> FragmentUtil.showFragmentDialog(supportFragmentManager.beginTransaction(), null,
                    DownloadDialogFragment.getDownloadFragment(entity!!.oldimgUrl.substring(43, entity!!.oldimgUrl.length), object : DownloadFileListener {
                        override fun response(file: File) {
                            SPUtil.save(Constants.LOCK_PATH, file.absolutePath)
                            Toast.makeText(this@WallpaperActivity, resources.getString(R.string.lock_screen_set_success), Toast.LENGTH_SHORT).show()
                        }
                    }))
            R.id.iv_download -> FragmentUtil.showFragmentDialog(supportFragmentManager.beginTransaction(), null,
                    DownloadDialogFragment.getDownloadFragment(entity!!.oldimgUrl.substring(43, entity!!.oldimgUrl.length), object : DownloadFileListener {
                        override fun response(file: File) {
                            repository!!.downloadWallpaper(EntityUtil.LabelEntityToDB(entity, file.absolutePath))
                                    .subscribe {
                                        if (it) Toast.makeText(this@WallpaperActivity, resources.getString(R.string.download_success), Toast.LENGTH_SHORT).show()
                                        else Toast.makeText(this@WallpaperActivity, resources.getString(R.string.download_extends), Toast.LENGTH_SHORT).show()
                                    }
                        }
                    }))

        }
    }

}