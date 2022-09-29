package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal class NavSnapshot<out T> internal constructor(
    val hostEntries: List<NavHostEntry<T>>,
    val action: NavAction,
    internal val outdatedHostEntryIds: List<NavId>,
) {

    override fun toString() =
        "NavSnapshot(hostEntries=$hostEntries, action=$action, outdatedHostEntryIds=$outdatedHostEntryIds)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavSnapshot<*>

        if (hostEntries != other.hostEntries) return false
        if (action != other.action) return false
        if (outdatedHostEntryIds != other.outdatedHostEntryIds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hostEntries.hashCode()
        result = 31 * result + action.hashCode()
        result = 31 * result + outdatedHostEntryIds.hashCode()
        return result
    }

}