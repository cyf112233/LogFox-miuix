package com.f0x1d.logfox.feature.filters.presentation.list.ui.compose

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.designsystem.component.placeholder.ListPlaceholder
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.f0x1d.logfox.feature.filters.presentation.list.FiltersViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun FiltersScreenContent(
    state: FiltersViewState,
    onBack: () -> Unit,
    onCreateFilter: () -> Unit,
    onOpenFilter: (UserFilter) -> Unit,
    onDeleteFilter: (UserFilter) -> Unit,
    onSwitchFilter: (UserFilter, Boolean) -> Unit,
    onClearAll: () -> Unit,
    onImportFilters: () -> Unit,
    onExportAllFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterToDelete by remember { mutableStateOf<UserFilter?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = stringResource(Strings.filters),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = MiuixIcons.Back, contentDescription = stringResource(Strings.back))
                    }
                },
                actions = {
                    OverlayIconDropdownMenu(
                        entries = listOf(
                            DropdownEntry(
                                items = listOf(
                                    DropdownItem(
                                        text = stringResource(Strings.clear),
                                        onClick = { showClearAllDialog = true },
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.str_import),
                                        onClick = onImportFilters,
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.export_all),
                                        onClick = onExportAllFilters,
                                    ),
                                ),
                            ),
                        ),
                    ) {
                        Icon(imageVector = MiuixIcons.More, contentDescription = stringResource(Strings.more))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateFilter) {
                Icon(
                    imageVector = MiuixIcons.Add,
                    contentDescription = stringResource(Strings.create_filter),
                )
            }
        },
    ) { paddingValues ->
        if (state.filters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                ListPlaceholder(
                    iconResId = Icons.ic_filter,
                    text = {
                        Text(
                            text = stringResource(Strings.no_filters),
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
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
                overscrollEffect = null,
            ) {
                item { Spacer(modifier = Modifier.size(12.dp)) }

                items(state.filters, key = { it.id }) { filter ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                    ) {
                        BasicComponent(
                            title = filterTitle(filter),
                            summary = filterSummary(filter),
                            endActions = {
                                Switch(
                                    checked = filter.enabled,
                                    onCheckedChange = { onSwitchFilter(filter, it) },
                                )
                                IconButton(onClick = { filterToDelete = filter }) {
                                    Icon(
                                        imageVector = MiuixIcons.Delete,
                                        contentDescription = stringResource(Strings.delete),
                                        tint = MiuixTheme.colorScheme.error,
                                    )
                                }
                            },
                            onClick = { onOpenFilter(filter) },
                        )
                    }
                }
            }
        }

        OverlayDialog(
            show = filterToDelete != null,
            title = stringResource(Strings.delete),
            summary = stringResource(Strings.delete_warning),
            onDismissRequest = { filterToDelete = null },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = stringResource(Strings.close),
                    onClick = { filterToDelete = null },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = stringResource(Strings.delete),
                    onClick = {
                        filterToDelete?.let(onDeleteFilter)
                        filterToDelete = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }

        OverlayDialog(
            show = showClearAllDialog,
            title = stringResource(Strings.clear),
            summary = stringResource(Strings.clear_warning),
            onDismissRequest = { showClearAllDialog = false },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = stringResource(Strings.close),
                    onClick = { showClearAllDialog = false },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = stringResource(Strings.clear),
                    onClick = {
                        onClearAll()
                        showClearAllDialog = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}

@Composable
private fun filterTitle(filter: UserFilter): String = filter.name?.takeIf { it.isNotBlank() }
    ?: stringResource(if (filter.including) Strings.including else Strings.excluding)

@Composable
private fun filterSummary(filter: UserFilter): String? {
    val logLevelsLabel = stringResource(Strings.log_levels)
    val uidLabel = stringResource(Strings.uid)
    val pidLabel = stringResource(Strings.pid)
    val tidLabel = stringResource(Strings.tid)
    val packageNameLabel = stringResource(Strings.package_name)
    val tagLabel = stringResource(Strings.tag)
    val contentLabel = stringResource(Strings.content_contains)

    return remember(filter) {
        buildList {
            if (filter.allowedLevels.isNotEmpty()) {
                add("$logLevelsLabel: ${filter.allowedLevels.joinToString("") { it.letter }}")
            }
            filter.uid?.takeIf { it.isNotBlank() }?.let { add("$uidLabel: $it") }
            filter.pid?.takeIf { it.isNotBlank() }?.let { add("$pidLabel: $it") }
            filter.tid?.takeIf { it.isNotBlank() }?.let { add("$tidLabel: $it") }
            filter.packageName?.takeIf { it.isNotBlank() }?.let { add("$packageNameLabel: $it") }
            filter.tag?.takeIf { it.isNotBlank() }?.let { add("$tagLabel: $it") }
            filter.content?.takeIf { it.isNotBlank() }?.let { add("$contentLabel: $it") }
        }.joinToString(" · ").takeIf { it.isNotEmpty() }
    }
}
