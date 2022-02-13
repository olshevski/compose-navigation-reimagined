package dev.olshevski.navigation.reimagined

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle

/**
 * Convenient [BackHandler] implementation that supports the most basic logic of back navigation.
 *
 * The underlying [BackHandler] will be kept enabled while the [navController] contains
 * more than one entry in its backstack. The back button press will cause a single
 * [NavController.pop]. This way the back handling may be delegated to upper `BackHandlers`
 * when there are no more items to pop off without leaving the backstack completely empty.
 *
 * For any more specific use-cases feel free to use [BackHandler] directly and define your own
 * behaviour.
 *
 * IMPORTANT NOTICE: place your [NavBackHandler] or [BackHandler] in a composable
 * before a corresponding [NavHost]. As both [BackHandler] and [NavHost] use [Lifecycle]
 * under the hood, there is a case when the order of back handling may be restored incorrectly
 * after a process/activity recreation. This is how the framework works and there is nothing
 * to do about it. Simple placement of [BackHandler] before [NavHost] guarantees no issues
 * in this part.
 */
@Composable
fun <T> NavBackHandler(
    navController: NavController<T>
) {
    BackHandler(enabled = navController.backstack.entries.size > 1) {
        navController.pop()
    }
}