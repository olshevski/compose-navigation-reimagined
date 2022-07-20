package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@Composable
internal fun <T> BaseNavHost(
    backstack: NavBackstack<T>,
    entryTransition: @Composable NavHostStateScope<T>.(NavHostEntry<T>?) -> NavHostEntry<T>?
) {
    // In the future, it may be convenient to make possible to create NavHostState externally,
    // so it is hoistable. But I need to see reasonable use-cases for this.
    val navHostState = rememberNavHostState(backstack)

    val currentNavHostEntry = key(navHostState.id) {
        navHostState.entryTransition(navHostState.lastNavHostEntry)

        // For NavHost: currentNavHostEntry is the same as lastNavHostEntry.
        //
        // For AnimatedNavHost: currentNavHostEntry is the entry in transition. When transition
        // finishes, currentNavHostEntry will become the same as lastNavHostEntry.
    }

    DisposableEffect(navHostState, currentNavHostEntry) {
        onDispose {
            // should be called only in onDispose, because it affects lifecycle events
            navHostState.onTransitionFinish()
        }
    }

    DisposableEffect(navHostState) {
        navHostState.onCreate()
        onDispose {
            navHostState.onDispose()
        }
    }
}