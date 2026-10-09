package com.f0x1d.logfox.feature.crashes.presentation.list.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.designsystem.component.icon.AppIcon
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.compose.designsystem.component.search.TopSearchBar
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.crashes.presentation.common.model.AppCrashesCountItem
import com.f0x1d.logfox.feature.crashes.presentation.list.CrashesViewState
import com.f0x1d.logfox.feature.preferences.api.CrashesSort
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun CrashesScreenContent(
    state: CrashesViewState,
    onQueryChange: (String) -> Unit,
    onCrashClick: (AppCrashesCountItem) -> Unit,
    onCrashDelete: (AppCrashesCountItem) -> Unit,
    onSortChange: (CrashesSort, Boolean) -> Unit,
    onOpenBlacklist: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchExpanded by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var crashToDelete by remember { mutableStateOf<AppCrashesCountItem?>(null) }

    val scrollBehavior = MiuixScrollBehavior()
    val searching = !state.query.isNullOrEmpty()
    val items = if (searching) state.searchedCrashes else state.crashes

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = stringResource(Strings.crashes),
                actions = {
                    OverlayIconDropdownMenu(
                        entries = listOf(
                            DropdownEntry(
                                items = listOf(
                                    DropdownItem(
                                        text = stringResource(Strings.sort),
                                        onClick = { showSortDialog = true },
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.blacklist),
                                        onClick = onOpenBlacklist,
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.clear),
                                        onClick = { showClearDialog = true },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            TopSearchBar(
                query = state.query.orEmpty(),
                onQueryChange = onQueryChange,
                onSearch = onQueryChange,
                expanded = searchExpanded,
                onExpandedChange = { searchExpanded = it },
                modifier = Modifier.padding(horizontal = 12.dp),
                label = stringResource(Strings.crashes),
            ) { }

            if (items.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    ListPlaceholder(
                        iconResId = Icons.ic_bug,
                        text = {
                            Text(
                                text = stringResource(Strings.no_crashes),
                                style = MiuixTheme.textStyles.body1,
                                color = MiuixTheme.colorScheme.onBackgroundVariant,
                            )
                        },
                    )
                }
            } else {
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

                    items(items, key = { it.lastCrashId }) { item ->
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                        ) {
                            ArrowPreference(
                                title = item.appName ?: item.packageName,
                                summary = crashSummary(item),
                                startAction = {
                                    AppIcon(
                                        packageName = item.packageName,
                                        modifier = Modifier.size(40.dp),
                                    )
                                },
                                endActions = {
                                    IconButton(onClick = { crashToDelete = item }) {
                                        Icon(
                                            imageVector = MiuixIcons.Delete,
                                            contentDescription = null,
                                            tint = MiuixTheme.colorScheme.error,
                                        )
                                    }
                                },
                                onClick = { onCrashClick(item) },
                            )
                        }
                    }
                }
            }
        }

        if (showSortDialog) {
            SortDialog(
                currentSort = state.currentSort,
                reversedOrder = state.sortInReversedOrder,
                onDismiss = { showSortDialog = false },
                onConfirm = { sort, reversed ->
                    onSortChange(sort, reversed)
                    showSortDialog = false
                },
            )
        }

        OverlayDialog(
            show = crashToDelete != null,
            title = stringResource(Strings.delete),
            summary = stringResource(Strings.delete_warning),
            onDismissRequest = { crashToDelete = null },
        ) {
            ConfirmButtons(
                confirmText = stringResource(Strings.delete),
                onDismiss = { crashToDelete = null },
                onConfirm = {
                    crashToDelete?.let(onCrashDelete)
                    crashToDelete = null
                },
            )
        }

        OverlayDialog(
            show = showClearDialog,
            title = stringResource(Strings.clear),
            summary = stringResource(Strings.clear_warning),
            onDismissRequest = { showClearDialog = false },
        ) {
            ConfirmButtons(
                confirmText = stringResource(Strings.clear),
                onDismiss = { showClearDialog = false },
                onConfirm = {
                    onClearAll()
                    showClearDialog = false
                },
            )
        }
    }
}

@Composable
private fun SortDialog(
    currentSort: CrashesSort,
    reversedOrder: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (CrashesSort, Boolean) -> Unit,
) {
    var selectedSort by remember { mutableStateOf(currentSort) }
    var reversed by remember { mutableStateOf(reversedOrder) }

    OverlayDialog(
        show = true,
        title = stringResource(Strings.sort),
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CrashesSort.entries.forEachIndexed { index, sort ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                }
                RadioButtonPreference(
                    title = stringResource(sort.titleRes),
                    selected = selectedSort == sort,
                    onClick = { selectedSort = sort },
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            SwitchPreference(
                title = stringResource(Strings.in_reversed_order),
                checked = reversed,
                onCheckedChange = { reversed = it },
            )

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
                    text = stringResource(Strings.save),
                    onClick = { onConfirm(selectedSort, reversed) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}

@Composable
private fun ConfirmButtons(
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

@Composable
private fun crashSummary(item: AppCrashesCountItem): String {
    val crashesLabel = stringResource(Strings.crashes)
    return remember(item) {
        if (item.count == 1) {
            "${item.crashType.readableName} • ${item.formattedDate}"
        } else {
            "$crashesLabel: ${item.count} • ${item.packageName}"
        }
    }
}
