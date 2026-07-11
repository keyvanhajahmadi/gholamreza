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
    var isRepeatDaily by remember { mutableStateOf(existingConfig?.isRepeatDaily ?: true) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var configError by remember { mutableStateOf(false) }

    var startHour by remember { mutableIntStateOf(existingConfig?.startHour ?: 8) }
    var startMinute by remember { mutableIntStateOf(existingConfig?.startMinute ?: 0) }
    var endHour by remember { mutableIntStateOf(existingConfig?.endHour ?: 17) }
    var endMinute by remember { mutableIntStateOf(existingConfig?.endMinute ?: 0) }

    val startTimePickerState = remember(showStartPicker) {
        TimePickerState(startHour, startMinute, is24Hour = true)
    }
    val endTimePickerState = remember(showEndPicker) {
        TimePickerState(endHour, endMinute, is24Hour = true)
    }

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
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
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

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Schedule Time", style = MaterialTheme.typography.titleSmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start Time")
                        FilledTonalButton(onClick = {
                            showStartPicker = true
                        }) {
                            Text(String.format("%02d:%02d", startHour, startMinute))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("End Time")
                        FilledTonalButton(onClick = {
                            showEndPicker = true
                        }) {
                            Text(String.format("%02d:%02d", endHour, endMinute))
                        }
                    }
                }
            }

            if (showStartPicker) {
                AlertDialog(
                    onDismissRequest = { showStartPicker = false },
                    title = { Text("Select Start Time") },
                    text = { TimePicker(state = startTimePickerState) },
                    confirmButton = {
                        TextButton(onClick = {
                            startHour = startTimePickerState.hour
                            startMinute = startTimePickerState.minute
                            showStartPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
                    }
                )
            }

            if (showEndPicker) {
                AlertDialog(
                    onDismissRequest = { showEndPicker = false },
                    title = { Text("Select End Time") },
                    text = { TimePicker(state = endTimePickerState) },
                    confirmButton = {
                        TextButton(onClick = {
                            endHour = endTimePickerState.hour
                            endMinute = endTimePickerState.minute
                            showEndPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
                    }
                )
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
