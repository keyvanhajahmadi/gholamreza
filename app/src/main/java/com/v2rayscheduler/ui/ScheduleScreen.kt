package com.v2rayscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2rayscheduler.model.ScheduleConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    existingConfig: ScheduleConfig? = null,
    onSave: (ScheduleConfig) -> Unit,
    onBack: () -> Unit
) {
    var label by remember { mutableStateOf(existingConfig?.label ?: "") }
    var configContent by remember { mutableStateOf(existingConfig?.configContent ?: "") }
    var hour by remember { mutableIntStateOf(existingConfig?.hour ?: 8) }
    var minute by remember { mutableIntStateOf(existingConfig?.minute ?: 0) }
    var isRepeatDaily by remember { mutableStateOf(existingConfig?.isRepeatDaily ?: true) }
    var showTimePicker by remember { mutableStateOf(false) }
    var configError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (existingConfig != null) "Edit Schedule" else "New Schedule")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (configContent.isNotBlank()) {
                            val config = ScheduleConfig(
                                id = existingConfig?.id ?: java.util.UUID.randomUUID().toString(),
                                label = label,
                                configContent = configContent,
                                hour = hour,
                                minute = minute,
                                isEnabled = existingConfig?.isEnabled ?: true,
                                isRepeatDaily = isRepeatDaily
                            )
                            onSave(config)
                        } else {
                            configError = true
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                placeholder = { Text("e.g. Work VPN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = configContent,
                onValueChange = {
                    configContent = it
                    configError = false
                },
                label = { Text("V2Ray Config (JSON)") },
                placeholder = { Text("Paste your VMess/VLess/SS config here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                isError = configError,
                supportingText = if (configError) {
                    { Text("Config cannot be empty") }
                } else null,
                maxLines = 10
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Time", style = MaterialTheme.typography.titleSmall)
                FilledTonalButton(onClick = { showTimePicker = true }) {
                    Text(
                        String.format(
                            "%02d:%02d %s",
                            if (hour > 12) hour - 12 else if (hour == 0) 12 else hour,
                            minute,
                            if (hour < 12) "AM" else "PM"
                        )
                    )
                }
            }

            if (showTimePicker) {
                TimePickerState(
                    initialHour = hour,
                    initialMinute = minute,
                    is24Hour = false
                ).let { state ->
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            hour = state.hour
                            minute = state.minute
                            showTimePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Repeat Daily", style = MaterialTheme.typography.titleSmall)
                Switch(checked = isRepeatDaily, onCheckedChange = { isRepeatDaily = it })
            }
        }
    }
}
