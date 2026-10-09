package com.f0x1d.logfox.feature.filters.presentation.list.ui

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersCommand
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersSideEffect
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersState
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersViewModel
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersViewState
import com.f0x1d.logfox.feature.filters.presentation.list.ui.compose.FiltersScreenContent
import com.f0x1d.logfox.feature.navigation.api.Directions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FiltersFragment :
    BaseStoreComposeFragment<
        FiltersViewState,
        FiltersState,
        FiltersCommand,
        FiltersSideEffect,
        FiltersViewModel,
        >() {

    override val viewModel by viewModels<FiltersViewModel>()

    private val importFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { send(FiltersCommand.Import(it)) }
    }

    private val exportFiltersLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { send(FiltersCommand.ExportAll(it)) }
    }

    @Composable
    override fun Content(state: FiltersViewState) = FiltersScreenContent(
        state = state,
        onBack = { findNavController().navigateUp() },
        onCreateFilter = { send(FiltersCommand.CreateNewFilter) },
        onOpenFilter = { send(FiltersCommand.OpenFilter(it.id)) },
        onDeleteFilter = { send(FiltersCommand.Delete(it)) },
        onSwitchFilter = { filter, checked -> send(FiltersCommand.Switch(filter, checked)) },
        onClearAll = { send(FiltersCommand.ClearAll) },
        onImportFilters = { importFiltersLauncher.launch(arrayOf("application/json", "*/*")) },
        onExportAllFilters = { exportFiltersLauncher.launch("filters.json") },
    )

    override fun handleSideEffect(sideEffect: FiltersSideEffect) {
        when (sideEffect) {
            is FiltersSideEffect.NavigateToEditFilter -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf("filter_id" to sideEffect.filterId),
                )
            }

            is FiltersSideEffect.NavigateToCreateFilter -> {
                findNavController().navigate(Directions.action_filtersFragment_to_editFilterFragment)
            }

            // Business logic side effects - handled by EffectHandler
            else -> Unit
        }
    }
}
