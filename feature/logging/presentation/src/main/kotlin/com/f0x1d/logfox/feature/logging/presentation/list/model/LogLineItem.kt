package com.f0x1d.logfox.feature.logging.presentation.list.model

import androidx.compose.runtime.Immutable
import com.f0x1d.logfox.core.recycler.Identifiable
import com.f0x1d.logfox.feature.logging.api.model.LogLevel

/**
 * Stable (every field is immutable) presentation model of one log row.
 *
 * [Immutable] matters here: the list is rebuilt on every incoming log line, and declaring it stable
 * lets Compose skip list items that did not change instead of recomposing the whole visible window.
 */
@Immutable
data class LogLineItem(
    val logLineId: Long,
    val dateAndTime: Long,
    val uid: String,
    val pid: String,
    val tid: String,
    val packageName: String?,
    val level: LogLevel,
    val tag: String,
    val content: String,
    val displayText: String,
    val expanded: Boolean,
    val selected: Boolean,
    val textSize: Float,
) : Identifiable {
    override val id: Any get() = logLineId
}
