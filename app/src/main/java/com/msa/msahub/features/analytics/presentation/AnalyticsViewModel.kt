package com.msa.msahub.features.analytics.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AnalyticsUiState())
    val state = _state.asStateFlow()
}

data class AnalyticsUiState(
    val trends: List<Any> = emptyList(),
    val isLoading: Boolean = false
)
