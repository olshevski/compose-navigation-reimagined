package dev.olshevski.navigation.reimagined

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@VisibleForTesting
@Composable
internal fun <T> BaseNavHost(
    state: NavHostState<T>,
    entryTransition: @Composable (List<NavHostEntry<T>>) -> List<NavHostEntry<T>>
) {
    val targetHostEntries = state.hostEntries

    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.onDispose()
            state.removeOutdatedHostEntries()
        }
    }

    val currentHostEntries = key(state.id) {
        entryTransition(targetHostEntries)

        // For NavHost: currentHostEntries is the same as state.hostEntries.
        //
        // For AnimatedNavHost: currentHostEntries are the entries in transition. When transition
        // finishes, currentHostEntries will become the same as state.hostEntries.
    }

    DisposableEffect(state, targetHostEntries.lastOrNull()) {
        onDispose {
            state.onTransitionStart()
        }
    }

    DisposableEffect(state, currentHostEntries.lastOrNull()) {
        if (currentHostEntries.lastOrNull() == targetHostEntries.lastOrNull()) {
            state.onTransitionFinish()
        }
        onDispose {}
    }

    DisposableEffect(state, currentHostEntries) {
        if (currentHostEntries == targetHostEntries) {
            state.removeOutdatedHostEntries()
        }
        onDispose {}
    }

}