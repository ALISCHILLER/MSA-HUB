package com.msa.msahub.features.devices.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msa.msahub.features.devices.domain.model.DeviceCapability

@Composable
fun DeviceControlsPanel(
    capabilities: Set<DeviceCapability>,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit
) {
    Card {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Controls", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onTurnOn, enabled = DeviceCapability.ON_OFF in capabilities) {
                    Text("Turn On")
                }
                OutlinedButton(onClick = onTurnOff, enabled = DeviceCapability.ON_OFF in capabilities) {
                    Text("Turn Off")
                }
            }
        }
    }
}
