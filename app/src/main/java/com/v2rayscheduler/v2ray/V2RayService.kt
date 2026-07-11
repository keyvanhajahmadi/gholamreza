package com.v2rayscheduler.v2ray

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.v2ray.ang.util.V2RayUtils
import com.v2rayscheduler.R

class V2RayService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configJson = intent?.getStringExtra("config") ?: return START_NOT_STICKY

        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        startVPN(configJson)

        return START_STICKY
    }

    private fun startVPN(configJson: String) {
        try {
            val builder = Builder().apply {
                setMtu(1500)
                addAddress("10.0.0.2", 32)
                addRoute("0.0.0.0", 0)
                addDnsServer("8.8.8.8")
                addDnsServer("1.1.1.1")
                setSession(getString(R.string.app_name))
            }

            vpnInterface = establish()
            if (vpnInterface == null) return

            V2RayUtils.startV2Ray(configJson, object : V2RayUtils.V2RayCallback {
                override fun onSuccess() {
                    isRunning = true
                }

                override fun onError(msg: String) {
                    isRunning = false
                    stopSelf()
                }
            })

        } catch (e: Exception) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        stopVPN()
        super.onDestroy()
    }

    private fun stopVPN() {
        isRunning = false
        V2RayUtils.stopV2Ray()
        vpnInterface?.close()
        vpnInterface = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "V2Ray Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "V2Ray connection status"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("V2Ray Scheduler")
            .setContentText("VPN is running")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "v2ray_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}
