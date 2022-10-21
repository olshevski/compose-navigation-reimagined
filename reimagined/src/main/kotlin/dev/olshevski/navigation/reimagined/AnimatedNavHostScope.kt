package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry]. Additionally,
 * implements [AnimatedVisibilityScope] that can be used to access current [transition].
 */
@Stable
interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry] as well as
 * all scoped [ViewModelStoreOwners][ViewModelStoreOwner] that where specified
 * in `scopeSpec` of [ScopingAnimatedNavHost]. Additionally,
 * implements [AnimatedVisibilityScope] that can be used to access current [transition].
 */
@Stable
interface ScopingAnimatedNavHostScope<out T, S> : AnimatedNavHostScope<T>, ScopingNavHostScope<T, S>

@Stable
internal class ScopingAnimatedNavHostScopeImpl<out T, S>(
    hostEntries: List<NavHostEntry<T>>,
    scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : ScopingNavHostScopeImpl<T, S>(hostEntries, scopedHostEntries),
    ScopingAnimatedNavHostScope<T, S>,
    AnimatedVisibilityScope by animatedVisibilityScope