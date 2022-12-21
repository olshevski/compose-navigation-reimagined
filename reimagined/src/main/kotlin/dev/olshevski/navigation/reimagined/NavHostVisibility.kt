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

@ExperimentalAnimationApi
@Composable
fun NavHostAnimatedVisibility(
    visible: Boolean,
    enter: EnterTransition = fadeIn(tween()),
    exit: ExitTransition = fadeOut(tween()),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val controller = rememberNavController<VisibilityUnit>(initialBackstack = emptyList())
    controller.setVisibility(visible)
    @OptIn(ExperimentalReimaginedApi::class)
    BaseNavHost(controller.backstack, EmptyScopeSpec) { targetSnapshot ->
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