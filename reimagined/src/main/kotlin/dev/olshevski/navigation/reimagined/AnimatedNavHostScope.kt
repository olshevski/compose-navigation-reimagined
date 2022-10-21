package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Stable

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry] as well as other
 * convenient methods only available within [AnimatedNavHost]. Additionally, implements
 * [AnimatedVisibilityScope] that can be used to access [transition].
 */
@Stable
interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

@Stable
interface ScopingAnimatedNavHostScope<out T, S> : AnimatedNavHostScope<T>

internal class ScopingAnimatedNavHostScopeImpl<out T, S>(
    hostEntries: List<NavHostEntry<T>>,
    scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : ScopingNavHostScopeImpl<T, S>(hostEntries, scopedHostEntries),
    ScopingAnimatedNavHostScope<T, S>,
    AnimatedVisibilityScope by animatedVisibilityScope