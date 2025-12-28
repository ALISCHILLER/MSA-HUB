package com.msa.msahub.features.devices.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.features.devices.presentation.state.DeviceSettingsUiEvent
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSettingsScreen(
    deviceId: String,
    onBack: () -> Unit,
    viewModel: DeviceSettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(deviceId) {
        viewModel.onEvent(DeviceSettingsUiEvent.SetDeviceId(deviceId))
        viewModel.onEvent(DeviceSettingsUiEvent.Load)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Favorite")
                Switch(
                    checked = state.isFavorite,
                    onCheckedChange = { viewModel.onEvent(DeviceSettingsUiEvent.ToggleFavorite(it)) }
                )
            }
        }
    }
}
