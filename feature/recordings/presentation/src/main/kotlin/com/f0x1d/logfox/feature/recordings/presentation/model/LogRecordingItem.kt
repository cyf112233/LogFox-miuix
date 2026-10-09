package com.f0x1d.logfox.feature.recordings.presentation.model

import androidx.compose.runtime.Immutable

/** [Immutable] lets Compose skip list items whose recording did not change. */
@Immutable
data class LogRecordingItem(
    val recordingId: Long,
    val title: String,
    val formattedDate: String,
)
