package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope

interface AnimatedNavHostScope<T> : NavHostScope<T>, AnimatedVisibilityScope

internal class AnimatedNavHostScopeImpl<T>(
    backstack: NavBackstack<T>,
    currentNavHostEntry: NavHostEntry<T>,
    navHostStateScope: NavHostStateScope<T>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(
    backstack = backstack,
    currentNavHostEntry = currentNavHostEntry,
    navHostStateScope = navHostStateScope
), AnimatedNavHostScope<T>, AnimatedVisibilityScope by animatedVisibilityScope