package com.yuan7.lockscreen.command

import android.app.KeyguardManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v7.app.NotificationCompat

import com.yuan7.lockscreen.R
import com.yuan7.lockscreen.view.activity.MainActivity

class LockScreenService : Service() {

    internal var receiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        val builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("文字锁屏正在为你服务")
        builder.setContentText("文字锁屏功能，请勿关闭")
        builder.setWhen(System.currentTimeMillis())
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()
        startForeground(FOREGROUND_ID, notification)


        val key: KeyguardManager.KeyguardLock
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        //This is deprecated, but it is a simple way to disable the lockscreen in code
        key = km.newKeyguardLock("")
        key.disableKeyguard()
        //Start listening for the Screen On, Screen Off, and Boot completed actions
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_BOOT_COMPLETED)
        //Set up a receiver to listen for the Intents in this Service
        receiver = LockScreenReceiver()
        registerReceiver(receiver, filter)

        super.onCreate()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    companion object {
        private val FOREGROUND_ID = 1000
    }
}
