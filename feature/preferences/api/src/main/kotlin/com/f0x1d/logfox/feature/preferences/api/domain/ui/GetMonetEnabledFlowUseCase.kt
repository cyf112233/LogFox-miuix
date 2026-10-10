package com.f0x1d.logfox.feature.preferences.api.domain.ui

import kotlinx.coroutines.flow.Flow

interface GetMonetEnabledFlowUseCase {
    operator fun invoke(): Flow<Boolean>
}
