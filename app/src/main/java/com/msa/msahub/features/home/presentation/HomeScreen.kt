package com.msa.msahub.features.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.features.devices.presentation.components.DeviceCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onDeviceClick: (String) -> Unit,
    onSeeAllDevices: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MSA HUB Dashboard") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ۱. بخش خلاصه وضعیت (Summary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("System Status", style = MaterialTheme.typography.titleMedium)
                        Text("${state.onlineCount} of ${state.totalCount} devices online", style = MaterialTheme.typography.bodyMedium)
                    }
                    CircularProgressIndicator(
                        progress = if (state.totalCount > 0) state.onlineCount.toFloat() / state.totalCount else 0f,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 6.dp
                    )
                }
            }

            // ۲. بخش صحنه‌های سریع (Quick Scenes)
            if (state.quickScenes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Quick Scenes", style = MaterialTheme.typography.titleLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.quickScenes) { scene ->
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.executeScene(scene.id) },
                                label = { Text(scene.name) }
                            )
                        }
                    }
                }
            }

            // ۳. بخش دستگاه‌های برگزیده (Favorites)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Favorite Devices", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onSeeAllDevices) { Text("See All") }
                }

                if (state.favoriteDevices.isEmpty()) {
                    Text("No favorites yet. Add some from the devices tab!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                } else {
                    state.favoriteDevices.forEach { device ->
                        DeviceCard(device = device, onClick = { onDeviceClick(device.id) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
