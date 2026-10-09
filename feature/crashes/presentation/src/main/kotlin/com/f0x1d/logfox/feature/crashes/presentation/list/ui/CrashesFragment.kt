package com.f0x1d.logfox.feature.crashes.presentation.list.ui

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesCommand
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesSideEffect
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesState
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewModel
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewState
import com.f0x1d.logfox.feature.crashes.presentation.list.ui.compose.CrashesScreenContent
import com.f0x1d.logfox.feature.navigation.api.Directions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CrashesFragment :
    BaseStoreComposeFragment<
        CrashesViewState,
        CrashesState,
        CrashesCommand,
        CrashesSideEffect,
        CrashesViewModel,
        >() {

    override val viewModel by hiltNavGraphViewModels<CrashesViewModel>(Directions.crashesFragment)

    @Composable
    override fun Content(state: CrashesViewState) = CrashesScreenContent(
        state = state,
        onQueryChange = { send(CrashesCommand.UpdateQuery(it)) },
        onCrashClick = {
            send(
                CrashesCommand.CrashClicked(
                    crashId = it.lastCrashId,
                    count = it.count,
                    packageName = it.packageName,
                    appName = it.appName,
                ),
            )
        },
        onCrashDelete = { send(CrashesCommand.DeleteCrashesByPackageName(it.packageName)) },
        onSortChange = { sort, reversed ->
            send(CrashesCommand.UpdateSort(sort, reversed))
        },
        onOpenBlacklist = { send(CrashesCommand.OpenBlacklist) },
        onClearAll = { send(CrashesCommand.ClearCrashes) },
    )

    override fun handleSideEffect(sideEffect: CrashesSideEffect) {
        when (sideEffect) {
            is CrashesSideEffect.NavigateToCrashDetails -> {
                findNavController().navigate(
                    resId = Directions.action_crashesFragment_to_crashDetailsFragment,
                    args = bundleOf("crash_id" to sideEffect.crashId),
                )
            }

            is CrashesSideEffect.NavigateToAppCrashes -> {
                findNavController().navigate(
                    resId = Directions.action_crashesFragment_to_appCrashesFragment,
                    args = bundleOf(
                        "package_name" to sideEffect.packageName,
                        "app_name" to sideEffect.appName,
                    ),
                )
            }

            is CrashesSideEffect.NavigateToBlacklist -> {
                findNavController().navigate(Directions.action_crashesFragment_to_appsPickerFragment)
            }

            // Business logic side effects are handled by EffectHandler
            else -> Unit
        }
    }
}
