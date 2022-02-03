package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@Composable
internal fun <T> BaseNavHost(
    backstack: NavBackstack<T>,
    entryTransition: @Composable (NavComponentEntry<T>?) -> NavComponentEntry<T>?
) {
    // In future, it may be convenient to make possible to create ComponentHolder externally
    // so it is hoistable. But I need to see reasonable use-cases for this.
    val componentHolder = rememberNavComponentHolder(backstack)

    val currentEntry = key(componentHolder.id) {
        entryTransition(componentHolder.lastEntry.value)
        // For NavHost: currentEntry is the same as lastEntry.
        // For AnimatedNavHost: currentEntry is the entry in transition. When transition finishes,
        // currentEntry will become the same as lastEntry.
    }

    DisposableEffect(componentHolder) {
        componentHolder.onCreate()
        onDispose {
            componentHolder.onDispose()
        }
    }

    DisposableEffect(componentHolder, currentEntry) {
        componentHolder.onTransitionFinish()
        onDispose {}
    }
}