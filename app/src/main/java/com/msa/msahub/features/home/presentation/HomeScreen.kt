package com.msa.msahub.features.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.R
import com.msa.msahub.core.ui.components.FullscreenLoading
import com.msa.msahub.core.ui.design.Dimens
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
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Welcome back, Admin",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Alerts */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Alerts")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FullscreenLoading()
            }

            AnimatedVisibility(
                visible = !state.isLoading,
                enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.lg)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Dimens.xl)
                ) {
                    Spacer(modifier = Modifier.height(Dimens.xs))

                    SystemStatusSection(state.onlineCount, state.totalCount)

                    if (state.quickScenes.isNotEmpty()) {
                        SectionHeader(title = stringResource(R.string.quick_scenes))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.md),
                            contentPadding = PaddingValues(bottom = Dimens.sm)
                        ) {
                            items(state.quickScenes) { scene ->
                                SuggestionChip(
                                    onClick = { viewModel.executeScene(scene.id) },
                                    label = { Text(scene.name) },
                                    shape = MaterialTheme.shapes.medium
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                        SectionHeader(
                            title = stringResource(R.string.favorite_devices),
                            actionText = stringResource(R.string.see_all),
                            onActionClick = onSeeAllDevices
                        )

                        if (state.favoriteDevices.isEmpty()) {
                            EmptyFavoritesPlaceholder()
                        } else {
                            state.favoriteDevices.forEach { device ->
                                DeviceCard(device = device, onClick = { onDeviceClick(device.id) })
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.xl))
                }
            }
        }
    }
}

@Composable
private fun SystemStatusSection(online: Int, total: Int) {
    val isDegraded = online < total && total > 0
    val cardColor = if (isDegraded) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val icon: ImageVector = if (isDegraded) Icons.Outlined.Error else Icons.Outlined.CheckCircle
    val statusText = if (isDegraded) "Action Required" else "All Systems Nominal"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(Dimens.xl),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    statusText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColorFor(cardColor)
                )
                Text(
                    stringResource(R.string.devices_online, online, total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColorFor(cardColor).copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = contentColorFor(cardColor)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyFavoritesPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.no_favorites),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
