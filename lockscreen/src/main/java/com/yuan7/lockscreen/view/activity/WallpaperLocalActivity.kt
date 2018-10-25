package com.yuan7.lockscreen.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVActivity
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.ActivityWallpaperLocalBinding
import com.yuan7.lockscreen.helper.a.AHelper
import com.yuan7.lockscreen.helper.rx.EventBase
import com.yuan7.lockscreen.helper.rx.RxBus
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.model.repository.WallpaperRepository
import com.yuan7.lockscreen.utils.BitmapUtil
import com.yuan7.lockscreen.utils.SPUtil
import com.yuan7.lockscreen.utils.ShareUtil
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class WallpaperLocalActivity : BaseVActivity<ActivityWallpaperLocalBinding>(R.layout.activity_wallpaper_local), View.OnClickListener {

    @Inject
    lateinit var dataBase: AppDataBase
    var entity: LabelDB? = null
    var repository: WallpaperRepository? = null

    override fun bindData(savedInstanceState: Bundle?) {
        repository = WallpaperRepository(dataBase)
        entity = intent.getSerializableExtra(Constants.ACTIVITY_INTENT_ENTITY) as LabelDB?

        binding.entity = entity
        binding.onClick = this

        val enable = SPUtil.get(Constants.GAME_ENABLE_YQ, false) as Boolean
        if (enable) {
            AHelper.showGDTBanner(this, binding.flAd)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()

            R.id.iv_pic -> startActivity(Intent(this@WallpaperLocalActivity, ImageActivity::class.java)
                    .putExtra(Constants.ACTIVITY_INTENT_ENTITY, entity))

            R.id.iv_share -> ShareUtil.shareSingleImage(this, BitmapUtil.saveFile(BitmapUtil.drawableToBitmap(binding.ivPic.drawable), "icon.jpg"))

            R.id.btn_set_wallpaper -> repository!!.getWallpaperSetResponse(this@WallpaperLocalActivity, entity!!, null)
                    .subscribe {
                        if (it) Toast.makeText(this@WallpaperLocalActivity, resources.getString(R.string.wallpaper_set_success), Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this@WallpaperLocalActivity, resources.getString(R.string.wallpaper_set_failed), Toast.LENGTH_SHORT).show()
                    }

            R.id.btn_set_lock -> {
                SPUtil.save(Constants.LOCK_PATH, entity!!.localPath)
                Toast.makeText(this@WallpaperLocalActivity, resources.getString(R.string.lock_screen_set_success), Toast.LENGTH_SHORT).show()
            }

            R.id.iv_delete -> AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.alert_title))
                    .setMessage(resources.getString(R.string.alert_message))
                    .setPositiveButton(resources.getString(R.string.alert_positive)) { dialog, which ->
                        repository!!.delete(entity!!)
                                .subscribe {
                                    if (it > 0) {
                                        RxBus.getInstance().post(EventBase(0, entity))
                                        Toast.makeText(this@WallpaperLocalActivity, resources.getString(R.string.alert_delete_success), Toast.LENGTH_SHORT).show()
                                        this.finish()
                                    } else {
                                        Toast.makeText(this@WallpaperLocalActivity, resources.getString(R.string.alert_delete_failed), Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }
                    .setNegativeButton(resources.getString(R.string.alert_negative), null)
                    .create()
                    .show()
        }
    }

}