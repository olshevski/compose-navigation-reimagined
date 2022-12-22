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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Allows you to create new NavHosts with different custom layouts and transitions, reusing the
 * whole internal state and architecture components management intact.
 *
 * BaseNavHost gives you access to the whole backstack of entries through [NavSnapshot],
 * so it is possible to lay out and display several entries or even the whole backstack at the same
 * time.
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
 * BaseNavHost does the internal queueing. It would not send you the next target snapshot unless
 * you finish the transition and return the target snapshot back as a result of [transition].
 * Only then the next target snapshot will be passed into `transition`.
 *
 * All library's default NavHosts use BaseNavHost internally, so you may explore their sources as
 * examples.
 *
 * **Note:** this is still an early public version of the API. It may get some changes
 * in the future.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access scoped [ViewModelStoreOwners][ViewModelStoreOwner]. You may set it to
 * [EmptyScopeSpec] if you don't want your custom NavHost to support scoping.
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
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    visibleItems: (snapshot: NavSnapshot<T, S>) -> Set<NavSnapshotItem<T, S>> = { setOfNotNull(it.items.lastOrNull()) },
    transition: @Composable (snapshot: NavSnapshot<T, S>) -> NavSnapshot<T, S>
) = BaseNavHost(
    state = rememberScopingNavHostState(backstack, scopeSpec),
    visibleItems = visibleItems,
    transition = transition
)

/**
 * Allows you to create new NavHosts with different custom layouts and transitions, reusing the
 * whole internal state and architecture components management intact.
 *
 * BaseNavHost gives you access to the whole backstack of entries through [NavSnapshot],
 * so it is possible to lay out and display several entries or even the whole backstack at the same
 * time.
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
 * BaseNavHost does the internal queueing. It would not send you the next target snapshot unless
 * you finish the transition and return the target snapshot back as a result of [transition].
 * Only then the next target snapshot will be passed into `transition`.
 *
 * All library's default NavHosts use BaseNavHost internally, so you may explore their sources as
 * examples.
 *
 * **Note:** this is still an early public version of the API. It may get some changes
 * in the future.
 *
 * @param state state holder of all internal BaseNavHost state. Stores and manages saved state
 * and all Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry)
 * for every entry and every scope.
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
            enqueueSnapshotTransition(latestSnapshot, transition)

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
            state.onTransitionFinish(currentVisibleItems)
            onDispose {}
        }

        DisposableEffect(currentSnapshot) {
            state.removeOutdatedHostEntries(currentSnapshot)
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
private fun <T> enqueueSnapshotTransition(
    snapshot: T,
    transition: @Composable (snapshot: T) -> T
): TransitionState<T> {
    // Queue of pending transitions. The first item in the queue is the currently running
    // transition.
    val queue = remember { mutableStateListOf<T>() }
    val targetSnapshot by remember(snapshot) {
        derivedStateOf { queue.firstOrNull() ?: snapshot }
    }
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
