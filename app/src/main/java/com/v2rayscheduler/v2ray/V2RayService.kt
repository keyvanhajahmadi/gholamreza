package com.v2rayscheduler.v2ray

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import libv2ray.CoreCallbackHandler
import libv2ray.CoreController
import libv2ray.Libv2ray

class V2RayService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var coreController: CoreController? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Libv2ray.initCoreEnv(filesDir.absolutePath, "")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configJson = intent?.getStringExtra("config") ?: return START_NOT_STICKY

        startForeground(NOTIFICATION_ID, buildNotification())

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
                setSession("V2RayScheduler")
            }
            vpnInterface = builder.establish()
            if (vpnInterface == null) return

            val tunFd = vpnInterface!!.fd

            coreController = Libv2ray.newCoreController(object : CoreCallbackHandler {
                override fun startup(): Long = 0
                override fun shutdown(): Long {
                    stopSelf()
                    return 0
                }
                override fun onEmitStatus(l: Long, s: String?): Long = 0
            })

            coreController?.startLoop(configJson, tunFd)

        } catch (e: Exception) {
            stopVPN()
        }
    }

    private fun stopVPN() {
        try {
            coreController?.stopLoop()
        } catch (_: Exception) { }
        coreController = null
        try {
            vpnInterface?.close()
        } catch (_: Exception) { }
        vpnInterface = null
    }

    override fun onDestroy() {
        stopVPN()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVPN()
        super.onRevoke()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "V2Ray Service", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "V2Ray VPN connection" }
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
