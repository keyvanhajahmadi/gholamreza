package com.v2rayscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.v2rayscheduler.model.ScheduleConfig

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("v2ray_scheduler", Context.MODE_PRIVATE)
            val configsJson = prefs.getString("schedules", "[]") ?: "[]"
            val configs = ScheduleConfig.listFromJson(configsJson)

            val manager = ScheduleManager(context)
            configs.forEach { config ->
                if (config.isEnabled) {
                    manager.scheduleAlarm(config)
                }
            }
        }
    }
}
