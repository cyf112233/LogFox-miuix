package com.f0x1d.logfox.feature.preferences.presentation.ui.settings.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.f0x1d.logfox.feature.preferences.presentation.PreferenceDivider
import com.f0x1d.logfox.feature.preferences.presentation.SettingsScreen
import com.f0x1d.logfox.feature.preferences.presentation.rememberBooleanPreference
import com.f0x1d.logfox.feature.preferences.presentation.settingsGroup
import com.f0x1d.logfox.feature.preferences.presentation.ui.settings.PreferencesUIViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.CheckboxPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val KEY_OPEN_CRASHES_ON_STARTUP = "pref_open_crashes_page_on_startup"
private const val KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT = "pref_export_logs_in_original_format"
private const val KEY_WRAP_CRASH_LOG_LINES = "pref_wrap_crash_log_lines"
private const val KEY_LOGS_EXPANDED = "pref_logs_expanded"
private const val KEY_RESUME_LOGS_WITH_TOUCH = "pref_resume_logs_with_touch"

private const val NIGHT_THEME_DEFAULT = 0
private const val LOGS_UPDATE_INTERVAL_DEFAULT = 300L
private const val LOGS_TEXT_SIZE_DEFAULT = 14
private const val LOGS_DISPLAY_LIMIT_DEFAULT = 10000
private const val DATE_FORMAT_DEFAULT = "dd.MM"
private const val TIME_FORMAT_DEFAULT = "HH:mm:ss.SSS"

private enum class UiDialog {
    DATE_FORMAT,
    TIME_FORMAT,
    LOGS_FORMAT,
    LOGS_UPDATE_INTERVAL,
    LOGS_TEXT_SIZE,
    LOGS_DISPLAY_LIMIT,
}

