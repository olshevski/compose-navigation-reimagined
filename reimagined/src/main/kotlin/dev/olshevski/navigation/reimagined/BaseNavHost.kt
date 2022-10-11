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
    visibleItems: (NavSnapshot<T, S>) -> Set<NavSnapshotItem<T, S>> = { setOfNotNull(it.items.lastOrNull()) },
    transition: @Composable (NavSnapshot<T, S>) -> NavSnapshot<T, S>
): Unit = key(state.hostId) {
    DisposableEffect(state) {
        state.onCreate()
        onDispose {
            state.onDispose()
            state.removeOutdatedEntries(state.snapshot)
        }
    }

    val (targetSnapshot, currentSnapshot) = enqueueSnapshotTransition(state.snapshot, transition)

    val targetVisibleItems by remember(targetSnapshot) {
        derivedStateOf { visibleItems(targetSnapshot) }
    }
    val currentVisibleItems by remember(currentSnapshot) {
        derivedStateOf { visibleItems(currentSnapshot) }
    }

    val updatedTargetVisibleItems by rememberUpdatedState(targetVisibleItems)
    DisposableEffect(state, targetVisibleItems) {
        onDispose {
            state.onTransitionStart(updatedTargetVisibleItems)
        }
    }

    DisposableEffect(state, currentVisibleItems) {
        state.onTransitionFinish(currentVisibleItems)
        onDispose {}
    }

    DisposableEffect(state, currentSnapshot) {
        state.removeOutdatedEntries(currentSnapshot)
        onDispose {}
    }
}

/**
 * Enqueues a new target snapshot and transitions to it only when all previous transitions finish
 * (one by one).
 *
 * @param transition controls the transition behaviour. It may use [updateTransition] internally or
 * switch to a new target state instantaneously.
 */
@Composable
private fun <T> enqueueSnapshotTransition(
    snapshot: T,
    transition: @Composable (T) -> T
): TransitionState<T> {
    // Queue of pending transitions. The first item in the queue is the currently running
    // transition.
    val queue = remember { mutableStateListOf<T>() }
    val targetSnapshot by derivedStateOf { queue.firstOrNull() ?: snapshot }
    val currentSnapshot = transition(targetSnapshot)

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

    return TransitionState(targetSnapshot = targetSnapshot, currentSnapshot = currentSnapshot)
}

@Stable
private data class TransitionState<T>(
    val targetSnapshot: T,
    val currentSnapshot: T
)
