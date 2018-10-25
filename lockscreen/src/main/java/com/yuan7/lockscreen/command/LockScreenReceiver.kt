package com.yuan7.lockscreen.command

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.yuan7.lockscreen.constant.Constants
import com.yuan7.lockscreen.utils.SPUtil
import com.yuan7.lockscreen.view.activity.LockActivity

import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import android.content.Context.KEYGUARD_SERVICE

class LockScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        //If the screen was just turned on or it just booted up, start your Lock Activity
        if (action == Intent.ACTION_SCREEN_ON || action == Intent.ACTION_BOOT_COMPLETED) {
            startLockActivity(context)
        }
    }

    fun startLockActivity(context: Context) {
        val km = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!km.isKeyguardLocked) {
                        SPUtil.getInstance(context)
                        val path = SPUtil.get(Constants.LOCK_PATH, "") as String
                        if (path != "") {
                            val i = Intent(context, LockActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(i)
                        }
                    } else {
                        startLockActivity(context)
                    }
                }
    }
}
