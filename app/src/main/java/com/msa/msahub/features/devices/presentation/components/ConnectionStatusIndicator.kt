package com.msa.msahub.features.devices.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConnectionStatusIndicator(isOnline: Boolean) {
    Text(
        text = if (isOnline) "Online" else "Offline",
        style = MaterialTheme.typography.titleSmall
    )
}
