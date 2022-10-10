package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

/**
 * An animated navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * This composable uses animated transitions to switch between destinations. You may set a custom
 * [AnimatedNavHostTransitionSpec] to specify the desired transitions.
 *
 * If you don't need animated transitions use [NavHost] instead.
 *
 * @param controller a navigation controller that will provide its backstack to this
 * `AnimatedNavHost`. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param transitionSpec specifies the desired transitions. If not set, the default transition
 * will be a simple crossfade.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingAnimatedNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable ScopingAnimatedNavHostScope<T, S>.(T) -> Unit
) = ScopingAnimatedNavHost(
    backstack = controller.backstack,
    scopeSpec = scopeSpec,
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

/**
 * An animated navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [backstack].
 *
 * This composable uses animated transitions to switch between destinations. You may set a custom
 * [AnimatedNavHostTransitionSpec] to specify the desired transitions.
 *
 * If you don't need animated transitions use [NavHost] instead.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param transitionSpec specifies the desired transitions. If not set, the default transition
 * will be a simple crossfade.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingAnimatedNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable ScopingAnimatedNavHostScope<T, S>.(T) -> Unit
) = ScopingAnimatedNavHost(
    state = rememberNavHostState(backstack, scopeSpec),
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

@ExperimentalAnimationApi
@Composable
internal fun <T, S> ScopingAnimatedNavHost(
    state: NavHostState<T, S>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable ScopingAnimatedNavHostScope<T, S>.(T) -> Unit
) = BaseNavHost(state) { targetSnapshot ->
    val transition = updateTransition(
        targetState = targetSnapshot,
        label = "AnimatedNavHost"
    )
    transition.AnimatedContent(
        transitionSpec = {
            selectTransition(transitionSpec, targetState.action)
        },
        contentKey = { it.items.lastOrNull()?.hostEntry?.id }
    ) { snapshot ->
        val lastSnapshotItem = snapshot.items.lastOrNull()
        if (lastSnapshotItem != null) {
            lastSnapshotItem.hostEntry.ComponentProvider {
                val scope = remember(snapshot.items, this@AnimatedContent) {
                    ScopingAnimatedNavHostScopeImpl(
                        hostEntries = snapshot.items.map { it.hostEntry },
                        scopedHostEntries = lastSnapshotItem.scopedHostEntries,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                }
                scope.contentSelector(lastSnapshotItem.hostEntry.destination)
            }
        } else {
            emptyBackstackPlaceholder()
        }
    }

    val currentSnapshot = transition.currentState
    return@BaseNavHost NavTransitionState(
        targetSnapshot = targetSnapshot,
        currentSnapshot = currentSnapshot,
        targetVisibleItems = setOfNotNull(targetSnapshot.items.lastOrNull()),
        currentVisibleItems = setOfNotNull(currentSnapshot.items.lastOrNull())
    )
}

@ExperimentalAnimationApi
private fun <T, S> AnimatedContentScope<NavSnapshot<T, S>>.selectTransition(
    transitionSpec: AnimatedNavHostTransitionSpec<T>,
    action: NavAction,
): ContentTransform {
    val initialStateLastEntry = initialState.items.lastOrNull()?.hostEntry
    val targetStateLastEntry = targetState.items.lastOrNull()?.hostEntry

    // Request transition spec only when anything actually changes and should be animated.
    // For some reason AnimatedContent calls for transitionSpec even when created initially
    // which doesn't make much sense.
    return if (initialStateLastEntry?.id != targetStateLastEntry?.id) {
        val scope = AnimatedNavHostTransitionScopeImpl(this)
        with(transitionSpec) {
            when {
                initialStateLastEntry == null -> scope.fromEmptyBackstack(
                    action = action,
                    to = targetStateLastEntry!!.destination
                )
                targetStateLastEntry == null -> scope.toEmptyBackstack(
                    action = action,
                    from = initialStateLastEntry.destination
                )
                else -> scope.getContentTransform(
                    action = action,
                    from = initialStateLastEntry.destination,
                    to = targetStateLastEntry.destination
                )
            }
        }
    } else {
        EnterTransition.None with ExitTransition.None
    }
}
