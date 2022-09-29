package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal class SharedNavHostEntryRecord(
    val id: NavId,
    val key: NavKey,
    val associatedEntryIds: Array<NavId>
) : Parcelable

internal fun SharedNavHostEntry.toSharedHostEntryRecord() = SharedNavHostEntryRecord(
    id = id,
    key = key,
    associatedEntryIds = associatedEntryIds.toTypedArray()
)