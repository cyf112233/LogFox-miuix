package com.f0x1d.logfox.feature.crashes.presentation.details.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.designsystem.component.icon.AppIcon
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.crashes.presentation.details.CrashDetailsViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun CrashDetailsScreenContent(
    state: CrashDetailsViewState,
    notificationsChannelsAvailable: Boolean,
    snackbarMessage: String?,
    onSnackbarShown: () -> Unit,
    confirmBlacklist: Boolean,
    onConfirmBlacklistDismiss: () -> Unit,
    confirmDelete: Boolean,
    onConfirmDeleteDismiss: () -> Unit,
    onBack: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleWrapLines: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onBlacklistClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmBlacklist: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCopyLog: () -> Unit,
    onShareLog: () -> Unit,
    onExportToFile: () -> Unit,
    onExportToZip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackbarShown()
        }
    }

    val scrollBehavior = MiuixScrollBehavior()
    val highlightColor = MiuixTheme.colorScheme.primaryContainer

    val crash = state.crash
    val annotatedLog = remember(state.crashLog, state.searchMatchRanges, highlightColor) {
        val log = state.crashLog.orEmpty()
        buildAnnotatedString {
            append(log)
            state.searchMatchRanges.forEach { range ->
                if (range.first >= 0 && range.last + 1 <= log.length && range.first <= range.last) {
                    addStyle(SpanStyle(background = highlightColor), range.first, range.last + 1)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = crash?.appName ?: crash?.packageName.orEmpty(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = MiuixIcons.Back, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { searchExpanded = !searchExpanded }) {
                        Icon(imageVector = MiuixIcons.Search, contentDescription = null)
                    }
                    OverlayIconDropdownMenu(
                        entries = listOf(
                            DropdownEntry(
                                items = listOfNotNull(
                                    DropdownItem(
                                        text = stringResource(Strings.wrap_log_lines_in_details),
                                        selected = state.wrapCrashLogLines,
                                        onClick = onToggleWrapLines,
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.information),
                                        onClick = onOpenAppInfo,
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.notifications),
                                        onClick = onOpenNotificationSettings,
                                    ).takeIf {
                                        notificationsChannelsAvailable &&
                                            state.useSeparateNotificationsChannelsForCrashes
                                    },
                                    DropdownItem(
                                        text = stringResource(
                                            if (state.blacklisted == true) {
                                                Strings.remove_from_blacklist
                                            } else {
                                                Strings.add_to_blacklist
                                            },
                                        ),
                                        onClick = onBlacklistClick,
                                    ).takeIf { state.blacklisted != null },
                                    DropdownItem(
                                        text = stringResource(Strings.delete),
                                        onClick = onDeleteClick,
                                    ),
                                ),
                            ),
                        ),
                    ) {
                        Icon(imageVector = MiuixIcons.More, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            if (searchExpanded) {
                InputField(
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = onSearchQueryChange,
                    expanded = true,
                    onExpandedChange = { },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    label = stringResource(Strings.search),
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .scrollEndHaptic()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                overscrollEffect = null,
            ) {
                item { Spacer(modifier = Modifier.size(12.dp)) }

                if (crash != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AppIcon(
                                    packageName = crash.packageName,
                                    modifier = Modifier.size(52.dp),
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = crash.appName ?: stringResource(Strings.unknown),
                                        style = MiuixTheme.textStyles.title4,
                                    )
                                    Text(
                                        text = crash.packageName,
                                        style = MiuixTheme.textStyles.footnote1,
                                        color = MiuixTheme.colorScheme.onBackgroundVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.crashLog != null) {
                    item { SmallTitle(stringResource(Strings.logs)) }
                    item {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                        ) {
                            SelectionContainer {
                                Text(
                                    text = annotatedLog,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MiuixTheme.textStyles.body2.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    ActionButton(
                                        text = stringResource(android.R.string.copy),
                                        iconResId = Icons.ic_copy,
                                        onClick = onCopyLog,
                                        modifier = Modifier.weight(1f),
                                    )
                                    ActionButton(
                                        text = stringResource(Strings.share),
                                        iconResId = Icons.ic_share,
                                        onClick = onShareLog,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                Spacer(modifier = Modifier.size(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    ActionButton(
                                        text = stringResource(Strings.export),
                                        iconResId = Icons.ic_export,
                                        onClick = onExportToFile,
                                        modifier = Modifier.weight(1f),
                                    )
                                    ActionButton(
                                        text = stringResource(Strings.zip),
                                        iconResId = Icons.ic_archive,
                                        onClick = onExportToZip,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.size(12.dp)) }
            }
        }

        if (confirmBlacklist) {
            OverlayDialog(
                show = true,
                title = stringResource(Strings.blacklist),
                summary = stringResource(Strings.warning_blacklist),
                onDismissRequest = onConfirmBlacklistDismiss,
            ) {
                ConfirmRow(
                    confirmText = stringResource(Strings.blacklist),
                    onDismiss = onConfirmBlacklistDismiss,
                    onConfirm = onConfirmBlacklist,
                )
            }
        }

        if (confirmDelete) {
            OverlayDialog(
                show = true,
                title = stringResource(Strings.delete),
                summary = stringResource(Strings.delete_warning),
                onDismissRequest = onConfirmDeleteDismiss,
            ) {
                ConfirmRow(
                    confirmText = stringResource(Strings.delete),
                    onDismiss = onConfirmDeleteDismiss,
                    onConfirm = onConfirmDelete,
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(),
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text)
    }
}

@Composable
private fun ConfirmRow(
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(
            text = stringResource(Strings.close),
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
        )
        TextButton(
            text = confirmText,
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.textButtonColorsPrimary(),
        )
    }
}
