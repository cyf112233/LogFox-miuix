package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.api.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetMonetEnabledFlowUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class GetMonetEnabledFlowUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : GetMonetEnabledFlowUseCase {

    override fun invoke(): Flow<Boolean> = uiSettingsRepository.monetEnabled()
}
