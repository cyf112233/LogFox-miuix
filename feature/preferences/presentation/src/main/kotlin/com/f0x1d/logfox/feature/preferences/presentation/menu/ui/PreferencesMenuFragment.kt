package com.f0x1d.logfox.feature.preferences.presentation.menu.ui

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.context.shareFileIntent
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuCommand
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuState
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuViewModel
import com.f0x1d.logfox.feature.preferences.presentation.menu.PreferencesMenuViewState
import com.f0x1d.logfox.feature.preferences.presentation.menu.ui.compose.PreferencesMenuScreenContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesMenuFragment :
    BaseStoreComposeFragment<
        PreferencesMenuViewState,
        PreferencesMenuState,
        PreferencesMenuCommand,
        PreferencesMenuSideEffect,
        PreferencesMenuViewModel,
        >() {

    override val viewModel by viewModels<PreferencesMenuViewModel>()

    @Composable
    override fun Content(state: PreferencesMenuViewState) = PreferencesMenuScreenContent(
        state = state,
        onUISettingsClick = { send(PreferencesMenuCommand.UISettingsClicked) },
        onServiceSettingsClick = { send(PreferencesMenuCommand.ServiceSettingsClicked) },
        onCrashesSettingsClick = { send(PreferencesMenuCommand.CrashesSettingsClicked) },
        onNotificationsSettingsClick = {
            send(PreferencesMenuCommand.NotificationsSettingsClicked)
        },
        onLinksClick = { send(PreferencesMenuCommand.LinksClicked) },
        onShareLogsClick = { send(PreferencesMenuCommand.ShareLogsClicked) },
    )

    override fun handleSideEffect(sideEffect: PreferencesMenuSideEffect) {
        when (sideEffect) {
            is PreferencesMenuSideEffect.NavigateToUISettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsUIFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToServiceSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsServiceFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToCrashesSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsCrashesFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToNotificationsSettings -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsNotificationsFragment,
                )
            }

            is PreferencesMenuSideEffect.NavigateToLinks -> {
                findNavController().navigate(
                    Directions.action_settingsMenuFragment_to_settingsLinksFragment,
                )
            }

            is PreferencesMenuSideEffect.ShareLogs -> {
                requireContext().shareFileIntent(sideEffect.file)
            }
        }
    }
}
