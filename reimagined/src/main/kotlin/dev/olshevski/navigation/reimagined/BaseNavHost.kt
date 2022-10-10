package dev.olshevski.navigation.reimagined

import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal fun <T, S> BaseNavHost(
    state: NavHostState<T, S>,
    transition: @Composable (NavSnapshot<T, S>) -> NavTransitionState<T, S>
): Unit = key(state.hostId) {
    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.onDispose()
            state.removeOutdatedEntries(state.snapshot)
        }
    }

    val transitionState = enqueueSnapshot(state.snapshot) { targetSnapshot ->
        transition(targetSnapshot)
    }

    val updatedTargetVisibleItems by rememberUpdatedState(transitionState.targetVisibleItems)
    DisposableEffect(state, transitionState.targetVisibleItems) {
        onDispose {
            state.onTransitionStart(updatedTargetVisibleItems)
        }
    }

    DisposableEffect(state, transitionState.currentVisibleItems) {
        state.onTransitionFinish(transitionState.currentVisibleItems)
        onDispose {}
    }

    DisposableEffect(state, transitionState.currentSnapshot) {
        state.removeOutdatedEntries(transitionState.currentSnapshot)
        onDispose {}
    }

}

/**
 * Enqueues a new target state and transitions to it only when all previous transitions finish (one
 * by one).
 *
 * @param transition controls the transition behaviour. It may use [updateTransition] internally or
 * switch to a new target state instantaneously.
 */
@Composable
private fun <T, S> enqueueSnapshot(
    snapshot: NavSnapshot<T, S>,
    transition: @Composable (NavSnapshot<T, S>) -> NavTransitionState<T, S>
): NavTransitionState<T, S> {
    // Queue of pending transitions. The first item in the queue is the currently running
    // transition.
    val queue = remember { mutableStateListOf<NavSnapshot<T, S>>() }
    val targetSnapshot by derivedStateOf { queue.firstOrNull() ?: snapshot }
    val transitionState = transition(targetSnapshot)
    val currentSnapshot = transitionState.currentSnapshot

    DisposableEffect(snapshot) {
        if (currentSnapshot != snapshot) {
            queue.add(snapshot)
        }
        onDispose {}
    }

    DisposableEffect(currentSnapshot) {
        onDispose {
            if (queue.isNotEmpty()) {
                queue.removeFirst()
            }
        }
    }

    return transitionState
}