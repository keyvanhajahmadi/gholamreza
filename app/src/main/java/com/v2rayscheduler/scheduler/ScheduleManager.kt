package com.v2rayscheduler.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.v2rayscheduler.model.ScheduleConfig
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ScheduleManager(private val context: Context) {

    fun scheduleStartAlarm(config: ScheduleConfig) {
        if (!config.isEnabled) return
        scheduleAlarm(config, config.startHour, config.startMinute, "start")
    }

    fun scheduleEndAlarm(config: ScheduleConfig) {
        if (!config.isEnabled) return
        scheduleAlarm(config, config.endHour, config.endMinute, "end")
    }

    private fun scheduleAlarm(config: ScheduleConfig, hour: Int, minute: Int, action: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleReceiver::class.java).apply {
            putExtra("config_json", config.toJson())
            putExtra("action", action)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            config.id.hashCode() + if (action == "start") 0 else 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        if (config.isRepeatDaily) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarms(config: ScheduleConfig) {
        cancelAlarm(config, "start")
        cancelAlarm(config, "end")
    }

    private fun cancelAlarm(config: ScheduleConfig, action: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            config.id.hashCode() + if (action == "start") 0 else 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
