package com.f0x1d.logfox.di

import com.f0x1d.logfox.core.ui.base.ThemeSettingsProvider
import com.f0x1d.logfox.feature.preferences.api.data.UISettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow

@Module
@InstallIn(SingletonComponent::class)
internal object ThemeSettingsProviderModule {
    @Provides
    @Singleton
    fun provideThemeSettingsProvider(
        uiSettingsRepository: UISettingsRepository,
    ): ThemeSettingsProvider {
        val monetEnabled = uiSettingsRepository.monetEnabled()

        return object : ThemeSettingsProvider {
            override val monetEnabled: StateFlow<Boolean> = monetEnabled
        }
    }
}
