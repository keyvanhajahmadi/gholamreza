package com.v2rayscheduler.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.v2ray.V2RayController

class ConnectionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val configJson = inputData.getString("config_json") ?: return Result.failure()
        val config = ScheduleConfig.fromJson(configJson)

        val controller = V2RayController.getInstance(applicationContext)
        val success = controller.startV2Ray(config.configContent)

        return if (success) Result.success() else Result.retry()
    }
}
