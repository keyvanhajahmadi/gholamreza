package com.v2rayscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.v2ray.V2RayController

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val configJson = intent.getStringExtra("config_json") ?: return
        val config = ScheduleConfig.fromJson(configJson)

        val controller = V2RayController.getInstance(context)
        controller.startV2Ray(config.configContent)
    }
}
