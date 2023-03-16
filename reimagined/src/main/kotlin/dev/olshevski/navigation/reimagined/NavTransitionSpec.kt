package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with

/**
 * The specification of all animated transition between destinations. Used in [AnimatedNavHost].
 */
@ExperimentalAnimationApi
fun interface NavTransitionSpec<in T> {

    /**
     * Returns a [ContentTransform] that describes the desired transition between
     * destinations [to] and [from].
     *
     * This method is called in [NavTransitionScope] and provides convenient
     * methods [NavTransitionScope.slideOutOfContainer],
     * [NavTransitionScope.slideIntoContainer]
     * and [NavTransitionScope.using]. This is the same methods that you would be
     * using in [AnimatedContentScope] from [AnimatedContent] composable. In fact,
     * [AnimatedNavHost] uses `AnimatedContent` under the hood, so you may explore documentation
     * of `AnimatedContent` for better understanding of the API.
     *
     * If you want to provide a specific transition to and from an empty backstack state,
     * override methods [toEmptyBackstack] and [fromEmptyBackstack] of [NavTransitionSpec].
     *
     * @param action a hint about the last change done through a [NavController]. May be used
     * to select an animation that better corresponds to the action. A simple example would be
     * a "backward" sliding animation for [NavAction.Pop] and "forward" sliding animation
     * in all other cases.
     *
     * @param to a previous visible destination
     *
     * @param from a target visible destination
     *
     * @see AnimatedContent
     * @see AnimatedContentScope
     */
    fun NavTransitionScope.getContentTransform(
        action: NavAction,
        from: T,
        to: T
    ): ContentTransform

    /**
     * Returns a [ContentTransform] that describes the desired transition between
     * a previous destination and an empty backstack state.
     *
     * Note: you may need set some non-zero width/height composable as
     * `emptyBackstackPlaceholder` in [AnimatedNavHost] in order for a transition to run correctly.
     */
    fun NavTransitionScope.toEmptyBackstack(
        action: NavAction,
        from: T
    ): ContentTransform = EnterTransition.None with ExitTransition.None

    /**
     * Returns a [ContentTransform] that describes the desired transition between
     * an empty backstack state and a new destination
     *
     * Note: you may need set some non-zero width/height composable as
     * `emptyBackstackPlaceholder` in [AnimatedNavHost] in order for a transition to run correctly.
     */
    fun NavTransitionScope.fromEmptyBackstack(
        action: NavAction,
        to: T
    ): ContentTransform = EnterTransition.None with ExitTransition.None

}

@ExperimentalAnimationApi
internal val CrossfadeTransitionSpec = object : NavTransitionSpec<Any?> {

    private fun crossfade() = fadeIn(tween()) with fadeOut(tween())

    override fun NavTransitionScope.getContentTransform(
        action: NavAction,
        from: Any?,
        to: Any?
    ): ContentTransform = crossfade()

    override fun NavTransitionScope.toEmptyBackstack(
        action: NavAction,
        from: Any?
    ): ContentTransform = crossfade()

    override fun NavTransitionScope.fromEmptyBackstack(
        action: NavAction,
        to: Any?
    ): ContentTransform = crossfade()

}
