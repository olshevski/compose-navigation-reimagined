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

    internal val associatedEntryIds: MutableSet<NavId> = mutableSetOf()

    override fun toString() = "SharedNavHostEntry(id=$id, key=$key)"

}