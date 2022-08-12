package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@Composable
internal fun <T> BaseNavHost(
    backstack: NavBackstack<T>,
    entryTransition: @Composable (List<NavHostEntry<T>>) -> List<NavHostEntry<T>>
) {
    // In the future, it may be convenient to make possible to create NavHostState externally,
    // so it is hoistable. But I need to see reasonable use-cases for this.
    val state = rememberNavHostState(backstack)
    val targetHostEntries = state.hostEntries

    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.removeOutdatedHostEntries()
            state.onDispose()
        }
    }

    DisposableEffect(state, targetHostEntries.lastOrNull()) {
        state.onTransitionStart()
        onDispose {}
    }

    val currentHostEntries = key(state.id) {
        entryTransition(targetHostEntries)

        // For NavHost: currentHostEntries is the same as state.hostEntries.
        //
        // For AnimatedNavHost: currentHostEntries are the entries in transition. When transition
        // finishes, currentHostEntries will become the same as state.hostEntries.
    }

    DisposableEffect(state, currentHostEntries) {
        state.removeOutdatedHostEntries()
        onDispose {}
    }

    DisposableEffect(state, currentHostEntries.lastOrNull()) {
        state.onTransitionFinish()
        onDispose {}
    }
}