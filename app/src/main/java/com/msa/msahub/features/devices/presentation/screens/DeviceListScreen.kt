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
import com.msa.msahub.features.devices.presentation.components.DeviceCard
import com.msa.msahub.features.devices.presentation.state.DeviceListUiEvent
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    onDeviceClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: DeviceListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onEvent(DeviceListUiEvent.Load) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devices") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
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
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items) { d ->
                        DeviceCard(device = d, onClick = { onDeviceClick(d.id) })
                    }
                }
            }
        }
    }
}
