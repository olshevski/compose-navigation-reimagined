package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope

interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

internal class AnimatedNavHostScopeImpl<out T>(
    backstack: NavBackstack<T>,
    currentHostEntry: NavHostEntry<T>,
    hostStateScope: NavHostStateScope<T>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(
    backstack = backstack,
    currentHostEntry = currentHostEntry,
    hostStateScope = hostStateScope
), AnimatedNavHostScope<T>, AnimatedVisibilityScope by animatedVisibilityScope