package com.yuan7.lockscreen.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.base.BaseVFragment
import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.databinding.FragmentImagePortraitBinding
import com.yuan7.lockscreen.helper.glide.GlideUtils
import com.yuan7.lockscreen.model.db.AppDataBase
import com.yuan7.lockscreen.model.entity.LabelDB
import com.yuan7.lockscreen.model.repository.WallpaperRepository
import com.yuan7.lockscreen.utils.BitmapUtil
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/15.
 */
class ImagePortraitFragment: BaseVFragment<FragmentImagePortraitBinding>(R.layout.fragment_image_portrait) {

    @Inject
    lateinit var dataBase: AppDataBase
    var repository: WallpaperRepository? = null
    var entity: LabelDB? = null

    override fun bindData(savedInstanceState: Bundle?) {
        repository = WallpaperRepository(dataBase)
        entity = activity.intent.getSerializableExtra(Constants.ACTIVITY_INTENT_ENTITY) as LabelDB?

        GlideUtils.loadImage(entity!!.localPath, binding.ivPic)

        binding.ivPic.setOnClickListener { activity.finish() }
        binding.btnClick.setOnClickListener {
            Toast.makeText(activity, resources.getString(R.string.wallpaper_set_ing), Toast.LENGTH_SHORT).show()
            binding.btnClick.visibility = View.GONE
            repository!!.getWallpaperSetResponse(activity, entity!!, BitmapUtil.getScreenShot(activity))
                    .subscribe {
                        if (it) {
                            Toast.makeText(activity, resources.getString(R.string.wallpaper_set_success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.wallpaper_set_failed), Toast.LENGTH_SHORT).show()
                        }
                        activity.finish()
                    }
        }
    }

}