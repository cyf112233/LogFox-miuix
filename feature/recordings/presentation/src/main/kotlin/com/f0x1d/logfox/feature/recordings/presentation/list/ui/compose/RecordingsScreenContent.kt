package com.f0x1d.logfox.feature.recordings.presentation.list.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.base.preview.DayNightPreview
import com.f0x1d.logfox.compose.designsystem.Icons
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.compose.designsystem.theme.LogFoxTheme
import com.f0x1d.logfox.feature.recordings.presentation.list.RecordingsViewState
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.MockRecordingsScreenListener
import com.f0x1d.logfox.feature.recordings.presentation.list.ui.RecordingsScreenListener
import com.f0x1d.logfox.feature.recordings.api.data.RecordingState
import com.f0x1d.logfox.feature.recordings.presentation.model.LogRecordingItem
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Clear
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun RecordingsScreenContent(
    state: RecordingsViewState,
    listener: RecordingsScreenListener = MockRecordingsScreenListener,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = MiuixScrollBehavior()

    var showOverflowMenu by remember { mutableStateOf(false) }
    var recordingPendingDeletion by remember { mutableStateOf<LogRecordingItem?>(null) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = stringResource(Strings.recordings),
                actions = {
                    IconButton(onClick = { showClearConfirmation = true }) {
                        Icon(
                            imageVector = MiuixIcons.Clear,
                            contentDescription = stringResource(Strings.clear),
                        )
                    }

                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = stringResource(Strings.more),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            ),
            overscrollEffect = null,
        ) {
            item(key = "top_spacer") { Spacer(modifier = Modifier.size(12.dp)) }

            item(key = "controls") {
                RecordingControlsItem(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    recordingState = state.recordingState,
                    onStartStopClick = listener.onStartStopClick,
                    onPauseResumeClick = listener.onPauseResumeClick,
                )
            }

            if (state.recordings.isEmpty()) {
                item(key = "placeholder") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        ListPlaceholder(
                            iconResId = Icons.ic_recording,
                            text = {
                                Text(
                                    text = stringResource(Strings.no_recordings),
                                    style = MiuixTheme.textStyles.body1,
                                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                                )
                            },
                        )
                    }
                }
            }

            items(
                items = state.recordings,
                key = { item -> item.recordingId },
            ) { item ->
                RecordingItem(
                    item = item,
                    onRecordingClick = listener.onRecordingClick,
                    onRecordingDeleteClick = { recordingPendingDeletion = it },
                )
            }
        }

        // Popups and dialogs live inside the Scaffold but outside of the LazyColumn.
        AnchorDropdownMenu(
            show = showOverflowMenu,
            items = listOf(
                DropdownItem(
                    text = stringResource(Strings.save_all_logs),
                    onClick = listener.onSaveAllClick,
                ),
            ),
            onDismissFinished = { showOverflowMenu = false },
        )

        recordingPendingDeletion?.let { item ->
            ConfirmationDialog(
                title = stringResource(Strings.delete),
                message = stringResource(Strings.delete_warning),
                onConfirm = {
                    listener.onRecordingDeleteClick(item)
                    recordingPendingDeletion = null
                },
                onDismiss = { recordingPendingDeletion = null },
            )
        }

        if (showClearConfirmation) {
            ConfirmationDialog(
                title = stringResource(Strings.clear),
                message = stringResource(Strings.clear_warning),
                onConfirm = {
                    listener.onClearClick()
                    showClearConfirmation = false
                },
                onDismiss = { showClearConfirmation = false },
            )
        }
    }
}

@Composable
private fun RecordingItem(
    item: LogRecordingItem,
    modifier: Modifier = Modifier,
    onRecordingClick: (LogRecordingItem) -> Unit = { },
    onRecordingDeleteClick: (LogRecordingItem) -> Unit = { },
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
    ) {
        BasicComponent(
            title = item.title,
            summary = item.formattedDate,
            startAction = {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(Icons.ic_recording),
                    contentDescription = null,
                )
            },
            endActions = {
                IconButton(onClick = { onRecordingDeleteClick(item) }) {
                    Icon(
                        imageVector = MiuixIcons.Delete,
                        tint = MiuixTheme.colorScheme.error,
                        contentDescription = stringResource(Strings.delete),
                    )
                }
            },
            onClick = { onRecordingClick(item) },
        )
    }
}

/**
 * Dropdown anchored to the top end of the screen - the Miuix replacement of the Material3
 * `DropdownMenu`. It must stay a sibling of the `LazyColumn` inside the `Scaffold`.
 */
@Composable
private fun AnchorDropdownMenu(
    items: List<DropdownItem>,
    onDismissFinished: () -> Unit,
    show: Boolean = true,
) {
    var visible by remember(show) { mutableStateOf(show) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(0.dp),
        )

        OverlayListPopup(
            show = visible,
            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
            alignment = PopupPositionProvider.Align.TopEnd,
            onDismissRequest = {
                visible = false
                onDismissFinished()
            },
            onDismissFinished = onDismissFinished,
        ) {
            ListPopupColumn {
                items.forEachIndexed { index, item ->
                    DropdownImpl(
                        item = item,
                        optionSize = items.size,
                        isSelected = false,
                        index = index,
                        onSelectedIndexChange = {
                            visible = false
                            onDismissFinished()
                        },
                    )
                }
            }
        }
    }
}

/** Two button confirmation dialog - the Miuix replacement of the old `MaterialAlertDialogBuilder`. */
@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    OverlayDialog(
        show = true,
        title = title,
        summary = message,
        onDismissRequest = onDismiss,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                text = stringResource(Strings.no),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )

            TextButton(
                text = stringResource(Strings.yes),
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}

internal val MockRecordingsState = RecordingsViewState(
    recordings = listOf(
        LogRecordingItem(
            recordingId = 0,
            title = "Cool",
            formattedDate = "01/01/1970 00:00",
        ),
        LogRecordingItem(
            recordingId = 1,
            title = "Cool",
            formattedDate = "01/01/1970 00:00",
        ),
    ),
    recordingState = RecordingState.RECORDING,
)

@DayNightPreview
@Composable
private fun ScreenContentPreview() = LogFoxTheme {
    RecordingsScreenContent(
        state = MockRecordingsState,
    )
}
