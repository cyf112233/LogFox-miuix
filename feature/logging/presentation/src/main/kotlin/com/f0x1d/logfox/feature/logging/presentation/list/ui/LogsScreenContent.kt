package com.f0x1d.logfox.feature.logging.presentation.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.logging.presentation.list.LogsCommand
import com.f0x1d.logfox.feature.logging.presentation.list.LogsViewState
import com.f0x1d.logfox.feature.logging.presentation.list.model.LogLineItem
import com.f0x1d.logfox.feature.strings.Plurals
import com.f0x1d.logfox.feature.strings.Strings
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.icon.extended.Filter
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Pause
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.SelectAll
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * Stateless logs screen.
 *
 * The list is virtualised with a [LazyColumn] keyed by the log line id, and the auto-scroll
 * behaviour of the former `RecyclerView` is preserved:
 *
 * * while not paused the list follows the newest line (jump, never animate - lines arrive fast);
 * * a user scroll that leaves the bottom pauses the stream;
 * * scrolling back to the bottom resumes it when the corresponding preference is enabled;
 * * the scroll-to-bottom button either resumes or jumps back to the bottom while staying paused.
 */
@Composable
internal fun LogsScreenContent(
    state: LogsViewState,
    send: (LogsCommand) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()

    // Long pressed line id: drives the row context menu.
    var contextMenuLogLineId by remember { mutableStateOf<Long?>(null) }
    var showLogsMenu by remember { mutableStateOf(false) }
    var showSelectionMenu by remember { mutableStateOf(false) }
    // Incremented by the scroll-to-bottom button when the auto-scroll has to stay paused.
    var scrollToBottomRequests by remember { mutableIntStateOf(0) }

    val subtitle = buildString {
        if (state.query != null) {
            append(state.query)

            if (state.filters.isNotEmpty()) {
                append(", ")
            }
        }

        if (state.filters.isNotEmpty()) {
            append(pluralStringResource(Plurals.filters_count, state.filters.size, state.filters.size))
        }
    }

    // Follow the newest line while the stream is not paused. `scrollToItem` (no animation) keeps up
    // with the high incoming line rate; `logsChanged` is the trigger so the effect is restarted once
    // per update instead of on every recomposition.
    LaunchedEffect(state.logsChanged, state.paused) {
        if (state.paused || state.logs.isEmpty() || listState.isScrollInProgress) return@LaunchedEffect
        listState.scrollToItem(state.logs.lastIndex)
    }

    // Scroll-to-bottom button, used while the stream stays paused.
    LaunchedEffect(scrollToBottomRequests) {
        if (scrollToBottomRequests == 0 || state.logs.isEmpty()) return@LaunchedEffect
        listState.scrollToItem(state.logs.lastIndex)
    }

    // "Scrolled up pauses logging" / "reaching the bottom resumes it". The flow is edge triggered
    // (distinctUntilChanged), so the stream of incoming lines cannot start a Pause command storm.
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalCount = layoutInfo.totalItemsCount
            val atBottom = totalCount == 0 ||
                (lastVisibleIndex >= totalCount - 1 && !listState.canScrollForward)
            Triple(atBottom, listState.isScrollInProgress, totalCount)
        }
            .distinctUntilChanged()
            .collect { (atBottom, scrolling, totalCount) ->
                // While the list is still empty there is nothing to pause for.
                if (!scrolling || totalCount == 0) return@collect

                if (atBottom) {
                    if (state.paused && state.resumeLoggingWithBottomTouch) send(LogsCommand.Resume)
                } else {
                    send(LogsCommand.Pause)
                }
            }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = if (state.selecting) {
                    pluralStringResource(Plurals.selected_count, state.selectedCount, state.selectedCount)
                } else {
                    stringResource(Strings.app_name)
                },
                subtitle = subtitle,
                navigationIcon = {
                    if (state.selecting) {
                        IconButton(onClick = { send(LogsCommand.ClearSelection) }) {
                            Icon(
                                imageVector = MiuixIcons.Close,
                                contentDescription = stringResource(Strings.clear),
                            )
                        }
                    }
                },
                actions = {
                    if (state.selecting) {
                        IconButton(
                            onClick = {
                                send(LogsCommand.SelectAll(state.logs.mapTo(mutableSetOf()) { it.logLineId }))
                            },
                        ) {
                            Icon(
                                imageVector = MiuixIcons.SelectAll,
                                contentDescription = stringResource(Strings.select_all),
                            )
                        }

                        IconButton(onClick = { showSelectionMenu = true }) {
                            Icon(
                                imageVector = MiuixIcons.More,
                                contentDescription = stringResource(Strings.selected),
                            )
                        }
                    } else {
                        IconButton(onClick = { send(LogsCommand.SwitchState) }) {
                            Icon(
                                imageVector = if (state.paused) MiuixIcons.Play else MiuixIcons.Pause,
                                contentDescription = stringResource(
                                    if (state.paused) Strings.resume else Strings.pause,
                                ),
                            )
                        }

                        IconButton(onClick = { send(LogsCommand.OpenSearch) }) {
                            Icon(
                                imageVector = MiuixIcons.Search,
                                contentDescription = stringResource(Strings.search),
                            )
                        }

                        IconButton(onClick = { send(LogsCommand.OpenFiltersScreen) }) {
                            Icon(
                                imageVector = MiuixIcons.Filter,
                                contentDescription = stringResource(Strings.filters),
                            )
                        }

                        IconButton(onClick = { showLogsMenu = true }) {
                            Icon(
                                imageVector = MiuixIcons.More,
                                contentDescription = stringResource(Strings.more),
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (state.paused) {
                FloatingActionButton(
                    onClick = {
                        if (state.resumeLoggingWithBottomTouch) {
                            send(LogsCommand.Resume)
                        } else {
                            scrollToBottomRequests++
                        }
                    },
                ) {
                    Icon(
                        imageVector = MiuixIcons.ExpandMore,
                        contentDescription = stringResource(Strings.scroll_to_bottom),
                    )
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
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
            if (state.logs.isEmpty()) {
                item(key = "placeholder") {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        ListPlaceholder(
                            iconResId = Icons.ic_bug,
                            text = {
                                Text(
                                    text = stringResource(
                                        if (subtitle.isEmpty()) {
                                            Strings.waiting_for_logs
                                        } else {
                                            Strings.all_logs_were_filtered_out
                                        },
                                    ),
                                    style = MiuixTheme.textStyles.body1,
                                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                                )
                            },
                        )
                    }
                }
            }

            items(
                items = state.logs,
                key = { item -> item.logLineId },
            ) { item ->
                LogLineRow(
                    item = item,
                    onClick = { send(LogsCommand.ItemClicked(item.logLineId)) },
                    onLongClick = { contextMenuLogLineId = item.logLineId },
                )
            }
        }

        // Popups must be siblings of the LazyColumn inside the Scaffold, never inside a lazy item:
        // a lazy item would be recycled away and the popup could never be shown.
        contextMenuLogLineId?.let { logLineId ->
            AnchorDropdownMenu(
                items = listOf(
                    DropdownItem(
                        text = stringResource(Strings.select),
                        onClick = { send(LogsCommand.SelectLine(logLineId, true)) },
                    ),
                    DropdownItem(
                        // `Strings` has no copy entry (the old menu used `@android:string/copy`).
                        text = stringResource(android.R.string.copy),
                        onClick = { send(LogsCommand.CopyLog(logLineId)) },
                    ),
                    DropdownItem(
                        text = stringResource(Strings.create_filter),
                        onClick = { send(LogsCommand.CreateFilterFromLog(logLineId)) },
                    ),
                ),
                onDismissFinished = { contextMenuLogLineId = null },
            )
        }

        AnchorDropdownMenu(
            show = showLogsMenu,
            items = listOf(
                DropdownItem(
                    text = stringResource(Strings.save_all_logs),
                    onClick = { send(LogsCommand.SaveCurrentLogsClicked) },
                ),
                DropdownItem(
                    text = stringResource(Strings.clear),
                    onClick = { send(LogsCommand.ClearLogs) },
                ),
                DropdownItem(
                    text = stringResource(Strings.restart_logging),
                    onClick = { send(LogsCommand.RestartLogging) },
                ),
                DropdownItem(
                    text = stringResource(Strings.exit),
                    onClick = { send(LogsCommand.KillService) },
                ),
            ),
            onDismissFinished = { showLogsMenu = false },
        )

        AnchorDropdownMenu(
            show = showSelectionMenu,
            items = listOf(
                DropdownItem(
                    // `Strings` has no copy entry (the old menu used `@android:string/copy`).
                    text = stringResource(android.R.string.copy),
                    onClick = { send(LogsCommand.CopySelectedLogs) },
                ),
                DropdownItem(
                    text = stringResource(Strings.extended_copy),
                    onClick = { send(LogsCommand.OpenExtendedCopy) },
                ),
                DropdownItem(
                    text = stringResource(Strings.to_recording),
                    onClick = { send(LogsCommand.SelectedToRecording) },
                ),
                DropdownItem(
                    text = stringResource(Strings.export),
                    onClick = { send(LogsCommand.ExportSelectedClicked) },
                ),
            ),
            onDismissFinished = { showSelectionMenu = false },
        )
    }
}

/**
 * A single log line: the level badge on the left and the formatted line on the right, one line tall
 * until the row is expanded.
 */
@Composable
private fun LogLineRow(
    item: LogLineItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (item.selected) {
                    MiuixTheme.colorScheme.tertiaryContainer
                } else {
                    Color.Transparent
                },
            )
            // `combinedClickable` handles the tap and the long press without consuming drags, so
            // the vertical scroll of the parent list keeps working - and unlike the hand rolled
            // `detectTapGestures` it also provides the Miuix press feedback.
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = item.level.letter,
            modifier = Modifier
                .squircleSurface(
                    color = MiuixTheme.colorScheme.surfaceContainerHighest,
                    topStart = 0.dp,
                    topEnd = 6.dp,
                    bottomEnd = 6.dp,
                    bottomStart = 0.dp,
                )
                .padding(horizontal = 3.dp, vertical = 3.dp),
            fontSize = item.textSize.sp,
            fontWeight = FontWeight.Bold,
            color = item.level.levelColor(),
        )

        Text(
            text = item.displayText,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp, vertical = 3.dp),
            fontSize = item.textSize.sp,
            maxLines = if (item.expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Dropdown anchored to the top end of the screen - the equivalent of the old top bar `PopupMenu`.
 *
 * It uses a zero sized anchor so Miuix can place the popup right under the top bar actions. It has
 * to be a sibling of the `LazyColumn` inside the `Scaffold`, otherwise the `Overlay*` host is
 * missing and the popup can never be shown.
 */
@Composable
private fun AnchorDropdownMenu(
    items: List<DropdownItem>,
    onDismissFinished: () -> Unit,
    show: Boolean = true,
) {
    // `remember(show)`: every time the owner asks for the menu the popup is composed freshly and is
    // visible again, and once it is closed the owner is notified so it can drop its state.
    var visible by remember(show) { mutableStateOf(show) }
    val hapticFeedback = LocalHapticFeedback.current

    Box(modifier = Modifier.wrapContentSize(align = Alignment.TopEnd)) {
        // Zero sized anchor: `anchorBounds` comes from this layout, so the popup opens right below
        // the top bar actions.
        Box(modifier = Modifier.size(0.dp))

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
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                            visible = false
                            onDismissFinished()
                        },
                    )
                }
            }
        }
    }
}

/**
 * Level colour, taken from the current [MiuixTheme] scheme - never a literal colour, so both light
 * and dark themes stay readable:
 *
 * * `E`/`F`/`S` -> `error`
 * * `W` -> the warning-ish tertiary accent
 * * `I` -> the brand `primary`
 * * `D`/`V` -> the neutral variants
 */
@Composable
private fun LogLevel.levelColor(): Color = when (this) {
    LogLevel.FATAL,
    LogLevel.ERROR,
    LogLevel.SILENT,
    -> MiuixTheme.colorScheme.error

    LogLevel.WARNING -> MiuixTheme.colorScheme.tertiaryContainerVariant
    LogLevel.INFO -> MiuixTheme.colorScheme.primary
    LogLevel.DEBUG -> MiuixTheme.colorScheme.primaryVariant
    LogLevel.VERBOSE -> MiuixTheme.colorScheme.onSurfaceVariantSummary
}