@Composable
internal fun PreferencesUIScreenContent(
    state: PreferencesUIViewState,
    monetAvailable: Boolean,
    onNightThemeChanged: (Int) -> Unit,
    onMonetEnabledChanged: (Boolean) -> Unit,
    onDateFormatChanged: (String) -> Unit,
    onTimeFormatChanged: (String) -> Unit,
    onLogsFormatChanged: (Int, Boolean) -> Unit,
    onLogsUpdateIntervalChanged: (Long) -> Unit,
    onLogsTextSizeChanged: (Int) -> Unit,
    onLogsDisplayLimitChanged: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialog by remember { mutableStateOf<UiDialog?>(null) }

    SettingsScreen(
        title = stringResource(Strings.ui),
        modifier = modifier,
        onBack = onBack,
        overlays = {
            UIOverlays(
                dialog = dialog,
                state = state,
                onDismiss = { dialog = null },
                onDateFormatChanged = onDateFormatChanged,
                onTimeFormatChanged = onTimeFormatChanged,
                onLogsFormatChanged = onLogsFormatChanged,
                onLogsUpdateIntervalChanged = onLogsUpdateIntervalChanged,
                onLogsTextSizeChanged = onLogsTextSizeChanged,
                onLogsDisplayLimitChanged = onLogsDisplayLimitChanged,
            )
        },
    ) {
        settingsGroup({ stringResource(Strings.night_theme) }) {
            val themeItems = listOf(
                stringResource(Strings.follow_system),
                stringResource(Strings.light),
                stringResource(Strings.dark),
            )
            OverlayDropdownPreference(
                title = stringResource(Strings.theme_mode),
                items = themeItems,
                selectedIndex = state.nightTheme.coerceAtLeast(NIGHT_THEME_DEFAULT),
                onSelectedIndexChange = onNightThemeChanged,
            )

            if (monetAvailable) {
                PreferenceDivider()

                SwitchPreference(
                    title = stringResource(Strings.monet),
                    summary = stringResource(Strings.monet_summary),
                    checked = state.monetEnabled,
                    onCheckedChange = onMonetEnabledChanged,
                )
            }
        }

        settingsGroup({ stringResource(Strings.date_time) }) {
            ArrowPreference(
                title = stringResource(Strings.date_format),
                summary = state.dateFormat,
                onClick = { dialog = UiDialog.DATE_FORMAT },
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.time_format),
                summary = state.timeFormat,
                onClick = { dialog = UiDialog.TIME_FORMAT },
            )
        }

        settingsGroup({ stringResource(Strings.logs) }) {
            val (openCrashes, setOpenCrashes) = rememberBooleanPreference(
                key = KEY_OPEN_CRASHES_ON_STARTUP,
                defaultValue = false,
            )
            SwitchPreference(
                title = stringResource(Strings.open_crashes_page_on_startup),
                checked = openCrashes,
                onCheckedChange = setOpenCrashes,
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.logs_format),
                summary = logsFormatSummary(state),
                onClick = { dialog = UiDialog.LOGS_FORMAT },
            )

            PreferenceDivider()

            val (exportOriginal, setExportOriginal) = rememberBooleanPreference(
                key = KEY_EXPORT_LOGS_IN_ORIGINAL_FORMAT,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.export_logs_in_original_format),
                checked = exportOriginal,
                onCheckedChange = setExportOriginal,
            )

            PreferenceDivider()

            val (wrapLines, setWrapLines) = rememberBooleanPreference(
                key = KEY_WRAP_CRASH_LOG_LINES,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.wrap_log_lines_in_details),
                checked = wrapLines,
                onCheckedChange = setWrapLines,
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.logs_update_interval),
                summary = state.logsUpdateInterval.toString(),
                onClick = { dialog = UiDialog.LOGS_UPDATE_INTERVAL },
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.logs_text_size),
                summary = state.logsTextSize.toString(),
                onClick = { dialog = UiDialog.LOGS_TEXT_SIZE },
            )

            PreferenceDivider()

            ArrowPreference(
                title = stringResource(Strings.logs_display_limit),
                summary = state.logsDisplayLimit.toString(),
                onClick = { dialog = UiDialog.LOGS_DISPLAY_LIMIT },
            )

            PreferenceDivider()

            val (expanded, setExpanded) = rememberBooleanPreference(
                key = KEY_LOGS_EXPANDED,
                defaultValue = false,
            )
            SwitchPreference(
                title = stringResource(Strings.expanded_logs),
                checked = expanded,
                onCheckedChange = setExpanded,
            )

            PreferenceDivider()

            val (resumeWithTouch, setResumeWithTouch) = rememberBooleanPreference(
                key = KEY_RESUME_LOGS_WITH_TOUCH,
                defaultValue = true,
            )
            SwitchPreference(
                title = stringResource(Strings.resume_logging_with_bottom_edge_touch),
                checked = resumeWithTouch,
                onCheckedChange = setResumeWithTouch,
            )
        }
    }
}

@Composable
private fun UIOverlays(
    dialog: UiDialog?,
    state: PreferencesUIViewState,
    onDismiss: () -> Unit,
    onDateFormatChanged: (String) -> Unit,
    onTimeFormatChanged: (String) -> Unit,
    onLogsFormatChanged: (Int, Boolean) -> Unit,
    onLogsUpdateIntervalChanged: (Long) -> Unit,
    onLogsTextSizeChanged: (Int) -> Unit,
    onLogsDisplayLimitChanged: (Int) -> Unit,
) {
    when (dialog) {
        UiDialog.DATE_FORMAT -> TextValueDialog(
            title = stringResource(Strings.date_format),
            initialValue = state.dateFormat.ifEmpty { DATE_FORMAT_DEFAULT },
            onDismiss = onDismiss,
            onConfirm = {
                onDateFormatChanged(it)
                onDismiss()
            },
        )

        UiDialog.TIME_FORMAT -> TextValueDialog(
            title = stringResource(Strings.time_format),
            initialValue = state.timeFormat.ifEmpty { TIME_FORMAT_DEFAULT },
            onDismiss = onDismiss,
            onConfirm = {
                onTimeFormatChanged(it)
                onDismiss()
            },
        )

        UiDialog.LOGS_FORMAT -> LogsFormatDialog(
            state = state,
            onDismiss = onDismiss,
            onChanged = onLogsFormatChanged,
        )

        UiDialog.LOGS_UPDATE_INTERVAL -> NumberValueDialog(
            title = stringResource(Strings.logs_update_interval),
            initialValue = state.logsUpdateInterval.toString(),
            hint = stringResource(Strings.in_ms),
            onDismiss = onDismiss,
            onConfirm = {
                onLogsUpdateIntervalChanged(it)
                onDismiss()
            },
        )

        UiDialog.LOGS_TEXT_SIZE -> NumberValueDialog(
            title = stringResource(Strings.logs_text_size),
            initialValue = state.logsTextSize.toString(),
            hint = null,
            onDismiss = onDismiss,
            onConfirm = {
                onLogsTextSizeChanged(it.toInt())
                onDismiss()
            },
        )

        UiDialog.LOGS_DISPLAY_LIMIT -> NumberValueDialog(
            title = stringResource(Strings.logs_display_limit),
            initialValue = state.logsDisplayLimit.toString(),
            hint = stringResource(Strings.lines),
            onDismiss = onDismiss,
            onConfirm = {
                onLogsDisplayLimitChanged(it.toInt())
                onDismiss()
            },
        )

        null -> Unit
    }
}

