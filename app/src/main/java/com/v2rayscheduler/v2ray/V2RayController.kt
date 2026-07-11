package com.v2rayscheduler.v2ray

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

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

    fun startV2Ray(configContent: String): Boolean {
        return try {
            val file = File(context.cacheDir, "v2ray_config.json")
            file.writeText(configContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/json")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
            val intent = Intent(context.packageManager.getLaunchIntentForPackage(
                "com.v2ray.ang"
            ))
            context.startActivity(intent)
        } catch (_: Exception) { }
    }
}
