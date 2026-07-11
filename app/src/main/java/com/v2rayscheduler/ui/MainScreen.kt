package com.v2rayscheduler.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2rayscheduler.model.ScheduleConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    schedules: List<ScheduleConfig>,
    onAddSchedule: () -> Unit,
    onEditSchedule: (ScheduleConfig) -> Unit,
    onDeleteSchedule: (ScheduleConfig) -> Unit,
    onToggleSchedule: (ScheduleConfig) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("V2Ray Scheduler") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSchedule) {
                Icon(Icons.Default.Add, contentDescription = "Add schedule")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Scheduled Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (schedules.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn {
                    items(schedules, key = { it.id }) { schedule ->
                        ScheduleCard(
                            config = schedule,
                            onEdit = { onEditSchedule(schedule) },
                            onDelete = { onDeleteSchedule(schedule) },
                            onToggle = { onToggleSchedule(schedule) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    config: ScheduleConfig,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = config.label.ifEmpty { "Unnamed Schedule" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%02d:%02d", config.startHour, config.startMinute)} - ${String.format("%02d:%02d", config.endHour, config.endMinute)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = config.isEnabled,
                onCheckedChange = { onToggle() }
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No schedules yet.\nTap + to add one.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
