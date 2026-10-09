package com.f0x1d.logfox.feature.logging.presentation.list.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.f0x1d.logfox.core.copy.copyText
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.logging.presentation.list.LogsCommand
import com.f0x1d.logfox.feature.logging.presentation.list.LogsSideEffect
import com.f0x1d.logfox.feature.logging.presentation.list.LogsState
import com.f0x1d.logfox.feature.logging.presentation.list.LogsViewModel
import com.f0x1d.logfox.feature.logging.presentation.list.LogsViewState
import com.f0x1d.logfox.feature.navigation.api.Directions
import com.f0x1d.logfox.feature.strings.Strings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.SnackbarHostState

@AndroidEntryPoint
internal class LogsFragment :
    BaseStoreComposeFragment<
        LogsViewState,
        LogsState,
        LogsCommand,
        LogsSideEffect,
        LogsViewModel,
        >() {

    override val viewModel by viewModels<LogsViewModel>()

    private val snackbarHostState = SnackbarHostState()

    /**
     * While lines are selected, a system back press clears the selection instead of leaving the
     * screen - the same behaviour the toolbar close button has.
     */
    private val clearSelectionOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            send(LogsCommand.ClearSelection)
        }
    }

    private val exportLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(LogsCommand.ExportSelectedTo(uri)) }
    }

    private val saveCurrentLogsLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/*"),
    ) {
        it?.let { uri -> send(LogsCommand.SaveCurrentLogsTo(uri)) }
    }

    override fun onContentViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            clearSelectionOnBackPressedCallback,
        )
    }

    @Composable
    override fun Content(state: LogsViewState) {
        clearSelectionOnBackPressedCallback.isEnabled = state.selecting

        LogsScreenContent(
            state = state,
            send = ::send,
            snackbarHostState = snackbarHostState,
        )
    }

    override fun handleSideEffect(sideEffect: LogsSideEffect) {
        when (sideEffect) {
            is LogsSideEffect.NavigateToRecordings -> {
                findNavController().navigate(Directions.action_global_recordingsFragment)
            }

            is LogsSideEffect.NavigateToSearch -> {
                findNavController().navigate(Directions.action_logsFragment_to_searchBottomSheet)
            }

            is LogsSideEffect.OpenFilters -> {
                findNavController().navigate(Directions.action_logsFragment_to_filtersFragment)
            }

            is LogsSideEffect.NavigateToExtendedCopy -> {
                findNavController().navigate(Directions.action_logsFragment_to_logsExtendedCopyFragment)
            }

            is LogsSideEffect.OpenEditFilter -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf("filter_id" to sideEffect.filterId),
                )
            }

            is LogsSideEffect.OpenEditFilterFromLogLine -> {
                findNavController().navigate(
                    resId = Directions.action_filtersFragment_to_editFilterFragment,
                    args = bundleOf(
                        "log_uid" to sideEffect.uid,
                        "log_pid" to sideEffect.pid,
                        "log_tid" to sideEffect.tid,
                        "log_package_name" to sideEffect.packageName,
                        "log_tag" to sideEffect.tag,
                        "log_content" to sideEffect.content,
                        "log_level" to sideEffect.level.ordinal,
                    ),
                )
            }

            is LogsSideEffect.CopyText -> {
                requireContext().copyText(sideEffect.text)
                viewLifecycleOwner.lifecycleScope.launch {
                    snackbarHostState.showSnackbar(requireContext().getString(Strings.text_copied))
                }
            }

            is LogsSideEffect.LaunchExportPicker -> {
                exportLogsLauncher.launch(sideEffect.filename)
            }

            is LogsSideEffect.LaunchSaveCurrentPicker -> {
                saveCurrentLogsLauncher.launch(sideEffect.filename)
            }

            else -> Unit
        }
    }
}
