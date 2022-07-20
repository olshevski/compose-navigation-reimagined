package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope

interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

internal class AnimatedNavHostScopeImpl<out T>(
    backstack: NavBackstack<T>,
    currentNavHostEntry: NavHostEntry<T>,
    navHostStateScope: NavHostStateScope<T>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(
    backstack = backstack,
    currentNavHostEntry = currentNavHostEntry,
    navHostStateScope = navHostStateScope
), AnimatedNavHostScope<T>, AnimatedVisibilityScope by animatedVisibilityScope