package com.msa.msahub.features.scenes.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.msahub.core.ui.components.LoadingView
import com.msa.msahub.features.scenes.presentation.viewmodel.SceneListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneListScreen(
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: SceneListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scenes") },
                actions = { TextButton(onClick = onCreate) { Text("New") } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> Text(state.error ?: "", Modifier.padding(16.dp))
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.scenes) { s ->
                        Card {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(s.name, style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { viewModel.run(s.id) }) { Text("Run") }
                                    OutlinedButton(onClick = { onEdit(s.id) }) { Text("Edit") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
