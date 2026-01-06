package com.msa.msahub.features.scenes.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.R
import com.msa.msahub.core.ui.design.Dimens
import com.msa.msahub.features.scenes.domain.model.SceneAction
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneEditorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneEditorScreen(
    sceneId: String?,
    onBack: () -> Unit,
    viewModel: SceneEditorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(sceneId) {
        viewModel.load(sceneId)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (sceneId == null || sceneId == "new") "Create Scene" else "Edit Scene",
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = viewModel::save) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            Spacer(modifier = Modifier.height(Dimens.xs))

            // ۱. فیلد نام سناریو
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("Scene Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Default.Label, null) }
            )

            // ۲. تنظیمات وضعیت (Enabled/Disabled)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Scene Active", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Switch(checked = state.enabled, onCheckedChange = viewModel::setEnabled)
                }
            }

            Text("Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)

            // ۳. لیست اکشن‌ها
            if (state.actions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No actions added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.sm)
                ) {
                    itemsIndexed(state.actions) { index, action ->
                        ActionCard(action = action, onRemove = { viewModel.removeAction(index) })
                    }
                }
            }

            // ۴. دکمه افزودن اکشن
            Button(
                onClick = { viewModel.addAction("dev-1", "toggle", null) },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = Dimens.md),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(Dimens.sm))
                Text("ADD DEVICE ACTION")
            }
        }
    }
}

@Composable
fun ActionCard(action: SceneAction, onRemove: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(Dimens.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.SmartButton, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.md))
            Column(modifier = Modifier.weight(1f)) {
                Text("Device: ${action.deviceId}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("Action: ${action.command}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
