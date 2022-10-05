package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Parcelable representation of [ScopedNavHostEntry] data.
 */
@Parcelize
internal class ScopedNavHostEntryRecord(
    val id: NavId,
    val scope: NavScope
) : Parcelable

internal fun ScopedNavHostEntry.toScopedHostEntryRecord() = ScopedNavHostEntryRecord(
    id = id,
    scope = scope
)