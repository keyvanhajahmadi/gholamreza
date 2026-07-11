package com.v2rayscheduler.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScheduleConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "",
    val configContent: String = "",
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 17,
    val endMinute: Int = 0,
    val isEnabled: Boolean = true,
    val isRepeatDaily: Boolean = true
) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): ScheduleConfig =
            Gson().fromJson(json, ScheduleConfig::class.java)

        fun listToJson(list: List<ScheduleConfig>): String =
            Gson().toJson(list)

        fun listFromJson(json: String): List<ScheduleConfig> {
            val type = object : TypeToken<List<ScheduleConfig>>() {}.type
            return Gson().fromJson(json, type) ?: emptyList()
        }
    }
}
