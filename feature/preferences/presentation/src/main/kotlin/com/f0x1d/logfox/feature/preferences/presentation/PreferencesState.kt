package com.f0x1d.logfox.feature.preferences.presentation

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager

/**
 * Reads/writes the very same [PreferenceManager.getDefaultSharedPreferences] store the settings
 * screens used before the Miuix migration, so no stored value changes meaning. The value is kept in
 * sync with writes coming from anywhere else in the app.
 */
@Composable
private fun <T> rememberPreference(
    key: String,
    defaultValue: T,
    read: SharedPreferences.(String, T) -> T,
    write: SharedPreferences.Editor.(String, T) -> Unit,
): Pair<T, (T) -> Unit> {
    val context = LocalContext.current
    val preferences = remember(context) {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    val state: MutableState<T> = remember(preferences, key) {
        mutableStateOf(preferences.read(key, defaultValue))
    }

    DisposableEffect(preferences, key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == null || changedKey == key) {
                state.value = preferences.read(key, defaultValue)
            }
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val setter: (T) -> Unit = remember(preferences, key) {
        { value ->
            preferences.edit().apply { write(key, value) }.apply()
            state.value = value
        }
    }

    return state.value to setter
}

@Composable
internal fun rememberBooleanPreference(
    key: String,
    defaultValue: Boolean = false,
): Pair<Boolean, (Boolean) -> Unit> = rememberPreference(
    key = key,
    defaultValue = defaultValue,
    read = { k, d -> getBoolean(k, d) },
    write = { k, v -> putBoolean(k, v) },
)

@Composable
internal fun rememberIntPreference(
    key: String,
    defaultValue: Int = 0,
): Pair<Int, (Int) -> Unit> = rememberPreference(
    key = key,
    defaultValue = defaultValue,
    read = { k, d -> getInt(k, d) },
    write = { k, v -> putInt(k, v) },
)

@Composable
internal fun rememberLongPreference(
    key: String,
    defaultValue: Long = 0L,
): Pair<Long, (Long) -> Unit> = rememberPreference(
    key = key,
    defaultValue = defaultValue,
    read = { k, d -> getLong(k, d) },
    write = { k, v -> putLong(k, v) },
)

@Composable
internal fun rememberStringPreference(
    key: String,
    defaultValue: String = "",
): Pair<String, (String) -> Unit> = rememberPreference(
    key = key,
    defaultValue = defaultValue,
    read = { k, d -> getString(k, d) ?: d },
    write = { k, v -> putString(k, v) },
)
