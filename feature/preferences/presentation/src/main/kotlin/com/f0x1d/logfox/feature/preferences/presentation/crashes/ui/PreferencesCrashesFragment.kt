package com.f0x1d.logfox.feature.preferences.presentation.crashes.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesCommand
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesState
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesViewModel
import com.f0x1d.logfox.feature.preferences.presentation.crashes.PreferencesCrashesViewState
import com.f0x1d.logfox.feature.preferences.presentation.crashes.ui.compose.PreferencesCrashesScreenContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesCrashesFragment :
    BaseStoreComposeFragment<
        PreferencesCrashesViewState,
        PreferencesCrashesState,
        PreferencesCrashesCommand,
        PreferencesCrashesSideEffect,
        PreferencesCrashesViewModel,
        >() {

    override val viewModel by viewModels<PreferencesCrashesViewModel>()

    @Composable
    override fun Content(state: PreferencesCrashesViewState) = PreferencesCrashesScreenContent(
        onBack = { findNavController().navigateUp() },
    )

    override fun handleSideEffect(sideEffect: PreferencesCrashesSideEffect) = Unit
}
