package com.f0x1d.logfox.feature.crashes.presentation.appcrashes.ui

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesState
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewModel
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.AppCrashesViewState
import com.f0x1d.logfox.feature.crashes.presentation.appcrashes.ui.compose.AppCrashesScreenContent
import com.f0x1d.logfox.feature.navigation.api.Directions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class AppCrashesFragment :
    BaseStoreComposeFragment<
        AppCrashesViewState,
        AppCrashesState,
        AppCrashesCommand,
        AppCrashesSideEffect,
        AppCrashesViewModel,
        >() {

    override val viewModel by viewModels<AppCrashesViewModel>()

    @Composable
    override fun Content(state: AppCrashesViewState) = AppCrashesScreenContent(
        state = state,
        onBack = { findNavController().navigateUp() },
        onCrashClick = { send(AppCrashesCommand.CrashClicked(it.lastCrashId)) },
        onCrashDelete = { send(AppCrashesCommand.DeleteCrash(it.lastCrashId)) },
    )

    override fun handleSideEffect(sideEffect: AppCrashesSideEffect) {
        when (sideEffect) {
            is AppCrashesSideEffect.NavigateToCrashDetails -> {
                findNavController().navigate(
                    resId = Directions.action_appCrashesFragment_to_crashDetailsFragment,
                    args = bundleOf("crash_id" to sideEffect.crashId),
                )
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
