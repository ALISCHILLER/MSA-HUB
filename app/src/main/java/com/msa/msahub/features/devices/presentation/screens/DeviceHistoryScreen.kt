package com.msa.msahub.features.devices.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.core.ui.components.LoadingView
import com.msa.msahub.features.devices.presentation.state.DeviceHistoryUiEvent
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceHistoryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceHistoryScreen(
    deviceId: String,
    onBack: () -> Unit,
    viewModel: DeviceHistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(deviceId) {
        viewModel.onEvent(DeviceHistoryUiEvent.SetDeviceId(deviceId))
        viewModel.onEvent(DeviceHistoryUiEvent.Load)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> Text(state.errorMessage ?: "", Modifier.padding(16.dp))
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.items) { s ->
                        Card {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text("Online: ${s.isOnline}")
                                Text("On: ${s.isOn}")
                                Text("Updated: ${s.updatedAtMillis}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
