package com.example.uberdriver.presentation.driver.map.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.uberdriver.R
import com.example.uberdriver.presentation.driver.MainActivity

class AppLaunchService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppLaunchService", "onStartCommand called")

        // Step 1: Start safe foreground notification (minimal)
        startForeground(1, buildForegroundNotification())

        // Step 2: Show full-screen intent to bring app to front
        showFullScreenNotification()

        // Step 3: Stop service after launching app
        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, "pickup_channel")
            .setContentTitle("Driver Service Running")
            .setContentText("Monitoring pickup location")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) // ✅ use valid icon
            .setPriority(NotificationCompat.PRIORITY_LOW) // Safe for foreground
            .build()
    }

    private fun showFullScreenNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "pickup_channel")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("You've arrived at the pickup point")
            .setContentText("Tap to open Uber Driver")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL) // Treat like a call
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1002, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "pickup_channel",
                "Pickup Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for pickup arrival notifications"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

