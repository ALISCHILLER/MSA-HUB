package com.msa.msahub.features.scenes.domain.usecase

import com.msa.msahub.core.common.AppError
import com.msa.msahub.core.common.Result
import com.msa.msahub.features.scenes.domain.model.Scene

class ValidateSceneUseCase {
    operator fun invoke(scene: Scene): Result<Unit> {
        if (scene.name.isBlank()) return Result.Failure(AppError.Validation("Scene name is required"))
        if (scene.actions.isEmpty()) return Result.Failure(AppError.Validation("Scene must have at least 1 action"))

        val bad = scene.actions.firstOrNull { it.deviceId.isBlank() || it.command.isBlank() }
        if (bad != null) return Result.Failure(AppError.Validation("Each action must have deviceId and command"))

        return Result.Success(Unit)
    }
}
