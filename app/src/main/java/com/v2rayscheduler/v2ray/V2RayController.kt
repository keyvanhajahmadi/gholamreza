package com.v2rayscheduler.v2ray

import android.content.Context
import android.content.Intent
import android.net.Uri
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
        get() = ConnectionState.DISCONNECTED

    fun openConfigInV2RayNG(configContent: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("v2rayng://import-config/${Uri.encode(configContent)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) { }
    }

    fun startV2Ray(configContent: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("v2rayng://connect/${Uri.encode(configContent)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun stopV2Ray() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("v2rayng://disconnect")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) { }
    }
}
