package com.v2rayscheduler.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import com.v2rayscheduler.model.ConnectionState
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.scheduler.ScheduleManager
import com.v2rayscheduler.v2ray.V2RayController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("v2ray_scheduler", Context.MODE_PRIVATE)
    private val scheduleManager = ScheduleManager(application)
    private val v2rayController = V2RayController.getInstance(application)

    private val _schedules = MutableStateFlow<List<ScheduleConfig>>(emptyList())
    val schedules: StateFlow<List<ScheduleConfig>> = _schedules.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        val json = prefs.getString("schedules", "[]") ?: "[]"
        _schedules.value = ScheduleConfig.listFromJson(json)
    }

    private fun saveSchedules() {
        prefs.edit()
            .putString("schedules", ScheduleConfig.listToJson(_schedules.value))
            .apply()
    }

    fun addSchedule(config: ScheduleConfig) {
        val list = _schedules.value.toMutableList()
        list.add(config)
        _schedules.value = list
        saveSchedules()
        scheduleManager.scheduleStartAlarm(config)
        scheduleManager.scheduleEndAlarm(config)
    }

    fun updateSchedule(config: ScheduleConfig) {
        val list = _schedules.value.toMutableList()
        val index = list.indexOfFirst { it.id == config.id }
        if (index != -1) {
            scheduleManager.cancelAlarms(list[index])
            list[index] = config
            _schedules.value = list
            saveSchedules()
            scheduleManager.scheduleStartAlarm(config)
            scheduleManager.scheduleEndAlarm(config)
        }
    }

    fun deleteSchedule(config: ScheduleConfig) {
        val list = _schedules.value.toMutableList()
        list.removeAll { it.id == config.id }
        _schedules.value = list
        saveSchedules()
        scheduleManager.cancelAlarms(config)
    }

    fun toggleSchedule(config: ScheduleConfig) {
        val updated = config.copy(isEnabled = !config.isEnabled)
        updateSchedule(updated)
    }

    fun toggleConnection(configJson: String? = null) {
        when (_connectionState.value) {
            ConnectionState.DISCONNECTED -> {
                val ctx = getApplication<Application>()
                val intent = VpnService.prepare(ctx)
                if (intent != null) return

                _connectionState.value = ConnectionState.CONNECTING
                val config = configJson ?: _lastConfig
                if (config != null) {
                    _lastConfig = config
                    val success = v2rayController.startV2Ray(config)
                    _connectionState.value = if (success) ConnectionState.CONNECTED else ConnectionState.ERROR
                }
            }
            ConnectionState.CONNECTED -> {
                v2rayController.stopV2Ray()
                _connectionState.value = ConnectionState.DISCONNECTED
            }
            else -> {}
        }
    }

    private var _lastConfig: String? = null
}
