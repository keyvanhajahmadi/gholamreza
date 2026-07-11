package com.v2rayscheduler.v2ray

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.v2ray.ang.AppConfig
import com.v2ray.ang.util.Utils
import com.v2rayscheduler.model.ConnectionState

class V2RayController private constructor(private val context: Context) {

    private var isRunning = false
    private var vpnFd: ParcelFileDescriptor? = null

    companion object {
        @Volatile
        private var instance: V2RayController? = null

        fun getInstance(context: Context): V2RayController {
            return instance ?: synchronized(this) {
                instance ?: V2RayController(context.applicationContext).also { instance = it }
            }
        }
    }

    val state: ConnectionState
        get() = if (isRunning) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED

    fun startV2Ray(configContent: String): Boolean {
        if (isRunning) {
            stopV2Ray()
        }

        try {
            val intent = VpnService.prepare(context)
            if (intent != null) {
                return false
            }

            isRunning = true
            val serviceIntent = Intent(context, V2RayService::class.java).apply {
                putExtra("config", configContent)
            }
            context.startForegroundService(serviceIntent)
            return true
        } catch (e: Exception) {
            isRunning = false
            return false
        }
    }

    fun stopV2Ray() {
        isRunning = false
        val serviceIntent = Intent(context, V2RayService::class.java)
        context.stopService(serviceIntent)
    }
}
