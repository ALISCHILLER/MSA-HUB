package com.msa.msahub.features.scenes.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneEditorScreen(
    sceneId: String?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (sceneId == "new") "سناریوی جدید" else "ویرایش سناریو") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Text("این بخش در حال توسعه است...", modifier = Modifier.padding(padding))
    }
}
