package com.f0x1d.logfox.feature.logging.presentation.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsCommand
import com.f0x1d.logfox.feature.logging.presentation.search.SearchLogsViewState
import com.f0x1d.logfox.feature.strings.Strings
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Clear
import top.yukonga.miuix.kmp.preference.CheckboxPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Stateless content of the search bottom sheet: the query field, the case sensitive toggle and the
 * clear / search actions.
 *
 * It is rendered inside the platform bottom sheet dialog, which already provides the surface, the
 * rounded corners and the window insets - so this intentionally has no `Scaffold`.
 */
@Composable
internal fun SearchLogsScreenContent(
    state: SearchLogsViewState,
    send: (SearchLogsCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf(state.query.orEmpty()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val search: () -> Unit = {
        if (query.isNotEmpty()) send(SearchLogsCommand.UpdateQuery(query))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Strings.search),
            modifier = Modifier.padding(top = 8.dp),
            style = MiuixTheme.textStyles.title3,
        )

        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = stringResource(Strings.query),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { search() }),
            trailingIcon = {
                if (state.query != null) {
                    IconButton(onClick = { send(SearchLogsCommand.UpdateQuery(null)) }) {
                        Icon(
                            imageVector = MiuixIcons.Clear,
                            contentDescription = stringResource(Strings.clear),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            },
        )

        CheckboxPreference(
            title = stringResource(Strings.case_sensitive),
            checked = state.caseSensitive,
            onCheckedChange = { send(SearchLogsCommand.ToggleCaseSensitive) },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                text = stringResource(Strings.clear),
                onClick = { send(SearchLogsCommand.UpdateQuery(null)) },
                modifier = Modifier.weight(1f),
            )

            Button(
                onClick = search,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text(text = stringResource(Strings.search))
            }
        }
    }
}
