package dev.olshevski.navigation.reimagined

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
import androidx.compose.ui.unit.IntOffset

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