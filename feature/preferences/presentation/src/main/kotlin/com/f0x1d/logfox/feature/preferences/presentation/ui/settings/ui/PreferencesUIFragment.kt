package com.f0x1d.logfox.feature.preferences.presentation.ui.settings.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.compat.monetAvailable
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUICommand
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUISideEffect
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIState
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIViewModel
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIViewState
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.ui.compose.PreferencesUIScreenContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesUIFragment :
    BaseStoreComposeFragment<
        PreferencesUIViewState,
        PreferencesUIState,
        PreferencesUICommand,
        PreferencesUISideEffect,
        PreferencesUIViewModel,
        >() {

    override val viewModel by viewModels<PreferencesUIViewModel>()

    @Composable
    override fun Content(state: PreferencesUIViewState) = PreferencesUIScreenContent(
        state = state,
        monetAvailable = monetAvailable,
        onNightThemeChanged = { send(PreferencesUICommand.NightThemeChanged(it)) },
        onMonetEnabledChanged = { send(PreferencesUICommand.MonetEnabledChanged) },
        onDateFormatChanged = { send(PreferencesUICommand.DateFormatChanged(it)) },
        onTimeFormatChanged = { send(PreferencesUICommand.TimeFormatChanged(it)) },
        onLogsFormatChanged = { which, checked ->
            send(PreferencesUICommand.LogsFormatChanged(which, checked))
        },
        onLogsUpdateIntervalChanged = {
            send(PreferencesUICommand.LogsUpdateIntervalChanged(it))
        },
        onLogsTextSizeChanged = { send(PreferencesUICommand.LogsTextSizeChanged(it)) },
        onLogsDisplayLimitChanged = { send(PreferencesUICommand.LogsDisplayLimitChanged(it)) },
        onBack = { findNavController().navigateUp() },
    )

    override fun handleSideEffect(sideEffect: PreferencesUISideEffect) {
        when (sideEffect) {
            is PreferencesUISideEffect.RecreateActivity -> {
                requireActivity().recreate()
            }

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }
}
