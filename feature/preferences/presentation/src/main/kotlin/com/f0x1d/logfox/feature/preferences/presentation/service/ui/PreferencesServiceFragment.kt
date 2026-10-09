package com.f0x1d.logfox.feature.preferences.presentation.service.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.context.toast
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceCommand
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceState
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceViewModel
import com.f0x1d.logfox.feature.preferences.presentation.service.PreferencesServiceViewState
import com.f0x1d.logfox.feature.preferences.presentation.service.ui.compose.PreferencesServiceScreenContent
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesServiceFragment :
    BaseStoreComposeFragment<
        PreferencesServiceViewState,
        PreferencesServiceState,
        PreferencesServiceCommand,
        PreferencesServiceSideEffect,
        PreferencesServiceViewModel,
        >() {

    override val viewModel by viewModels<PreferencesServiceViewModel>()

    private var showRestartDialog by mutableStateOf(false)
    private var showAndroid13Warning by mutableStateOf(false)

    @Composable
    override fun Content(state: PreferencesServiceViewState) {
        PreferencesServiceScreenContent(
            state = state,
            onTerminalSelected = { send(PreferencesServiceCommand.TerminalSelected(it)) },
            onStartOnBootChanged = {
                send(PreferencesServiceCommand.StartOnBootChanged(it))
            },
            onShowLogsFromAppLaunchChanged = {
                send(PreferencesServiceCommand.ShowLogsFromAppLaunchChanged(it))
            },
            onBack = { findNavController().popBackStack() },
            showRestartDialog = showRestartDialog,
            onRestartDialogDismiss = { showRestartDialog = false },
            onRestartConfirmed = {
                showRestartDialog = false
                send(PreferencesServiceCommand.ConfirmRestartLogging)
            },
            showAndroid13Warning = showAndroid13Warning,
            onAndroid13WarningDismiss = { showAndroid13Warning = false },
        )
    }

    @SuppressLint("InlinedApi")
    override fun handleSideEffect(sideEffect: PreferencesServiceSideEffect) {
        when (sideEffect) {
            is PreferencesServiceSideEffect.ShowTerminalRestartDialog -> {
                showRestartDialog = true
            }

            is PreferencesServiceSideEffect.ShowTerminalUnavailableToast -> {
                requireContext().toast(Strings.terminal_unavailable)
            }

            is PreferencesServiceSideEffect.ShowAndroid13WarningDialog -> {
                showAndroid13Warning = true
            }

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }
}
