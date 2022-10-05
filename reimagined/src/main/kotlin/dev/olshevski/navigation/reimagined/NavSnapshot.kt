package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

@Stable
internal class NavSnapshot<out T> internal constructor(
    val items: List<NavSnapshotItem<T>>,
    val action: NavAction
) {

    override fun toString() = "NavSnapshot(entries=$items, action=$action)"

}