@Composable
private fun LogsFormatDialog(
    state: PreferencesUIViewState,
    onDismiss: () -> Unit,
    onChanged: (Int, Boolean) -> Unit,
) {
    val labels = listOf(
        stringResource(Strings.date),
        stringResource(Strings.time),
        stringResource(Strings.uid),
        stringResource(Strings.pid),
        stringResource(Strings.tid),
        stringResource(Strings.package_name),
        stringResource(Strings.tag),
        stringResource(Strings.content),
    )
    val checked = listOf(
        state.showLogDate,
        state.showLogTime,
        state.showLogUid,
        state.showLogPid,
        state.showLogTid,
        state.showLogPackage,
        state.showLogTag,
        state.showLogContent,
    )

    OverlayDialog(
        show = true,
        title = stringResource(Strings.logs_format),
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, label ->
                CheckboxPreference(
                    title = label,
                    checked = checked[index],
                    onCheckedChange = { onChanged(index, it) },
                )
            }

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
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        }
    }
}

@Composable
private fun NumberValueDialog(
    title: String,
    initialValue: String,
    hint: String?,

    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    var value by rememberSaveable(title) { mutableStateOf(initialValue) }
    var isError by remember { mutableStateOf(false) }

    OverlayDialog(
        show = true,
        title = title,
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = value,
                onValueChange = {
                    value = it
                    isError = false
                },
                label = hint.orEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )

            if (isError) {
                Text(
                    text = stringResource(Strings.invalid_value),
                    modifier = Modifier.padding(top = 8.dp),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.error,
                )
            }

            DialogButtons(
                onDismiss = onDismiss,
                onConfirm = {
                    val parsed = value.trim().toLongOrNull()
                    if (parsed == null) {
                        isError = true
                    } else {
                        onConfirm(parsed)
                    }
                },
            )
        }
    }
}

@Composable
private fun TextValueDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by rememberSaveable(title) { mutableStateOf(initialValue) }

    OverlayDialog(
        show = true,
        title = title,
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = value,
                onValueChange = { value = it },
                label = title,
                singleLine = true,
            )

            DialogButtons(
                onDismiss = onDismiss,
                onConfirm = { onConfirm(value.trim()) },
            )
        }
    }
}

@Composable
private fun DialogButtons(
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
            text = stringResource(Strings.save),
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.textButtonColorsPrimary(),
        )
    }
}

@Composable
private fun logsFormatSummary(state: PreferencesUIViewState): String {
    val date = stringResource(Strings.date)
    val time = stringResource(Strings.time)
    val uid = stringResource(Strings.uid)
    val pid = stringResource(Strings.pid)
    val tid = stringResource(Strings.tid)
    val packageName = stringResource(Strings.package_name)
    val tag = stringResource(Strings.tag)
    val content = stringResource(Strings.content)

    return remember(state) {
        listOf(
            date to state.showLogDate,
            time to state.showLogTime,
            uid to state.showLogUid,
            pid to state.showLogPid,
            tid to state.showLogTid,
            packageName to state.showLogPackage,
            tag to state.showLogTag,
            content to state.showLogContent,
        ).filter { it.second }.joinToString(", ") { it.first }
    }
}
