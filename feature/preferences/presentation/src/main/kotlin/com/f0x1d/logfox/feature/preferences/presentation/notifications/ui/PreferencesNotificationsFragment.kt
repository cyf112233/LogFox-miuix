package com.f0x1d.logfox.feature.preferences.presentation.notifications.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.notifications.api.LOGGING_STATUS_CHANNEL_ID
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsCommand
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsSideEffect
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsState
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsViewModel
import com.f0x1d.logfox.feature.preferences.presentation.notifications.PreferencesNotificationsViewState
import com.f0x1d.logfox.feature.preferences.presentation.notifications.ui.compose.PreferencesNotificationsScreenContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PreferencesNotificationsFragment :
    BaseStoreComposeFragment<
        PreferencesNotificationsViewState,
        PreferencesNotificationsState,
        PreferencesNotificationsCommand,
        PreferencesNotificationsSideEffect,
        PreferencesNotificationsViewModel,
        >() {

    override val viewModel by viewModels<PreferencesNotificationsViewModel>()

    override fun onStart() {
        super.onStart()
        send(PreferencesNotificationsCommand.CheckPermission)
    }

    @Composable
    override fun Content(state: PreferencesNotificationsViewState) =
        PreferencesNotificationsScreenContent(
            state = state,
            onOpenLoggingNotificationSettings = {
                send(PreferencesNotificationsCommand.OpenLoggingNotificationSettings)
            },
            onOpenNotificationsPermissionSettings = {
                send(PreferencesNotificationsCommand.OpenNotificationsPermissionSettings)
            },
            onBack = { findNavController().navigateUp() },
        )

    @SuppressLint("InlinedApi")
    override fun handleSideEffect(sideEffect: PreferencesNotificationsSideEffect) {
        when (sideEffect) {
            is PreferencesNotificationsSideEffect.OpenLoggingChannelSettings -> {
                startActivity(
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, LOGGING_STATUS_CHANNEL_ID)
                    },
                )
            }

            is PreferencesNotificationsSideEffect.OpenAppNotificationSettings -> {
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    },
                )
            }

            // Business logic side effects - handled by EffectHandler
            is PreferencesNotificationsSideEffect.CheckPermission -> Unit
        }
    }
}
