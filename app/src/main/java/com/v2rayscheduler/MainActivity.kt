package com.v2rayscheduler

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v2rayscheduler.model.ScheduleConfig
import com.v2rayscheduler.ui.MainScreen
import com.v2rayscheduler.ui.MainViewModel
import com.v2rayscheduler.ui.ScheduleScreen
import com.v2rayscheduler.ui.theme.V2RaySchedulerTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val VPN_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            V2RaySchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = viewModel()
                    val schedules by viewModel.schedules.collectAsState()
                    val connectionState by viewModel.connectionState.collectAsState()

                    var showScheduleScreen by remember { mutableStateOf(false) }
                    var editingConfig by remember { mutableStateOf<ScheduleConfig?>(null) }

                    if (showScheduleScreen) {
                        ScheduleScreen(
                            existingConfig = editingConfig,
                            onSave = { config ->
                                if (editingConfig != null) {
                                    viewModel.updateSchedule(config)
                                } else {
                                    viewModel.addSchedule(config)
                                }
                                showScheduleScreen = false
                                editingConfig = null
                            },
                            onBack = {
                                showScheduleScreen = false
                                editingConfig = null
                            }
                        )
                    } else {
                        MainScreen(
                            schedules = schedules,
                            connectionState = connectionState,
                            onAddSchedule = {
                                editingConfig = null
                                showScheduleScreen = true
                            },
                            onEditSchedule = { config ->
                                editingConfig = config
                                showScheduleScreen = true
                            },
                            onDeleteSchedule = { config ->
                                viewModel.deleteSchedule(config)
                            },
                            onToggleSchedule = { config ->
                                viewModel.toggleSchedule(config)
                            },
                            onToggleConnection = {
                                val intent = VpnService.prepare(this@MainActivity)
                                if (intent != null) {
                                    startActivityForResult(intent, VPN_REQUEST_CODE)
                                } else {
                                    viewModel.toggleConnection()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val viewModel: MainViewModel = viewModel()
            viewModel.toggleConnection()
        }
    }
}
