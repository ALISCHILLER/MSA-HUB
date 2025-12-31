package com.msa.msahub.features.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHostState.showSnackbar("Settings saved successfully")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("General Settings") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("MQTT Configuration", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(
                value = state.mqttHost,
                onValueChange = viewModel::onMqttHostChange,
                label = { Text("MQTT Broker Host") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.mqttPort,
                onValueChange = viewModel::onMqttPortChange,
                label = { Text("MQTT Broker Port") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            Text("API Configuration", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = state.apiBaseUrl,
                onValueChange = viewModel::onApiUrlChange,
                label = { Text("API Base URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.authToken,
                onValueChange = viewModel::onTokenChange,
                label = { Text("Auth Token") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::saveSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
