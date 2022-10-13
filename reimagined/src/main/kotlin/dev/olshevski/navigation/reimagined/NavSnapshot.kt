package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
data class NavSnapshot<out T, S> internal constructor(
    val items: List<NavSnapshotItem<T, S>>,
    val action: NavAction
)