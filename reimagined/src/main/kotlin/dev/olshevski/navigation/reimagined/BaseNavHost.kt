package dev.olshevski.navigation.reimagined

import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle

/**
 * Allows you to create new NavHosts with different custom layouts and transitions, reusing the
 * whole internal state and architecture components management intact.
 *
 * BaseNavHost gives you access to the entire backstack of entries through [NavSnapshot],
 * so it is possible to lay out and display several or all entries at the same time.
 *
 * BaseNavHost uses [NavSnapshots][NavSnapshot] as anchor points of navigation. The transition
 * between two NavSnapshots is defined with [transition]. BaseNavHost passes you the snapshot
 * changes as an input parameter of `transition`. You may choose to switch to the passed target
 * snapshot immediately as in [NavHost] or with some animation as in [AnimatedNavHost].
 * The returned snapshot is what notifies BaseNavHost of when the current transition to
 * the requested snapshot ends: simply return the previous snapshot until you finish transitioning
 * to the new target snapshot.  When you are done, return the target snapshot.
 *
 * This is similar to how [updateTransition] treats its `targetState` and `currentState`:
 * `currentState` is set to `targetState` only when  transition ends. You may think of the input
 * parameter of [transition] as `targetState` and the returned value as `currentState`.
 *
 * **Queueing:**
 *
 * BaseNavHost does the internal queueing depending on [transitionQueueing]:
 *
 * - If the parameter is either [QueueAll][NavTransitionQueueing.QueueAll] or
 * [ConflateQueued][NavTransitionQueueing.ConflateQueued], then BaseNavHost would not send you
 * the next target snapshot unless you finish the current transition and return the target snapshot
 * back as a result of [transition]. Only then the next target snapshot will be passed into
 * `transition`.
 *
 * - If the parameter is [InterruptCurrent][NavTransitionQueueing.InterruptCurrent] then
 * BaseNavHost will send you any new target snapshot as soon as possible. It is up to the
 * implementation to resolve and animate the interruption correctly. When the last uninterrupted
 * transition finishes you must return the most recent target snapshot.
 *
 * All library's default NavHosts use BaseNavHost internally, so you may explore their sources as
 * examples.
 *
 * **Note:** this is still an early public version of the API. It may get some changes
 * in the future.
 *
 * @param state the holder of all internal BaseNavHost state. Stores and manages saved state
 * and all Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry)
 * for every entry and every scope.
 *
 * @param transitionQueueing the strategy of processing incoming transitions when transition
 * animations run slower than being added
 *
 * @param visibleItems controls the lifecycle states of [NavHostEntries][NavHostEntry].
 * By default, only the last entry is considered active and promoted to
 * [RESUMED][Lifecycle.State.RESUMED] state. With this function parameter, you can have several
 * entries receive `RESUMED` state. It is also possible to control lifecycle states of entries
 * dynamically. BaseNavHost will subscribe to all [State] object changes read inside [visibleItems].
 */
@ExperimentalReimaginedApi
@Composable
fun <T, S> BaseNavHost(
    state: ScopingNavHostState<T, S>,
    transitionQueueing: NavTransitionQueueing,
    visibleItems: (snapshot: NavSnapshot<T, S>) -> Set<NavSnapshotItem<T, S>> = { setOfNotNull(it.items.lastOrNull()) },
    transition: @Composable (snapshot: NavSnapshot<T, S>) -> NavSnapshot<T, S>
) {
    state as NavHostStateImpl
    key(state.hostId) {
        val latestSnapshot by remember { derivedStateOf { state.createSnapshot() } }

        DisposableEffect(Unit) {
            onDispose {
                state.removeOutdatedHostEntries(latestSnapshot)
            }
        }

        val (targetSnapshot, currentSnapshot) =
            enqueueSnapshotTransition(latestSnapshot, transitionQueueing, transition)

        val targetVisibleItems by remember(targetSnapshot) {
            derivedStateOf { visibleItems(targetSnapshot) }
        }
        val currentVisibleItems by remember(currentSnapshot) {
            derivedStateOf { visibleItems(currentSnapshot) }
        }

        val updatedTargetVisibleItems by rememberUpdatedState(targetVisibleItems)
        DisposableEffect(targetVisibleItems) {
            onDispose {
                state.onTransitionStart(updatedTargetVisibleItems)
            }
        }

        DisposableEffect(currentVisibleItems) {
            if (currentVisibleItems == targetVisibleItems) {
                state.onTransitionFinish(currentVisibleItems)
            }
            onDispose {}
        }

        DisposableEffect(currentSnapshot) {
            if (currentSnapshot == targetSnapshot) {
                state.removeOutdatedHostEntries(currentSnapshot)
            }
            onDispose {}
        }
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
private fun <T, S> enqueueSnapshotTransition(
    snapshot: NavSnapshot<T, S>,
    transitionQueueing: NavTransitionQueueing,
    transition: @Composable (snapshot: NavSnapshot<T, S>) -> NavSnapshot<T, S>
): TransitionState<NavSnapshot<T, S>> {
    // Queue of pending transitions. The first item in the queue is the currently running
    // transition.
    val queue = remember { mutableStateListOf<NavSnapshot<T, S>>() }
    val targetSnapshot = queue.firstOrNull() ?: snapshot
    val currentSnapshot = transition(targetSnapshot)

    DisposableEffect(snapshot) {
        if (currentSnapshot != snapshot) {
            addToQueue(queue, snapshot, transitionQueueing)
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

private fun <T, S> addToQueue(
    queue: SnapshotStateList<NavSnapshot<T, S>>,
    snapshot: NavSnapshot<T, S>,
    transitionQueueing: NavTransitionQueueing
) {
    when (transitionQueueing) {
        NavTransitionQueueing.QueueAll -> queue.add(snapshot)
        NavTransitionQueueing.ConflateQueued -> {
            // Keep 2 items max: the first item is the currently running transition, the second one
            // is the pending transition. Replace the pending transition.
            if (queue.size < 2) {
                queue.add(snapshot)
            } else {
                // covers the case when transitionQueueing was changed on the fly
                queue.removeRange(1, queue.size)
                queue.add(snapshot)
            }
        }
        NavTransitionQueueing.InterruptCurrent -> if (queue.isNotEmpty()) {
            queue.clear()
        }
    }
}

@Stable
private data class TransitionState<T>(
    val targetSnapshot: T,
    val currentSnapshot: T
)
