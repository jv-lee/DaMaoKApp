package com.yuan7.lockscreen.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.widget.Toast
import com.umeng.analytics.MobclickAgent
import com.yuan7.lockscreen.utils.ActivityUtil
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Created by Administrator on 2018/6/5.
 */
abstract class BaseActivity : FragmentActivity(), LifecycleRegistryOwner, HasSupportFragmentInjector {
    private var firstTime: Long = 0
    private var hasBackExit = false
    private var hasBackClose = false

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    val mRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry {
        return mRegistry
    }

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment>? {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        ActivityUtil.add(this)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityUtil.remove(this)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> if (hasBackExit) {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
                    firstTime = secondTime//更新firstTime
                    return true
                } else {//两次按键小于2秒时，退出应用
                    finish()
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (hasBackClose) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    //back按键退出 开关
    protected fun backCloseEnable(enable: Boolean) {
        hasBackClose = enable
    }

    //back 按下2次退出 开关
    protected fun backExitEnable(enable: Boolean) {
        hasBackExit = enable
    }
}