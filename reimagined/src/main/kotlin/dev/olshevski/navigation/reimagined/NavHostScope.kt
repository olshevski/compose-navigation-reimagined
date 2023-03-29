package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry].
 */
@Stable
interface NavHostScope<out T> {

    /**
     * List of all current [NavHostEntries][NavHostEntry] in the same order their associated
     * [entries][NavEntry] appear in the backstack.
     *
     * The last entry of this list is always the currently displayed entry.
     */
    val hostEntries: List<NavHostEntry<T>>

}

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry] as well as
 * all scoped [ViewModelStoreOwners][ViewModelStoreOwner] that where specified
 * in `scopeSpec` of [ScopingNavHost], [ScopingAnimatedNavHost] or other `Scoping...NavHost`
 * implementation.
 */
@Stable
interface ScopingNavHostScope<out T, S> : NavHostScope<T> {

    /**
     * [ScopedNavHostEntries][ScopedNavHostEntry] for all scopes associated with the current
     * destination in `scopeSpec`
     */
    val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>

}

@Stable
internal open class ScopingNavHostScopeImpl<out T, S>(
    override val hostEntries: List<NavHostEntry<T>>,
    override val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
) : ScopingNavHostScope<T, S>

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

/**
 * Returns [ViewModelStoreOwner] for the [scope]. This scope should be associated with the
 * current destination in `scopeSpec` of [ScopingNavHost], [ScopingAnimatedNavHost] or
 * other `Scoping...NavHost` implementation. Otherwise, [IllegalStateException] will be thrown.
 */
fun <T, S> ScopingNavHostScope<T, S>.getScopedViewModelStoreOwner(scope: S): ViewModelStoreOwner =
    scopedHostEntries[scope] ?: error(
        "You should associate the scope ($scope) with the destination " +
                "(${currentHostEntry.destination}) in a scopeSpec"
    )