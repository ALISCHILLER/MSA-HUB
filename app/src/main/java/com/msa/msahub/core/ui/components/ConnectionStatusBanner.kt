package com.msa.msahub.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionState
import org.koin.compose.koinInject

@Composable
fun ConnectionStatusBanner() {
    val mqttClient: MqttClient = koinInject()
    val state by mqttClient.connectionState.collectAsState()

    AnimatedVisibility(
        visible = state !is MqttConnectionState.Connected,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val (text, color) = when (state) {
            is MqttConnectionState.Connecting -> "Connecting to Hub..." to Color(0xFFFFB300)
            is MqttConnectionState.Failed -> "Connection Failed" to MaterialTheme.colorScheme.error
            else -> "Disconnected" to Color.Gray
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
