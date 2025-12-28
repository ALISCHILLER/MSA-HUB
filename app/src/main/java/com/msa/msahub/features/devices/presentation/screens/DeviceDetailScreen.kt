package com.msa.msahub.features.devices.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.core.ui.components.LoadingView
import com.msa.msahub.features.devices.presentation.components.ConnectionStatusIndicator
import com.msa.msahub.features.devices.presentation.components.DeviceControlsPanel
import com.msa.msahub.features.devices.presentation.state.DeviceDetailUiEvent
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    deviceId: String,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: DeviceDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(deviceId) {
        viewModel.onEvent(DeviceDetailUiEvent.SetDeviceId(deviceId))
        viewModel.onEvent(DeviceDetailUiEvent.Load)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.device?.name ?: "Device") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
                actions = {
                    TextButton(onClick = onHistoryClick) { Text("History") }
                    TextButton(onClick = onSettingsClick) { Text("Settings") }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> Text(
                    text = state.errorMessage ?: "",
                    modifier = Modifier.padding(16.dp)
                )
                else -> Column(
                    Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ConnectionStatusIndicator(isOnline = state.state?.isOnline == true)

                    DeviceControlsPanel(
                        capabilities = state.device?.capabilities.orEmpty(),
                        onTurnOn = { viewModel.onEvent(DeviceDetailUiEvent.SendCommand("turn_on")) },
                        onTurnOff = { viewModel.onEvent(DeviceDetailUiEvent.SendCommand("turn_off")) }
                    )

                    Text(
                        text = "Last state: ${state.state?.updatedAtMillis ?: 0L}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
