package com.msa.msahub.features.automation.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.msa.msahub.features.automation.domain.model.*
import com.msa.msahub.features.devices.domain.model.Device
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomationScreen(
    viewModel: AddAutomationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddAutomationEffect.NavigateBack -> onNavigateBack()
                is AddAutomationEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Create Automation", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            WizardBottomBar(
                currentStep = state.currentStep,
                onNext = { viewModel.onEvent(AddAutomationEvent.NextStep) },
                onPrevious = { viewModel.onEvent(AddAutomationEvent.PreviousStep) },
                onSave = { viewModel.onEvent(AddAutomationEvent.Save) },
                isSaving = state.isSaving
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LinearProgressIndicator(
                progress = { state.currentStep / 3f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            
            StepContent(
                step = state.currentStep,
                state = state,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun StepContent(
    step: Int,
    state: AddAutomationState,
    viewModel: AddAutomationViewModel
) {
    AnimatedContent(
        targetState = step,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            }
        }, label = "StepTransition"
    ) { currentStep ->
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.lg, vertical = Dimens.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            when (currentStep) {
                1 -> TriggerStep(state, viewModel)
                2 -> ConditionStep(state, viewModel)
                3 -> ActionStep(state, viewModel)
            }
        }
    }
}

@Composable
fun TriggerStep(state: AddAutomationState, viewModel: AddAutomationViewModel) {
    Text("Name & Triggers", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    OutlinedTextField(
        value = state.name,
        onValueChange = { viewModel.onEvent(AddAutomationEvent.UpdateName(it)) },
        label = { Text("Automation Name") },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    )

    Divider(modifier = Modifier.padding(vertical = Dimens.sm))

    Text("Triggers", style = MaterialTheme.typography.titleMedium)
    
    if (state.triggers.isEmpty()) {
        Text("No triggers added. Add at least one.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }

    state.triggers.forEachIndexed { index, trigger ->
        TriggerItem(trigger) { viewModel.onEvent(AddAutomationEvent.RemoveTrigger(index)) }
    }

    OutlinedButton(
        onClick = { 
            viewModel.onEvent(AddAutomationEvent.AddTrigger(AutomationTrigger.TimeSchedule("0 8 * * *")))
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Add, null)
        Spacer(Modifier.width(8.dp))
        Text("Add Trigger")
    }
}

@Composable
fun TriggerItem(trigger: AutomationTrigger, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(
                text = when (trigger) {
                    is AutomationTrigger.TimeSchedule -> "Time: ${trigger.cronExpression}"
                    is AutomationTrigger.DeviceStateChanged -> "Device State Change"
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ConditionStep(state: AddAutomationState, viewModel: AddAutomationViewModel) {
    Text("Conditions (Optional)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Text("Add rules that must be true for the automation to run.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    
    Icon(Icons.Default.Tune, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
}

@Composable
fun ActionStep(state: AddAutomationState, viewModel: AddAutomationViewModel) {
    Text("Actions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    if (state.actions.isEmpty()) {
        Text("Add at least one action to execute.", color = MaterialTheme.colorScheme.error)
    }

    state.actions.forEach { action ->
        ActionItem(action)
    }

    OutlinedButton(
        onClick = {
            if (state.availableDevices.isNotEmpty()) {
                val firstDev = state.availableDevices.first()
                viewModel.onEvent(AddAutomationEvent.AddAction(AutomationAction(firstDev.id, "toggle")))
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.FlashOn, null)
        Spacer(Modifier.width(8.dp))
        Text("Add Action")
    }
}

@Composable
fun ActionItem(action: AutomationAction) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.SmartButton, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Text("Send '${action.command}' to Device ${action.deviceId}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WizardBottomBar(
    currentStep: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    Surface(tonalElevation = 4.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.lg),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 1) {
                OutlinedButton(onClick = onPrevious) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (currentStep < 3) {
                Button(onClick = onNext) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            } else {
                Button(
                    onClick = onSave,
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Finish")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }
    }
}
