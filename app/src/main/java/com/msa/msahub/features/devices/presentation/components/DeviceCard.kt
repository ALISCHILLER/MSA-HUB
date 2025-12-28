package com.msa.msahub.features.devices.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msa.msahub.features.devices.domain.model.Device

@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(device.name, style = MaterialTheme.typography.titleMedium)
            Text("${device.type} • ${device.roomName ?: "No room"}", style = MaterialTheme.typography.bodySmall)
            Text("Caps: ${device.capabilities.joinToString()}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
