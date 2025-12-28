package com.msa.msahub.features.devices.domain.usecase

import com.msa.msahub.core.common.Result
import com.msa.msahub.features.devices.data.sync.OfflineCommandOutbox

class FlushOfflineCommandsUseCase(
    private val outbox: OfflineCommandOutbox
) {
    suspend operator fun invoke(max: Int = 50): Result<Int> = outbox.flush(max)
}
