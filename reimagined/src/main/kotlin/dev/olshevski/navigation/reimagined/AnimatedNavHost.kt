package dev.olshevski.navigation.reimagined

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
) = BaseNavHost(backstack) { lastHostEntry ->
    val transition = updateTransition(
        targetState = lastHostEntry,
        label = "AnimatedNavHost"
    )
    transition.AnimatedContent(
        transitionSpec = {
            selectTransition(transitionSpec, backstack.action)
        },
        contentKey = { it?.id }
    ) { hostEntry ->
        if (hostEntry != null) {
            hostEntry.ComponentProvider {
                val scope = remember(backstack, hostEntry, this@BaseNavHost, this@AnimatedContent) {
                    AnimatedNavHostScopeImpl(
                        backstack = backstack,
                        currentHostEntry = hostEntry,
                        hostStateScope = this@BaseNavHost,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                }
                scope.contentSelector(hostEntry.destination)
            }
        } else {
            emptyBackstackPlaceholder()
        }
    }
    transition.currentState
}

@ExperimentalAnimationApi
private fun <T> AnimatedContentScope<NavHostEntry<T>?>.selectTransition(
    transitionSpec: AnimatedNavHostTransitionSpec<T>,
    action: NavAction,
): ContentTransform {
    // Request transition spec only when anything actually changes and should be animated.
    // For some reason AnimatedContent calls for transitionSpec even when created initially
    // which doesn't make much sense.
    return if (initialState?.id != targetState?.id) {
        val scope = AnimatedNavHostTransitionScopeImpl(this)
        with(transitionSpec) {
            when {
                initialState == null -> scope.fromEmptyBackstack(
                    action = action,
                    to = targetState!!.destination
                )
                targetState == null -> scope.toEmptyBackstack(
                    action = action,
                    from = initialState!!.destination
                )
                else -> scope.getContentTransform(
                    action = action,
                    from = initialState!!.destination,
                    to = targetState!!.destination
                )
            }
        }
    } else {
        EnterTransition.None with ExitTransition.None
    }
}
