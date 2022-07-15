package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key

@Composable
internal fun <T> BaseNavHost(
    backstack: NavBackstack<T>,
    entryTransition: @Composable (NavComponentEntry<T>?) -> NavComponentEntry<T>?
) {
    // In future, it may be convenient to make possible to create ComponentHolder externally,
    // so it is hoistable. But I need to see reasonable use-cases for this.
    val componentHolder = rememberNavComponentHolder(backstack)

    val currentComponentEntry = key(componentHolder.id) {
        entryTransition(componentHolder.lastComponentEntry)

        // For NavHost: currentComponentEntry is the same as lastComponentEntry.
        //
        // For AnimatedNavHost: currentComponentEntry is the entry in transition. When transition
        // finishes, currentComponentEntry will become the same as currentComponentEntry.
    }

    DisposableEffect(componentHolder, currentComponentEntry) {
        onDispose {
            // should be called only in onDispose, because it affects lifecycle events
            componentHolder.onTransitionFinish()
        }
    }

    DisposableEffect(componentHolder) {
        componentHolder.onCreate()
        onDispose {
            componentHolder.onDispose()
        }
    }
}