package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Backstack snapshot with all [NavHostEntries][NavHostEntry] and
 * [ScopedNavHostEntries][ScopedNavHostEntry] saved. This is used for proper queueing of
 * transitions. The order of the [items] is the same as the order of entries in the backstack.
 */
@Stable
class NavSnapshot<out T, S> internal constructor(
    val items: List<NavSnapshotItem<T, S>>,
    val action: NavAction
) {

    override fun toString() = "NavSnapshot(items=$items, action=$action)"

    // snapshots must never be checked for structural equality, only for referential

}

@Stable
data class NavSnapshotItem<out T, S> internal constructor(
    val hostEntry: NavHostEntry<T>,
    val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
)

/**
 * The CompositionLocal containing all current scoped [ViewModelStoreOwners][ViewModelStoreOwner]
 * associated with the current navigation destination.
 */
val LocalScopedViewModelStoreOwners =
    compositionLocalOf<Map<out Any?, ViewModelStoreOwner>> { emptyMap() }

@Composable
fun NavSnapshotItem<*, *>.ComponentsProvider(
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalLifecycleOwner provides hostEntry,
    LocalSavedStateRegistryOwner provides hostEntry,
    LocalViewModelStoreOwner provides hostEntry,
    LocalScopedViewModelStoreOwners provides scopedHostEntries
) {
    hostEntry.SaveableStateProvider(content)
}