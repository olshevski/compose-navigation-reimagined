package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal class NavSnapshot<out T, S> internal constructor(
    val items: List<NavSnapshotItem<T, S>>,
    val action: NavAction
) {

    override fun toString() = "NavSnapshot(items=$items, action=$action)"

}