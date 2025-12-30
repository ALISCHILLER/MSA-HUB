package com.msa.msahub.core.observability

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class MetricsSnapshot(
    val queued: Long = 0,
    val sent: Long = 0,
    val ackOk: Long = 0,
    val ackFail: Long = 0,
    val flushOk: Long = 0,
    val flushFail: Long = 0
)

class MetricsStore {
    private val _state = MutableStateFlow(MetricsSnapshot())
    val state: StateFlow<MetricsSnapshot> = _state

    fun track(e: MetricsEvent) {
        _state.update { cur ->
            when (e) {
                MetricsEvent.CommandQueued -> cur.copy(queued = cur.queued + 1)
                MetricsEvent.CommandSent -> cur.copy(sent = cur.sent + 1)
                MetricsEvent.CommandAckSuccess -> cur.copy(ackOk = cur.ackOk + 1)
                MetricsEvent.CommandAckFail -> cur.copy(ackFail = cur.ackFail + 1)
                MetricsEvent.FlushSuccess -> cur.copy(flushOk = cur.flushOk + 1)
                MetricsEvent.FlushFail -> cur.copy(flushFail = cur.flushFail + 1)
            }
        }
    }

    fun reset() {
        _state.value = MetricsSnapshot()
    }
}
