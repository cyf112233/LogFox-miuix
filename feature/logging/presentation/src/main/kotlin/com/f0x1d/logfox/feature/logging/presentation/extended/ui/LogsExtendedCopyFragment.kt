package com.f0x1d.logfox.feature.logging.presentation.extended.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyCommand
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopySideEffect
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyState
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyViewModel
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class LogsExtendedCopyFragment :
    BaseStoreComposeFragment<
        LogsExtendedCopyViewState,
        LogsExtendedCopyState,
        LogsExtendedCopyCommand,
        LogsExtendedCopySideEffect,
        LogsExtendedCopyViewModel,
        >() {

    override val viewModel by viewModels<LogsExtendedCopyViewModel>()

    @Composable
    override fun Content(state: LogsExtendedCopyViewState) {
        LogsExtendedCopyScreenContent(
            state = state,
            onBack = { findNavController().navigateUp() },
        )
    }

    override fun handleSideEffect(sideEffect: LogsExtendedCopySideEffect) {
        // All side effects are business logic, handled by EffectHandler
        // No UI side effects for this screen
    }
}
