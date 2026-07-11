package com.v2rayscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.v2ray.V2RayController
import com.v2rayscheduler.v2ray.V2RayService

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val configJson = intent.getStringExtra("config_json") ?: return
        val action = intent.getStringExtra("action") ?: "start"
        val config = ScheduleConfig.fromJson(configJson)

        if (action == "start") {
            val serviceIntent = Intent(context, V2RayService::class.java).apply {
                putExtra("config", config.configContent)
                putExtra("label", config.label)
            }
            context.startForegroundService(serviceIntent)
        } else {
            V2RayController.getInstance(context).stopV2Ray()
        }
    }
}
