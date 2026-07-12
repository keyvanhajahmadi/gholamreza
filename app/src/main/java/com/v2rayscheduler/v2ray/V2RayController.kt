package com.v2rayscheduler.v2ray

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.v2rayscheduler.model.ConnectionState

class V2RayController private constructor(private val context: Context) {

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

    private var isRunning = false

    fun startV2Ray(configContent: String): Boolean {
        if (isRunning) return true

        val intent = VpnService.prepare(context)
        if (intent != null) return false

        try {
            val serviceIntent = Intent(context, V2RayService::class.java).apply {
                putExtra("config", configContent)
            }
            context.startForegroundService(serviceIntent)
            isRunning = true
            return true
        } catch (_: Exception) {
            return false
        }
    }

    fun stopV2Ray() {
        isRunning = false
        val intent = Intent(context, V2RayService::class.java)
        context.stopService(intent)
    }

    fun setRunning(running: Boolean) {
        isRunning = running
    }
}
