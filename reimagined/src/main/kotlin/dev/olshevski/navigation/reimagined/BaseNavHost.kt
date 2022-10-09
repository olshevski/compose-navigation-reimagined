package dev.olshevski.navigation.reimagined

import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal fun <T, S> BaseNavHost(
    state: NavHostState<T, S>,
    transition: @Composable (NavSnapshot<T, S>) -> NavSnapshot<T, S>
): Unit = key(state.hostId) {
    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.onDispose()
            state.removeOutdatedEntries(state.snapshot)
        }
    }

    val (targetSnapshot, currentSnapshot) = enqueueState(state.snapshot) { targetSnapshot ->
        transition(targetSnapshot)
    }

    // For NavHost: currentSnapshot is the same as targetSnapshot.
    //
    // For AnimatedNavHost: currentSnapshot is the snapshot in transition. When transition
    // finishes, currentSnapshot will become the same as targetSnapshot.

    val updatedTargetSnapshot by rememberUpdatedState(targetSnapshot)
    DisposableEffect(state, targetSnapshot.items.lastOrNull()?.hostEntry) {
        onDispose {
            state.onTransitionStart(updatedTargetSnapshot)
        }
    }

    DisposableEffect(state, currentSnapshot.items.lastOrNull()?.hostEntry) {
        state.onTransitionFinish(currentSnapshot)
        onDispose {}
    }

    DisposableEffect(state, currentSnapshot) {
        state.removeOutdatedEntries(currentSnapshot)
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
@Suppress("SameParameterValue")
@Composable
private fun <T> enqueueState(
    state: T,
    transition: @Composable (T) -> T
): StateInfo<T> {
    // Queue of pending transitions. The first item in the queue is the currently running
    // transition.
    val queue = remember { mutableStateListOf<T>() }
    val targetState by derivedStateOf { queue.firstOrNull() ?: state }
    val currentState = transition(targetState)

    DisposableEffect(state) {
        if (currentState != state) {
            queue.add(state)
        }
        onDispose {}
    }

    DisposableEffect(currentState) {
        onDispose {
            if (queue.isNotEmpty()) {
                queue.removeFirst()
            }
        }
    }

    return StateInfo(targetState = targetState, currentState = currentState)
}

@Stable
private data class StateInfo<T>(
    val targetState: T,
    val currentState: T
)