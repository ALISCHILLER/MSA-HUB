package com.msa.msahub.features.settings.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.R
import com.msa.msahub.core.ui.design.Dimens
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHostState.showSnackbar("All settings saved and applied.")
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.xl)
        ) {
            Spacer(modifier = Modifier.height(Dimens.xs))

            // ۱. بخش کشف هاب محلی (Local Discovery)
            SettingsSection(
                title = "Local Hub Discovery",
                icon = Icons.Outlined.Search,
                description = "Scan your WiFi network for available MSA Hubs."
            ) {
                Button(
                    onClick = { if (state.isScanningLocal) viewModel.stopLocalDiscovery() else viewModel.startLocalDiscovery() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (state.isScanningLocal) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                ) {
                    Icon(if (state.isScanningLocal) Icons.Outlined.Stop else Icons.Outlined.Radar, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.isScanningLocal) "Stop Scanning" else "Start Network Scan")
                }

                if (state.discoveredHubs.isNotEmpty()) {
                    Text("Discovered Hubs:", style = MaterialTheme.typography.labelLarge)
                    state.discoveredHubs.forEach { hub ->
                        OutlinedCard(
                            onClick = { viewModel.selectDiscoveredHub(hub) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Router, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(hub.serviceName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("${hub.host.hostAddress}:${hub.port}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // ۲. تنظیمات MQTT
            SettingsSection(
                title = "MQTT Connectivity",
                icon = Icons.Outlined.Hub,
                description = "Configure how the hub connects to the IoT Broker."
            ) {
                OutlinedTextField(
                    value = state.mqttHost,
                    onValueChange = viewModel::onMqttHostChange,
                    label = { Text("Broker Address") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Dns, null) },
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = state.mqttPort,
                    onValueChange = viewModel::onMqttPortChange,
                    label = { Text("Port") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            // ۳. امنیت و احراز هویت
            SettingsSection(
                title = "Authentication",
                icon = Icons.Outlined.VpnKey
            ) {
                OutlinedTextField(
                    value = state.mqttUsername,
                    onValueChange = viewModel::onMqttUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Person, null) },
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = state.mqttPassword,
                    onValueChange = viewModel::onMqttPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null)
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            Button(
                onClick = viewModel::saveSettings,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Outlined.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("SAVE CONFIGURATION", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(Dimens.xxl))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
        description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                content()
            }
        }
    }
}
