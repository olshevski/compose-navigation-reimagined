package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry] as well as other
 * convenient methods only available within [NavHost].
 */
@Stable
interface NavHostScope<out T> {

    /**
     * List of all current [NavHostEntries][NavHostEntry] in the same order
     * [entries][NavEntry] appear in the backstack.
     *
     * The last entry of this list is always the currently displayed entry. Guaranteed to not be
     * empty in [NavHost]/[AnimatedNavHost].
     *
     * You may use these entries to access neighbour [ViewModelStoreOwners][ViewModelStoreOwner]
     * and implement your own ViewModel-sharing behaviour.
     */
    val hostEntries: List<NavHostEntry<T>>

    fun getScopedViewModelStoreOwner(scope: NavScope): ViewModelStoreOwner
}

internal open class NavHostScopeImpl<out T>(
    override val hostEntries: List<NavHostEntry<T>>,
    private val scopedHostEntries: Map<NavScope, ScopedNavHostEntry>
) : NavHostScope<T> {

    override fun getScopedViewModelStoreOwner(scope: NavScope): ViewModelStoreOwner =
        scopedHostEntries[scope]
            ?: error("You should associate the scope ($scope) with the destination (${currentHostEntry.destination}) in a scopeSpec")

}

/**
 * Currently displayed [NavHostEntry]. Its destination is the one that is being passed into
 * NavHost's/AnimatedNavHost's `contentSelector` parameter.
 *
 * Also, this same exact entry is the one that is being set as the current
 * [LocalViewModelStoreOwner], [LocalSavedStateRegistryOwner] and [LocalLifecycleOwner].
 */
val <T> NavHostScope<T>.currentHostEntry: NavHostEntry<T> get() = hostEntries.last()

/**
 * Utility method to search through [NavHostScope.hostEntries]. Returns the item that matches
 * the specified [predicate] or `null` when nothing could be matched.
 *
 * @param match specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the last matching item from the start of the [NavHostScope.hostEntries]
 * will be returned.
 */
fun <T> NavHostScope<T>.findHostEntry(
    match: Match = Match.Last,
    predicate: (T) -> Boolean
): NavHostEntry<T>? {
    val entryPredicate: (NavHostEntry<T>) -> Boolean = { predicate(it.destination) }
    return hostEntries.run {
        when (match) {
            Match.First -> find(entryPredicate)
            Match.Last -> findLast(entryPredicate)
        }
    }
}