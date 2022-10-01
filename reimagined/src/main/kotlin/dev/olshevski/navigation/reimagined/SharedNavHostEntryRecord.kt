package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Parcelable representation of [SharedNavHostEntry] data.
 */
@Parcelize
internal class SharedNavHostEntryRecord(
    val id: NavId,
    val key: NavKey,
    val associatedEntryIds: List<NavId>
) : Parcelable

internal fun SharedNavHostEntry.toSharedHostEntryRecord() = SharedNavHostEntryRecord(
    id = id,
    key = key,
    associatedEntryIds = associatedEntryIds.toList()
)