package com.v2rayscheduler.v2ray

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class V2RayService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configJson = intent?.getStringExtra("config") ?: return START_NOT_STICKY
        val label = intent?.getStringExtra("label") ?: "V2Ray"

        val notification = buildNotification(label)
        startForeground(NOTIFICATION_ID, notification)

        V2RayController.getInstance(this).startV2Ray(configJson)

        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "V2Ray Scheduler",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "V2Ray connection status"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(label: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("V2Ray Scheduler")
            .setContentText("Starting $label ...")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "v2ray_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}
