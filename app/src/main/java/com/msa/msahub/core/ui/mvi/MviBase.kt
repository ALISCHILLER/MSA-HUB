package com.msa.msahub.core.ui.mvi

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel

/**
 * ساختار پایه برای پیاده‌سازی MVI-lite جهت یکپارچگی Stateها در Compose
 */
abstract class BaseViewModel<State, Event, Effect>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    protected var currentState: State
        get() = _uiState.value
        set(value) { _uiState.value = value }

    abstract fun onEvent(event: Event)

    protected suspend fun emitEffect(effect: Effect) {
        _effect.emit(effect)
    }

    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = _uiState.value.reducer()
    }
}
