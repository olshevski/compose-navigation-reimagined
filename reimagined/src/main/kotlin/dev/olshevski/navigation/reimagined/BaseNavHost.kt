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
    val state = rememberNavHostState(backstack)

    val currentHostEntry = key(state.id) {
        state.entryTransition(state.lastHostEntry)

        // For NavHost: currentHostEntry is the same as lastHostEntry.
        //
        // For AnimatedNavHost: currentHostEntry is the entry in transition. When transition
        // finishes, currentHostEntry will become the same as lastHostEntry.
    }

    DisposableEffect(state, currentHostEntry) {
        onDispose {
            // should be called only in onDispose, because it affects lifecycle events
            state.onTransitionFinish()
        }
    }

    DisposableEffect(state) {
        state.onCreate()
        onDispose {
            state.onDispose()
        }
    }
}