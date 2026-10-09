package com.f0x1d.logfox.feature.preferences.presentation.links.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksCommand
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksState
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksViewModel
import com.f0x1d.logfox.feature.preferences.presentation.links.PreferencesLinksViewState
import com.f0x1d.logfox.feature.preferences.presentation.links.ui.compose.PreferencesLinksScreenContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesLinksFragment :
    BaseStoreComposeFragment<
        PreferencesLinksViewState,
        PreferencesLinksState,
        PreferencesLinksCommand,
        PreferencesLinksSideEffect,
        PreferencesLinksViewModel,
        >() {

    override val viewModel by viewModels<PreferencesLinksViewModel>()

    @Composable
    override fun Content(state: PreferencesLinksViewState) = PreferencesLinksScreenContent(
        onBack = { findNavController().navigateUp() },
    )

    override fun handleSideEffect(sideEffect: PreferencesLinksSideEffect) = Unit
}
