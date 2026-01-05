package com.msa.msahub.features.settings.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
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
                },
                actions = {
                    IconButton(onClick = { /* Reset to Defaults */ }) {
                        Icon(Icons.Outlined.RestartAlt, contentDescription = "Reset")
                    }
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

            // ۱. بخش تنظیمات پروتکل MQTT (Connection)
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
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.md)
                ) {
                    OutlinedTextField(
                        value = state.mqttPort,
                        onValueChange = viewModel::onMqttPortChange,
                        label = { Text("Port") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    Surface(
                        modifier = Modifier.weight(1.2f).height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Dimens.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.mqttUseTls,
                                onCheckedChange = viewModel::onMqttTlsChange
                            )
                            Text("Use TLS", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // ۲. بخش امنیت و اعتبارنامه (Credentials)
            SettingsSection(
                title = "Authentication",
                icon = Icons.Outlined.VpnKey,
                description = "Secure access to the MQTT broker and API."
            ) {
                OutlinedTextField(
                    value = state.mqttUsername,
                    onValueChange = viewModel::onMqttUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Person, null) },
                    singleLine = true,
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
                            Icon(
                                if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                null
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            // ۳. تنظیمات API سرور
            SettingsSection(
                title = "Backend Service",
                icon = Icons.Outlined.CloudQueue,
                description = "Manage connection to the central management API."
            ) {
                OutlinedTextField(
                    value = state.apiBaseUrl,
                    onValueChange = viewModel::onApiUrlChange,
                    label = { Text("API Base URL") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.Link, null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            // ۴. اطلاعات سیستم (Read-only)
            SettingsSection(
                title = "System Information",
                icon = Icons.Outlined.Info
            ) {
                InfoRow(label = "App Version", value = "0.10.0 (Stable)")
                InfoRow(label = "Encryption", value = "AES-256-GCM Active")
                InfoRow(label = "Hub Status", value = "Healthy")
            }

            Button(
                onClick = viewModel::saveSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = Dimens.md),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Outlined.Save, null)
                Spacer(Modifier.width(Dimens.sm))
                Text(
                    "SAVE CONFIGURATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
        
        description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(Dimens.lg),
                verticalArrangement = Arrangement.spacedBy(Dimens.md)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
