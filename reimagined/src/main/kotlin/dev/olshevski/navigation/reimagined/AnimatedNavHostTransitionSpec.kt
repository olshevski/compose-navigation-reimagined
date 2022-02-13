package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.ui.unit.IntOffset

/**
 * The specification of all animated transition between destinations. Used in [AnimatedNavHost].
 */
@ExperimentalAnimationApi
fun interface AnimatedNavHostTransitionSpec<in T> {

    /**
     * Returns a [ContentTransform] that describes the desired transition between
     * destinations [to] and [from].
     *
     * This method is called in [AnimatedNavHostTransitionScope] and provides convenient
     * methods [AnimatedNavHostTransitionScope.slideOutOfContainer],
     * [AnimatedNavHostTransitionScope.slideIntoContainer]
     * and [AnimatedNavHostTransitionScope.using]. This is the same methods that you would be
     * using in [AnimatedContentScope] from [AnimatedContent] composable. In fact,
     * [AnimatedNavHost] uses `AnimatedContent` under the hood, so you may explore documentation
     * of `AnimatedContent` for better understanding of the API.
     *
     * If you want to provide a specific transition to and from an empty backstack state,
     * override methods [toEmptyBackstack] and [fromEmptyBackstack] of
     * [AnimatedNavHostTransitionSpec].
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
    fun AnimatedNavHostTransitionScope.getContentTransform(
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
    fun AnimatedNavHostTransitionScope.toEmptyBackstack(
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
    fun AnimatedNavHostTransitionScope.fromEmptyBackstack(
        action: NavAction,
        to: T
    ): ContentTransform = EnterTransition.None with ExitTransition.None

}

@ExperimentalAnimationApi
interface AnimatedNavHostTransitionScope {

    /**
     * This defines a horizontal/vertical exit transition to completely slide out of the
     * [AnimatedNavHost] container. The offset amount is dynamically calculated based on the current
     * size of the [AnimatedNavHost] and the new target size. This offset gets passed
     * to [targetOffset] lambda. By default, [targetOffset] uses this offset as is, but it can be
     * customized to slide a distance based on the offset. [slideOutOfContainer] is a
     * convenient alternative to [slideOutHorizontally] and [slideOutVertically] when the incoming
     * and outgoing content differ in size. Otherwise, it would be equivalent to
     * [slideOutHorizontally] and [slideOutVertically] with an offset of the full width/height.
     *
     * [towards] specifies the slide direction. Content can be slided out of the container towards
     * [SlideDirection.Left], [SlideDirection.Right], [SlideDirection.Up] and [SlideDirection.Down].
     *
     * [animationSpec] defines the animation that will be used to animate the slide-out.
     */
    fun slideOutOfContainer(
        towards: SlideDirection,
        animationSpec: FiniteAnimationSpec<IntOffset> = spring(
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
        targetOffset: (offsetForFullSlide: Int) -> Int = { it }
    ): ExitTransition

    /**
     * This defines a horizontal/vertical slide-in that is specific to [AnimatedNavHost] from the
     * edge of the container. The offset amount is dynamically calculated based on the current
     * size of the [AnimatedNavHost] and its content alignment. This offset (may be positive or
     * negative based on the direction of the slide) is then passed to [initialOffset]. By default,
     * [initialOffset] will be using the offset calculated from the system to slide the content in.
     * [slideIntoContainer] is a convenient alternative to [slideInHorizontally] and
     * [slideInVertically] when the incoming and outgoing content
     * differ in size. Otherwise, it would be equivalent to [slideInHorizontally] and
     * [slideInVertically] with an offset of the full width/height.
     *
     * [towards] specifies the slide direction. Content can be slided into the container towards
     * [SlideDirection.Left], [SlideDirection.Right], [SlideDirection.Up] and [SlideDirection.Down].
     *
     * [animationSpec] defines the animation that will be used to animate the slide-in.
     */
    fun slideIntoContainer(
        towards: SlideDirection,
        animationSpec: FiniteAnimationSpec<IntOffset> = spring(
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
        initialOffset: (offsetForFullSlide: Int) -> Int = { it }
    ): EnterTransition

    /**
     * Customizes the [SizeTransform] of a given [ContentTransform].
     */
    infix fun ContentTransform.using(sizeTransform: SizeTransform?): ContentTransform

}

@ExperimentalAnimationApi
internal class AnimatedNavHostTransitionScopeImpl<S>(
    private val animatedContentScope: AnimatedContentScope<S>
) : AnimatedNavHostTransitionScope {

    override fun slideOutOfContainer(
        towards: SlideDirection,
        animationSpec: FiniteAnimationSpec<IntOffset>,
        targetOffset: (offsetForFullSlide: Int) -> Int
    ) = animatedContentScope.slideOutOfContainer(towards, animationSpec, targetOffset)

    override fun slideIntoContainer(
        towards: SlideDirection,
        animationSpec: FiniteAnimationSpec<IntOffset>,
        initialOffset: (offsetForFullSlide: Int) -> Int
    ) = animatedContentScope.slideIntoContainer(towards, animationSpec, initialOffset)

    override fun ContentTransform.using(sizeTransform: SizeTransform?) =
        with(animatedContentScope) { using(sizeTransform) }

}