package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@ExperimentalReimaginedApi
@Stable
data class NavSnapshot<out T, S> internal constructor(
    val items: List<NavSnapshotItem<T, S>>,
    val action: NavAction
)

@ExperimentalReimaginedApi
@Stable
data class NavSnapshotItem<out T, S> internal constructor(
    val hostEntry: NavHostEntry<T>,
    val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
)