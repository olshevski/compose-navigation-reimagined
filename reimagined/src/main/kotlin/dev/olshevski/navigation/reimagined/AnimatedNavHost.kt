package dev.olshevski.navigation.reimagined

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@ExperimentalAnimationApi
private val CrossfadeTransitionSpec = object : AnimatedNavHostTransitionSpec<Any?> {

    private fun crossfade() = fadeIn(tween()) with fadeOut(tween())

    override fun AnimatedNavHostTransitionScope.getContentTransform(
        action: NavAction,
        from: Any?,
        to: Any?
    ): ContentTransform = crossfade()

    override fun AnimatedNavHostTransitionScope.toEmptyBackstack(
        action: NavAction,
        from: Any?
    ): ContentTransform = crossfade()

    override fun AnimatedNavHostTransitionScope.fromEmptyBackstack(
        action: NavAction,
        to: Any?
    ): ContentTransform = crossfade()

}

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
fun <T> AnimatedNavHost(
    controller: NavController<T>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
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
fun <T> AnimatedNavHost(
    backstack: NavBackstack<T>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable AnimatedNavHostScope<T>.(T) -> Unit
) = AnimatedNavHost(
    state = rememberNavHostState(backstack),
    transitionSpec = transitionSpec,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

@ExperimentalAnimationApi
@VisibleForTesting
@Composable
internal fun <T> AnimatedNavHost(
    state: NavHostState<T>,
    transitionSpec: AnimatedNavHostTransitionSpec<T> = CrossfadeTransitionSpec,
    emptyBackstackPlaceholder: @Composable AnimatedVisibilityScope.() -> Unit = {},
    contentSelector: @Composable AnimatedNavHostScope<T>.(T) -> Unit
) = BaseNavHost(state) { targetHostEntries ->
    val transition = updateTransition(
        targetState = targetHostEntries,
        label = "AnimatedNavHost"
    )
    transition.AnimatedContent(
        transitionSpec = {
            selectTransition(transitionSpec, state.backstack.action)
        },
        contentKey = { it.lastOrNull()?.id }
    ) { hostEntries ->
        val lastHostEntry = hostEntries.lastOrNull()
        if (lastHostEntry != null) {
            lastHostEntry.ComponentProvider {
                val scope = remember(hostEntries, this@AnimatedContent) {
                    AnimatedNavHostScopeImpl(
                        hostEntries = hostEntries,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                }
                scope.contentSelector(lastHostEntry.destination)
            }
        } else {
            emptyBackstackPlaceholder()
        }
    }
    transition.currentState
}

@ExperimentalAnimationApi
private fun <T> AnimatedContentScope<List<NavHostEntry<T>>>.selectTransition(
    transitionSpec: AnimatedNavHostTransitionSpec<T>,
    action: NavAction,
): ContentTransform {
    val initialStateLastEntry = initialState.lastOrNull()
    val targetStateLastEntry = targetState.lastOrNull()

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
