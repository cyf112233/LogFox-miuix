package com.f0x1d.logfox.feature.recordings.presentation.list.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.f0x1d.logfox.core.tea.BaseStoreComposeFragment
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsCommand
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsSideEffect
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsState
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsViewModel
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsViewState
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.compose.RecordingsScreenContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.SnackbarHostState

@AndroidEntryPoint
internal class RecordingsFragment :
    BaseStoreComposeFragment<
        RecordingsViewState,
        RecordingsState,
        RecordingsCommand,
        RecordingsSideEffect,
        RecordingsViewModel,
        >() {

    override val viewModel by viewModels<RecordingsViewModel>()

    private val snackbarHostState = SnackbarHostState()

    @Composable
    override fun Content(state: RecordingsViewState) {
        val listener = remember {
            RecordingsScreenListener(
                onRecordingClick = { send(RecordingsCommand.OpenRecordingDetails(it.recordingId)) },
                onRecordingDeleteClick = { send(RecordingsCommand.Delete(it.recordingId)) },
                onStartStopClick = { send(RecordingsCommand.ToggleStartStop) },
                onPauseResumeClick = { send(RecordingsCommand.TogglePauseResume) },
                onClearClick = { send(RecordingsCommand.ClearRecordings) },
                onSaveAllClick = { send(RecordingsCommand.SaveAll) },
            )
        }

        RecordingsScreenContent(
            state = state,
            listener = listener,
            snackbarHostState = snackbarHostState,
        )
    }

    override fun handleSideEffect(sideEffect: RecordingsSideEffect) {
        when (sideEffect) {
            is RecordingsSideEffect.ShowSnackbar -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    snackbarHostState.showSnackbar(sideEffect.text)
                }
            }

            // UI side effect that the current screen does not navigate on (unchanged behaviour).
            is RecordingsSideEffect.OpenRecording -> Unit

            // Business logic side effects - handled by EffectHandler, ignored here
            else -> Unit
        }
    }
}
