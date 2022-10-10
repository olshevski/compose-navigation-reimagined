package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Stable

@Stable
interface ScopingAnimatedNavHostScope<out T, S> : AnimatedNavHostScope<T>

internal class ScopingAnimatedNavHostScopeImpl<out T, S>(
    hostEntries: List<NavHostEntry<T>>,
    scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : ScopingNavHostScopeImpl<T, S>(hostEntries, scopedHostEntries),
    ScopingAnimatedNavHostScope<T, S>,
    AnimatedVisibilityScope by animatedVisibilityScope