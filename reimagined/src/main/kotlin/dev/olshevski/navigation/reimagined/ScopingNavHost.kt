package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember

/**
 * A basic navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * This composable doesn't provide animated transitions between destinations - it will simply
 * jump-cut to the next destination.
 *
 * If you need animated transitions use [AnimatedNavHost] instead. For smoother transitions
 * between dialogs use [DialogNavHost].
 *
 * @param controller a navigation controller that will provide its backstack to this `NavHost`.
 * The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@Composable
fun <T, S> ScopingNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(T) -> Unit
) = ScopingNavHost(
    backstack = controller.backstack,
    scopeSpec = scopeSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

/**
 * A basic navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [backstack].
 *
 * This composable doesn't provide animated transitions between destinations - it will simply
 * jump-cut to the next destination.
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
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@Composable
fun <T, S> ScopingNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(T) -> Unit
) = ScopingNavHost(
    state = rememberNavHostState(backstack, scopeSpec),
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

@Composable
internal fun <T, S> ScopingNavHost(
    state: NavHostState<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(T) -> Unit
) = BaseNavHost(state) { snapshot ->
    val lastSnapshotItem = snapshot.items.lastOrNull()
    key(lastSnapshotItem?.hostEntry?.id) {
        if (lastSnapshotItem != null) {
            lastSnapshotItem.hostEntry.ComponentsProvider {
                val scope = remember(snapshot) {
                    ScopingNavHostScopeImpl(
                        hostEntries = snapshot.items.map { it.hostEntry },
                        scopedHostEntries = lastSnapshotItem.scopedHostEntries
                    )
                }
                scope.contentSelector(lastSnapshotItem.hostEntry.destination)
            }
        } else {
            emptyBackstackPlaceholder()
        }
    }
    return@BaseNavHost snapshot
}