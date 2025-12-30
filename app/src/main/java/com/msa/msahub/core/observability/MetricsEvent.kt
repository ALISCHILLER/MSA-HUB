package com.msa.msahub.core.observability

sealed interface MetricsEvent {
    data object CommandQueued : MetricsEvent
    data object CommandSent : MetricsEvent
    data object CommandAckSuccess : MetricsEvent
    data object CommandAckFail : MetricsEvent
    data object FlushSuccess : MetricsEvent
    data object FlushFail : MetricsEvent
}
