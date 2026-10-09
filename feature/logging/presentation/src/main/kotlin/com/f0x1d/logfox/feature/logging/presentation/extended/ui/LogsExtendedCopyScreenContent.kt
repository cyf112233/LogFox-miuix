package com.f0x1d.logfox.feature.logging.presentation.extended.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.compose.designsystem.component.button.NavigationBackButton
import com.f0x1d.logfox.feature.logging.presentation.extended.LogsExtendedCopyViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * Stateless full screen "extended copy" page: the selected lines rendered as one selectable block,
 * plus a copy-all action.
 */
@Composable
internal fun LogsExtendedCopyScreenContent(
    state: LogsExtendedCopyViewState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val clipboard = LocalClipboardManager.current
    val text = state.text.orEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = stringResource(Strings.extended_copy),
                navigationIcon = { NavigationBackButton(onClick = onBack) },
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
            item(key = "log_text") {
                SelectionContainer {
                    Text(
                        text = text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MiuixTheme.textStyles.paragraph,
                    )
                }
            }

            item(key = "copy") {
                Button(
                    onClick = { clipboard.setText(AnnotatedString(text)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                    enabled = text.isNotEmpty(),
                ) {
                    Text(text = stringResource(android.R.string.copy))
                }
            }
        }
    }
}
