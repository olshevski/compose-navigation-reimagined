package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal class NavSnapshotItem<out T>(
    val hostEntry: NavHostEntry<T>,
    val scopedHostEntries: Map<NavScope, ScopedNavHostEntry>
)