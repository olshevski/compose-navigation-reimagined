package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope

/**
 * Provides access to the list of all current [NavHostEntries][NavHostEntry] as well as other
 * convenient methods only available within [AnimatedNavHost]. Additionally, implements
 * [AnimatedVisibilityScope] that can be used to access [transition].
 */
interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

internal class AnimatedNavHostScopeImpl<out T>(
    hostEntries: List<NavHostEntry<T>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(hostEntries), AnimatedNavHostScope<T>,
    AnimatedVisibilityScope by animatedVisibilityScope