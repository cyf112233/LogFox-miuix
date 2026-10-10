package com.f0x1d.logfox.feature.preferences.impl.domain.ui

import com.f0x1d.logfox.feature.preferences.api.data.UISettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.ui.SetMonetEnabledUseCase
import javax.inject.Inject

internal class SetMonetEnabledUseCaseImpl @Inject constructor(
    private val uiSettingsRepository: UISettingsRepository,
) : SetMonetEnabledUseCase {

    override fun invoke(enabled: Boolean) {
        uiSettingsRepository.monetEnabled().set(enabled)
    }
}
