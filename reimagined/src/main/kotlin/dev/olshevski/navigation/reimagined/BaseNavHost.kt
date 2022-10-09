package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@Composable
internal fun <T, S> BaseNavHost(
    state: NavHostState<T, S>,
    transition: @Composable (NavSnapshot<T, S>) -> NavSnapshot<T, S>
) {
    val targetSnapshot = state.targetSnapshot

    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.onDispose()
            state.removeOutdatedEntries(targetSnapshot)
        }
    }

    val currentSnapshot = key(state.hostId) {
        transition(targetSnapshot)

        // For NavHost: currentSnapshot is the same as targetSnapshot.
        //
        // For AnimatedNavHost: currentSnapshot is the snapshot in transition. When transition
        // finishes, currentSnapshot will become the same as targetSnapshot.
    }

    DisposableEffect(state, targetSnapshot.items.lastOrNull()?.hostEntry) {
        onDispose {
            state.onTransitionStart()
        }
    }

    DisposableEffect(state, currentSnapshot.items.lastOrNull()?.hostEntry) {
        if (currentSnapshot.items.lastOrNull()?.hostEntry == targetSnapshot.items.lastOrNull()?.hostEntry) {
            state.onAllTransitionsFinish()
        }
        onDispose {}
    }

    DisposableEffect(state, currentSnapshot) {
        state.removeOutdatedEntries(currentSnapshot)
        onDispose {}
    }

}