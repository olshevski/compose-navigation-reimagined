package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedVisibilityScope

interface AnimatedNavHostScope<out T> : NavHostScope<T>, AnimatedVisibilityScope

internal class AnimatedNavHostScopeImpl<out T>(
    hostEntries: List<NavHostEntry<T>>,
    animatedVisibilityScope: AnimatedVisibilityScope
) : NavHostScopeImpl<T>(hostEntries), AnimatedNavHostScope<T>,
    AnimatedVisibilityScope by animatedVisibilityScope