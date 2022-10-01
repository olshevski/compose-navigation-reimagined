package dev.olshevski.navigation.reimagined

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStore

/**
 * @param key User key of this entry.
 */
@Stable
class SharedNavHostEntry internal constructor(
    id: NavId,
    val key: NavKey,
    viewModelStore: ViewModelStore,
    application: Application?
) : BaseNavHostEntry(id, viewModelStore, application) {

    private val _associatedEntryIds = mutableSetOf<NavId>()

    val associatedEntryIds: Set<NavId> get() = _associatedEntryIds

    internal fun addAssociatedEntryId(id: NavId) = _associatedEntryIds.add(id)

    internal fun addAssociatedEntryIds(ids: List<NavId>) = _associatedEntryIds.addAll(ids)

    override fun toString() =
        "SharedNavHostEntry(id=$id, key=$key, associatedEntryIds=$associatedEntryIds)"

}