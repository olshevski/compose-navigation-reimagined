package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

/**
 * Backstack snapshot with all [NavHostEntries][NavHostEntry] and
 * [ScopedNavHostEntries][ScopedNavHostEntry] saved. This is used for proper queueing of
 * transitions. The order of the [items] is the same as the order of entries in the backstack.
 */
@Stable
data class NavSnapshot<out T, S> internal constructor(
    val items: List<NavSnapshotItem<T, S>>,
    val action: NavAction
)

@Stable
data class NavSnapshotItem<out T, S> internal constructor(
    val hostEntry: NavHostEntry<T>,
    val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
)