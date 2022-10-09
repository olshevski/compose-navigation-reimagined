package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal class NavSnapshotItem<out T, S>(
    val hostEntry: NavHostEntry<T>,
    val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
) {

    override fun toString() =
        "NavSnapshotItem(hostEntry=$hostEntry, scopedHostEntries=$scopedHostEntries)"

}