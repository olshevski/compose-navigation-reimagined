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
import androidx.lifecycle.ViewModelStoreOwner

/**
 * An animated navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * This composable uses animated transitions to switch between destinations. You may set a custom
 * [NavTransitionSpec] to specify the desired transitions.
 *
 * If you don't need animated transitions use [NavHost] instead.
 *
 * @param controller a navigation controller that will provide its backstack to this
 * AnimatedNavHost. The last entry of the backstack is always the currently displayed entry.
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
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the AnimatedNavHost through the [AnimatedNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T> AnimatedNavHost(
    controller: NavController<T>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable AnimatedNavHostScope<T>.(T) -> Unit
) = AnimatedNavHost(
    backstack = controller.backstack,
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
 * [NavTransitionSpec] to specify the desired transitions.
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
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the AnimatedNavHost through the [AnimatedNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T> AnimatedNavHost(
    backstack: NavBackstack<T>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable AnimatedNavHostScope<T>.(T) -> Unit
) = AnimatedNavHost(
    state = rememberNavHostState(backstack, EmptyScopeSpec),
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

@ExperimentalAnimationApi
@Composable
internal fun <T, S> AnimatedNavHost(
    state: NavHostState<T, S>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable ScopingAnimatedNavHostScope<T, S>.(T) -> Unit
) = ScopingAnimatedNavHost(
    state = state,
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

/**
 * An animated navigation host that selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * This composable uses animated transitions to switch between destinations. You may set a custom
 * [NavTransitionSpec] to specify the desired transitions.
 *
 * If you don't need animated transitions use [NavHost] instead.
 *
 * **Scoping:**
 *
 * This version of AnimatedNavHost gives you the ability to define scoped
 * [ViewModelStoreOwners][ViewModelStoreOwner] that can be shared between arbitrary destinations.
 *
 * To do so, you must return a desired set of scopes for each requested destination in
 * [scopeSpec]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStoreOwner
 * and all of its ViewModels will be cleared.
 *
 * To access a scoped ViewModelStoreOwner, you may call
 * [ScopingNavHostScope.getScopedViewModelStoreOwner] inside [contentSelector] with the same scope
 * object you've returned in [scopeSpec]. Then you may pass this scoped ViewModelStoreOwner
 * as a parameter into a ViewModel provider method of choice and create shared ViewModels,
 * easily accessible from different destinations.
 *
 * @param controller a navigation controller that will provide its backstack to this
 * AnimatedNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param transitionSpec specifies the desired transitions. If not set, the default transition
 * will be a simple crossfade.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingAnimatedNavHost through
 * the [ScopingAnimatedNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingAnimatedNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
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
 * [NavTransitionSpec] to specify the desired transitions.
 *
 * If you don't need animated transitions use [NavHost] instead.
 *
 * **Scoping:**
 *
 * This version of AnimatedNavHost gives you the ability to define scoped
 * [ViewModelStoreOwners][ViewModelStoreOwner] that can be shared between arbitrary destinations.
 *
 * To do so, you must return a desired set of scopes for each requested destination in
 * [scopeSpec]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStoreOwner
 * and all of its ViewModels will be cleared.
 *
 * To access a scoped ViewModelStoreOwner, you may call
 * [ScopingNavHostScope.getScopedViewModelStoreOwner] inside [contentSelector] with the same scope
 * object you've returned in [scopeSpec]. Then you may pass this scoped ViewModelStoreOwner
 * as a parameter into a ViewModel provider method of choice and create shared ViewModels,
 * easily accessible from different destinations.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param transitionSpec specifies the desired transitions. If not set, the default transition
 * will be a simple crossfade.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingAnimatedNavHost through
 * the [ScopingAnimatedNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingAnimatedNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable ScopingAnimatedNavHostScope<T, S>.(T) -> Unit
) = ScopingAnimatedNavHost(
    state = rememberNavHostState(backstack, scopeSpec),
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

@OptIn(ExperimentalReimaginedApi::class)
@ExperimentalAnimationApi
@Composable
internal fun <T, S> ScopingAnimatedNavHost(
    state: NavHostState<T, S>,
    transitionSpec: NavTransitionSpec<T> = CrossfadeTransitionSpec,
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
            lastSnapshotItem.hostEntry.ComponentsProvider {
                val scope = remember(snapshot, this@AnimatedContent) {
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
    return@BaseNavHost transition.currentState
}

@ExperimentalReimaginedApi
@ExperimentalAnimationApi
private fun <T, S> AnimatedContentScope<NavSnapshot<T, S>>.selectTransition(
    transitionSpec: NavTransitionSpec<T>,
    action: NavAction,
): ContentTransform {
    val initialStateLastEntry = initialState.items.lastOrNull()?.hostEntry
    val targetStateLastEntry = targetState.items.lastOrNull()?.hostEntry

    // Request transition spec only when anything actually changes and should be animated.
    // For some reason AnimatedContent calls for transitionSpec even when created initially
    // which doesn't make much sense.
    return if (initialStateLastEntry?.id != targetStateLastEntry?.id) {
        val scope = NavTransitionScopeImpl(this)
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
