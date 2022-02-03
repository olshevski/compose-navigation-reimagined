package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key

/**
 * The basic navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * This composable doesn't do any animated transitions between destinations - it will jump-cut
 * to the next destination.
 *
 * If you need animated transitions use [AnimatedNavHost] instead. For smoother transitions
 * between dialogs use [DialogNavHost].
 *
 * @param controller the navigation controller that will provide its backstack to this `NavHost`.
 * The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed in case you leave the backstack empty. In majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@Composable
fun <T> NavHost(
    controller: NavController<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable (T) -> Unit
) = NavHost(
    backstack = controller.backstack,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

/**
 * The basic navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [backstack].
 *
 * This composable doesn't do any animated transitions between destinations - it will jump-cut
 * to the next destination.
 *
 * If you need animated transitions use [AnimatedNavHost] instead. For smoother transitions
 * between dialogs use [DialogNavHost].
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed in case you leave the backstack empty. In majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@Composable
fun <T> NavHost(
    backstack: NavBackstack<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable (T) -> Unit
) = BaseNavHost(backstack) { lastEntry ->
    key(lastEntry?.id) {
        if (lastEntry != null) {
            lastEntry.ComponentProvider {
                contentSelector(lastEntry.destination)
            }
        } else {
            emptyBackstackPlaceholder()
        }
    }
    lastEntry
}
