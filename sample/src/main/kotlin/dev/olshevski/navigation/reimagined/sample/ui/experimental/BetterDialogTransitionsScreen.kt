package dev.olshevski.navigation.reimagined.sample.ui.experimental

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.rememberNavHostState
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.DialogLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout

private enum class DialogDestination {
    First, Second
}

private val DialogTransitionSpec = NavTransitionSpec<DialogDestination> { action, _, _ ->
    val animationSpec = tween<Float>(durationMillis = 200)
    if (action == NavAction.Pop) {
        fadeIn(
            animationSpec = animationSpec
        ) + scaleIn(
            animationSpec = animationSpec,
            initialScale = 1.5f
        ) with fadeOut(
            animationSpec = animationSpec
        ) + scaleOut(
            animationSpec = animationSpec,
            targetScale = 0.5f
        )

    } else {
        fadeIn(
            animationSpec = animationSpec
        ) + scaleIn(
            animationSpec = animationSpec,
            initialScale = 0.5f
        ) with fadeOut(
            animationSpec = animationSpec
        ) + scaleOut(
            animationSpec = animationSpec,
            targetScale = 1.5f
        )
    }
}

@Composable
fun BetterDialogTransitionsScreen() = ScreenLayout(title = "Better dialog transitions") {
    val navController = rememberNavController<DialogDestination>(
        initialBackstack = emptyList()
    )

    AnimatedDialogNavHost(navController, transitionSpec = DialogTransitionSpec) { destination ->
        when (destination) {
            DialogDestination.First -> FirstDialogLayout(
                onOpenSecondDialogButtonClick = {
                    navController.navigate(DialogDestination.Second)
                }
            )
            DialogDestination.Second -> SecondDialogLayout()
        }
    }

    ContentLayout {
        Button(onClick = { navController.navigate(DialogDestination.First) }) {
            Text("Open First dialog")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T> AnimatedDialogNavHost(
    controller: NavController<T>,
    @Suppress("SameParameterValue") transitionSpec: NavTransitionSpec<T>,
    contentSelector: @Composable (T) -> Unit
) {
    val navHostState = rememberNavHostState(controller.backstack)
    val onDismissRequest: () -> Unit = { controller.pop() }
    val showDialog by rememberUpdatedState(controller.backstack.entries.isNotEmpty())

    if (showDialog) {
        DisposableEffect(Unit) {
            onDispose {
                @Suppress("KotlinConstantConditions")
                if (!showDialog) {
                    navHostState.clear()
                }
            }
        }
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                propagateMinConstraints = true
            ) {
                Box(
                    modifier = Modifier.clickable(
                        onClick = onDismissRequest,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                )

                AnimatedNavHost(navHostState, transitionSpec) { destination ->
                    Box(
                        modifier = Modifier
                            .padding(32.dp)
                            .widthIn(max = 320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        contentSelector(destination)
                    }
                }
            }
        }
    }
}

@Composable
private fun FirstDialogLayout(
    onOpenSecondDialogButtonClick: () -> Unit
) = DialogLayout(
    title = "First dialog"
) {
    Button(onClick = onOpenSecondDialogButtonClick) {
        Text("Open Second dialog")
    }
}

@Composable
private fun SecondDialogLayout() = DialogLayout(
    title = "Second dialog"
) {
    Text("Hello!")
}