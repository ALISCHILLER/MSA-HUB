package com.msa.msahub.features.devices.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.R
import com.msa.msahub.core.ui.components.FullscreenLoading
import com.msa.msahub.core.ui.components.FullscreenError
import com.msa.msahub.core.ui.design.Dimens
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

    LaunchedEffect(Unit) { 
        viewModel.onEvent(DeviceListUiEvent.Load) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.nav_devices),
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(DeviceListUiEvent.Refresh) }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to Add Device Screen */ },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Device")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ListState"
            ) { targetState ->
                when {
                    targetState.isLoading -> FullscreenLoading()
                    targetState.errorMessage != null -> {
                        FullscreenError(
                            title = stringResource(R.string.devices_error_title),
                            message = targetState.errorMessage ?: "",
                            retryText = stringResource(R.string.common_retry),
                            onRetry = { viewModel.onEvent(DeviceListUiEvent.Refresh) }
                        )
                    }
                    targetState.items.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.devices_empty_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = Dimens.lg, vertical = Dimens.md),
                            verticalArrangement = Arrangement.spacedBy(Dimens.md),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(targetState.items, key = { it.id }) { d ->
                                DeviceCard(device = d, onClick = { onDeviceClick(d.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
