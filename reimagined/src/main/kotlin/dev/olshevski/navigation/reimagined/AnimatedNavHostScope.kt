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

internal class AnimatedNavHostScopeImpl<out T>(
    hostEntries: List<NavHostEntry<T>>,
    baseHostScope: BaseNavHostScope<T>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(hostEntries, baseHostScope), AnimatedNavHostScope<T>,
    AnimatedVisibilityScope by animatedVisibilityScope