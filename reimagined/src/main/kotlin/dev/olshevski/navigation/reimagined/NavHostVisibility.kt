package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import kotlinx.parcelize.Parcelize

@Parcelize
private object VisibilityUnit : Parcelable {
    override fun toString() = this::class.simpleName!!
}

private fun NavController<VisibilityUnit>.setVisibility(visible: Boolean) {
    when {
        visible && backstack.entries.isEmpty() -> {
            setNewBackstack(listOf(navEntry(VisibilityUnit)))
        }
        !visible && backstack.entries.isNotEmpty() -> {
            setNewBackstack(emptyList())
        }
    }
}

/**
 * Controls the visibility of any NavHost that is placed inside [content]. When [visible] parameter
 * is set to false, [content] will be removed from composition, all saved states and architecture
 * components (Lifecycle, ViewModelStore, SavedStateRegistry) of NavHost entries will be cleared.
 *
 * If you do not want to clear entries' saved states and architecture components, you may
 * hoist NavHostState and use simple condition to control the visibility of NavHost:
 *
 * ```kotlin
 * val state = rememberNavHostState(backstack)
 * if (visible) {
 *      NavHost(state) {
 *          ...
 *      }
 * }
 * ```
 */
@ExperimentalReimaginedApi
@Composable
fun NavHostVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val controller = rememberNavController<VisibilityUnit>(initialBackstack = emptyList())
    controller.setVisibility(visible)
    NavHost(controller.backstack) {
        content()
    }
}

/**
 * Controls the visibility of any NavHost that is placed inside [content] and animates
 * the appearance and disappearance of it. When [visible] parameter
 * is set to false, [content] will be removed from composition, all saved states and architecture
 * components (Lifecycle, ViewModelStore, SavedStateRegistry) of NavHost entries will be cleared.
 *
 * If you do not want to clear entries' saved states and architecture components, you may
 * hoist NavHostState and use simple AnimatedVisibility to control the visibility of NavHost:
 *
 * ```kotlin
 * val state = rememberNavHostState(backstack)
 * AnimatedVisibility(visible) {
 *      NavHost(state) {
 *          ...
 *      }
 * }
 * ```
 */
@ExperimentalReimaginedApi
@ExperimentalAnimationApi
@Composable
fun NavHostAnimatedVisibility(
    visible: Boolean,
    enter: EnterTransition = fadeIn(tween()),
    exit: ExitTransition = fadeOut(tween()),
    transitionQueueing: NavTransitionQueueing = NavTransitionQueueing.Interrupt,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val controller = rememberNavController<VisibilityUnit>(initialBackstack = emptyList())
    controller.setVisibility(visible)
    BaseNavHost(
        state = rememberScopingNavHostState(
            controller.backstack,
            EmptyScopeSpec
        ),
        transitionQueueing = transitionQueueing
    ) { targetSnapshot ->
        val transition = updateTransition(
            targetState = targetSnapshot,
            label = "NavHostAnimatedVisibility"
        )
        transition.AnimatedVisibility(
            visible = { it.items.isNotEmpty() },
            enter = enter,
            exit = exit
        ) {
            val lastSnapshotItem =
                transition.currentState.items.lastOrNull() ?: transition.targetState.items.last()
            key(lastSnapshotItem.hostEntry.id) {
                lastSnapshotItem.hostEntry.ComponentsProvider {
                    content()
                }
            }
        }
        return@BaseNavHost transition.currentState
    }
}