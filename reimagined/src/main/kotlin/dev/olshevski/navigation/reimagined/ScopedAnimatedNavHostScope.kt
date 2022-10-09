package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Stable

@Stable
interface ScopedAnimatedNavHostScope<out T, S> : AnimatedNavHostScope<T>

internal class ScopedAnimatedNavHostScopeImpl<out T, S>(
    hostEntries: List<NavHostEntry<T>>,
    scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : ScopedNavHostScopeImpl<T, S>(hostEntries, scopedHostEntries),
    ScopedAnimatedNavHostScope<T, S>,
    AnimatedVisibilityScope by animatedVisibilityScope