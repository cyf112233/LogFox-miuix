package com.f0x1d.logfox.feature.filters.presentation.edit.ui.compose

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.core.ui.icons.Icons
import com.f0x1d.logfox.feature.filters.presentation.edit.EditFilterViewState
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.CheckboxPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
internal fun EditFilterScreenContent(
    state: EditFilterViewState,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onRename: (String) -> Unit,
    onExport: () -> Unit,
    onToggleIncluding: () -> Unit,
    onToggleEnabled: () -> Unit,
    onToggleLogLevel: (Int, Boolean) -> Unit,
    onSelectApp: () -> Unit,
    onUidChange: (String) -> Unit,
    onPidChange: (String) -> Unit,
    onTidChange: (String) -> Unit,
    onPackageNameChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    confirmDiscard: Boolean,
    onConfirmDiscardDismiss: () -> Unit,
    onConfirmDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showLogLevelsDialog by remember { mutableStateOf(false) }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = state.name?.takeIf { it.isNotBlank() }
                    ?: stringResource(Strings.filter_name_hint),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = MiuixIcons.Close, contentDescription = stringResource(Strings.close))
                    }
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(imageVector = MiuixIcons.Ok, contentDescription = stringResource(Strings.save))
                    }
                    OverlayIconDropdownMenu(
                        entries = listOf(
                            DropdownEntry(
                                items = listOfNotNull(
                                    DropdownItem(
                                        text = stringResource(Strings.rename_filter),
                                        onClick = { showRenameDialog = true },
                                    ),
                                    DropdownItem(
                                        text = stringResource(Strings.export),
                                        onClick = onExport,
                                    ).takeIf { state.filter != null },
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
            item { Spacer(modifier = Modifier.size(12.dp)) }

            item { SmallTitle(stringResource(Strings.filter)) }
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                ) {
                    SwitchPreference(
                        title = stringResource(
                            if (state.enabled) Strings.enabled else Strings.disabled,
                        ),
                        checked = state.enabled,
                        onCheckedChange = { onToggleEnabled() },
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    ArrowPreference(
                        title = stringResource(
                            if (state.including) Strings.including else Strings.excluding,
                        ),
                        startAction = {
                            Icon(
                                painter = painterResource(
                                    if (state.including) Icons.ic_add else Icons.ic_clear,
                                ),
                                contentDescription = null,
                                tint = if (state.including) {
                                    MiuixTheme.colorScheme.primary
                                } else {
                                    MiuixTheme.colorScheme.error
                                },
                            )
                        },
                        onClick = onToggleIncluding,
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    ArrowPreference(
                        title = stringResource(Strings.log_levels),
                        summary = selectedLogLevels(state.enabledLogLevels),
                        onClick = { showLogLevelsDialog = true },
                    )
                }
            }

            item { SmallTitle(stringResource(Strings.filter_name_hint)) }
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                ) {
                    TextField(
                        value = state.uid.orEmpty(),
                        onValueChange = onUidChange,
                        label = stringResource(Strings.uid),
                        singleLine = true,
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    TextField(
                        value = state.pid.orEmpty(),
                        onValueChange = onPidChange,
                        label = stringResource(Strings.pid),
                        singleLine = true,
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    TextField(
                        value = state.tid.orEmpty(),
                        onValueChange = onTidChange,
                        label = stringResource(Strings.tid),
                        singleLine = true,
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    TextField(
                        value = state.packageName.orEmpty(),
                        onValueChange = onPackageNameChange,
                        label = stringResource(Strings.package_name),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = onSelectApp) {
                                Icon(
                                    painter = painterResource(Icons.ic_android),
                                    contentDescription = stringResource(Strings.select),
                                )
                            }
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    TextField(
                        value = state.tag.orEmpty(),
                        onValueChange = onTagChange,
                        label = stringResource(Strings.tag),
                        singleLine = true,
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    TextField(
                        value = state.content.orEmpty(),
                        onValueChange = onContentChange,
                        label = stringResource(Strings.content_contains),
                        singleLine = true,
                    )
                }
            }

            item {
                Text(
                    text = stringResource(Strings.empty_fields_desc),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                    textAlign = TextAlign.Center,
                )
            }

            item { Spacer(modifier = Modifier.size(12.dp)) }
        }

        if (showLogLevelsDialog) {
            OverlayDialog(
                show = true,
                title = stringResource(Strings.log_levels),
                onDismissRequest = { showLogLevelsDialog = false },
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        LogLevel.entries.forEachIndexed { index, level ->
                            CheckboxPreference(
                                title = level.name,
                                checked = state.enabledLogLevels.getOrElse(index) { false },
                                onCheckedChange = { onToggleLogLevel(index, it) },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(
                            text = stringResource(Strings.close),
                            onClick = { showLogLevelsDialog = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }
        }

        if (confirmDiscard) {
            OverlayDialog(
                show = true,
                title = stringResource(Strings.discard_changes),
                summary = stringResource(Strings.discard_changes_message),
                onDismissRequest = onConfirmDiscardDismiss,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        text = stringResource(Strings.close),
                        onClick = onConfirmDiscardDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = stringResource(Strings.discard_changes),
                        onClick = onConfirmDiscard,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }

        if (showRenameDialog) {
            var name by rememberSaveable { mutableStateOf(state.name.orEmpty()) }

            OverlayDialog(
                show = true,
                title = stringResource(Strings.rename_filter),
                onDismissRequest = { showRenameDialog = false },
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = stringResource(Strings.filter_name),
                        singleLine = true,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TextButton(
                            text = stringResource(Strings.close),
                            onClick = { showRenameDialog = false },
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            text = stringResource(Strings.save),
                            onClick = {
                                onRename(name)
                                showRenameDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun selectedLogLevels(enabledLogLevels: List<Boolean>): String = remember(enabledLogLevels) {
    LogLevel.entries
        .filterIndexed { index, _ -> enabledLogLevels.getOrElse(index) { false } }
        .joinToString("") { it.letter }
}
