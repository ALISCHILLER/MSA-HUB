package com.msa.msahub.features.automation.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomationScreen(
    viewModel: AddAutomationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // شنود Effectها برای ناوبری
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddAutomationEffect.NavigateBack -> onNavigateBack()
                is AddAutomationEffect.ShowError -> { /* نمایش اسنک‌بار */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ساخت اتوماسیون جدید") },
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
                onNameChange = { viewModel.onEvent(AddAutomationEvent.UpdateName(it)) }
            )
        }
    }
}

@Composable
fun StepContent(
    step: Int,
    state: AddAutomationState,
    onNameChange: (String) -> Unit
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
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentStep) {
                1 -> TriggerStep(state, onNameChange)
                2 -> ConditionStep(state)
                3 -> ActionStep(state)
            }
        }
    }
}

@Composable
fun TriggerStep(state: AddAutomationState, onNameChange: (String) -> Unit) {
    Text("مرحله ۱: نام و محرک", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(24.dp))
    OutlinedTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = { Text("نام اتوماسیون") },
        modifier = Modifier.fillMaxWidth()
    )
    // اینجا لیست سنسورها برای انتخاب به عنوان Trigger اضافه می‌شود
}

@Composable
fun ConditionStep(state: AddAutomationState) {
    Text("مرحله ۲: شرایط (اختیاری)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    // UI انتخاب شرط
}

@Composable
fun ActionStep(state: AddAutomationState) {
    Text("مرحله ۳: عملیات اجرایی", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    // UI انتخاب دستگاه و دستور برای اجرا
}

@Composable
fun WizardBottomBar(
    currentStep: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 1) {
                TextButton(onClick = onPrevious) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قبلی")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (currentStep < 3) {
                Button(onClick = onNext) {
                    Text("بعدی")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            } else {
                Button(
                    onClick = onSave,
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("ثبت نهایی")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }
    }
}
