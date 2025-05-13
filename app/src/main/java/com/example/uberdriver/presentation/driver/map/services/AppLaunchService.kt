package com.example.uberdriver.presentation.driver.map.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AppLaunchService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bringAppToFront()
        stopSelf() // no need to keep it running
        return START_NOT_STICKY
    }

    private fun bringAppToFront() {
        val packageManager = applicationContext.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            applicationContext.startActivity(this)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
