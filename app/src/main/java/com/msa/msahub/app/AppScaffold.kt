package com.msa.msahub.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppScaffold(content: @Composable () -> Unit) {
    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
