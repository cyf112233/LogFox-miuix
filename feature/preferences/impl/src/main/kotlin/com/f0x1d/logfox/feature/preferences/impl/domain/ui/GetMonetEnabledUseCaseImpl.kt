package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.api.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.ui.GetMonetEnabledUseCase
import javax.inject.Inject

internal class GetMonetEnabledUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : GetMonetEnabledUseCase {

    override fun invoke(): Boolean = uiSettingsRepository.monetEnabled().value
}
