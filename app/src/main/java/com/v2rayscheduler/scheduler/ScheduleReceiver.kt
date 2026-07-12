package com.v2rayscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.v2ray.V2RayController

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val configJson = intent.getStringExtra("config_json") ?: return
        val action = intent.getStringExtra("action") ?: "start"
        val config = ScheduleConfig.fromJson(configJson)

        if (action == "start") {
            V2RayController.getInstance(context).startV2Ray(config.configContent)
        } else {
            V2RayController.getInstance(context).stopV2Ray()
        }
    }
}
