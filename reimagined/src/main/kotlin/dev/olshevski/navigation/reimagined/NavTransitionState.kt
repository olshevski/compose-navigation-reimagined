package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal data class NavTransitionState<T, S>(
    val targetSnapshot: NavSnapshot<T, S>,
    val currentSnapshot: NavSnapshot<T, S>,
    val targetVisibleItems: Set<NavSnapshotItem<T, S>>,
    val currentVisibleItems: Set<NavSnapshotItem<T, S>>
)