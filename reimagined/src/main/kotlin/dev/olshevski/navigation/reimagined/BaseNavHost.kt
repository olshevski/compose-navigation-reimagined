package dev.olshevski.navigation.reimagined

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@VisibleForTesting
@Composable
internal fun <T> BaseNavHost(
    state: NavHostState<T>,
    transition: @Composable (NavSnapshot<T>) -> NavSnapshot<T>
) {
    val targetSnapshot = state.targetSnapshot

    DisposableEffect(state) {
        state.onCreate()

        onDispose {
            state.onDispose()
            state.removeOutdatedHostEntries(targetSnapshot)
        }
    }

    val currentSnapshot = key(state.id) {
        transition(targetSnapshot)

        // For NavHost: currentSnapshot is the same as targetSnapshot.
        //
        // For AnimatedNavHost: currentSnapshot is the snapshot in transition. When transition
        // finishes, currentSnapshot will become the same as targetSnapshot.
    }

    DisposableEffect(state, targetSnapshot.hostEntries.lastOrNull()) {
        onDispose {
            Log.v("ERASEME", "onTransitionStart ${targetSnapshot.hostEntries.lastOrNull()}")
            state.onTransitionStart()
        }
    }

    DisposableEffect(state, currentSnapshot.hostEntries.lastOrNull()) {
        if (currentSnapshot.hostEntries.lastOrNull() == targetSnapshot.hostEntries.lastOrNull()) {
            Log.v("ERASEME", "onAllTransitionsFinish ${currentSnapshot.hostEntries.lastOrNull()}")
            state.onAllTransitionsFinish()
        }
        onDispose {}
    }

    DisposableEffect(state, currentSnapshot) {
        Log.v("ERASEME", "removeOutdatedHostEntries ${currentSnapshot.outdatedEntryIds}")
        state.removeOutdatedHostEntries(currentSnapshot)
        onDispose {}
    }

}