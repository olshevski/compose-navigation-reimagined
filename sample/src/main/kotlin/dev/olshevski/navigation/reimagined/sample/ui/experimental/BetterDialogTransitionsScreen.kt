package dev.olshevski.navigation.reimagined.sample.ui.experimental

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.AnimatedNavHostScope
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavHostVisibility
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.DialogLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout

private enum class DialogDestination {
    First, Second, Third
}

private val DialogTransitionSpec = NavTransitionSpec<DialogDestination> { action, _, _ ->
    val animationSpec = tween<Float>(durationMillis = 200)
    if (action == NavAction.Pop) {
        fadeIn(
            animationSpec = animationSpec
        ) + scaleIn(
            animationSpec = animationSpec,
            initialScale = 1.5f
        ) togetherWith fadeOut(
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
        ) togetherWith fadeOut(
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

    AnimatedDialogNavHost(
        backstack = navController.backstack,
        transitionSpec = DialogTransitionSpec,
        onDismissRequest = { navController.pop() }
    ) { destination ->
        when (destination) {
            DialogDestination.First -> FirstDialogLayout(
                onOpenSecondDialogButtonClick = {
                    navController.navigate(DialogDestination.Second)
                }
            )

            DialogDestination.Second -> SecondDialogLayout(
                onOpenThirdDialogButtonClick = {
                    navController.navigate(DialogDestination.Third)
                }
            )

            DialogDestination.Third -> ThirdDialogLayout()
        }
    }

    ContentLayout {
        CenteredText(text = "This demo shows how to add animated transitions to dialogs.")

        Button(onClick = { navController.navigate(DialogDestination.First) }) {
            Text("Open First dialog")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T> AnimatedDialogNavHost(
    backstack: NavBackstack<T>,
    @Suppress("SameParameterValue") transitionSpec: NavTransitionSpec<T>,
    onDismissRequest: () -> Unit,
    contentSelector: @Composable AnimatedNavHostScope<T>.(T) -> Unit
) {
    // NavHostVisibility properly clears internal NavHost state when it becomes invisible
    NavHostVisibility(visible = backstack.entries.isNotEmpty()) {
        // Everything is shown within the same Dialog, so it is not possible to use AlertDialog
        // here, unless you copy the internal AlertDialogContent from the androidx.compose.material
        // package and use it inside this Dialog.
        Dialog(
            onDismissRequest = onDismissRequest,
            // Here we set usePlatformDefaultWidth = false, so we are not limited to the area of
            // the dialog layout and could animate on the whole screen.
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // The downside of usePlatformDefaultWidth = false is that we need to handle outside
            // clicks ourselves.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = onDismissRequest,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
            )

            AnimatedNavHost(
                backstack = backstack,
                transitionSpec = transitionSpec
            ) { destination ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(32.dp)
                            .widthIn(max = 320.dp)
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
    modifier = Modifier.height(300.dp),
    title = "First dialog"
) {

    Button(onClick = onOpenSecondDialogButtonClick) {
        Text("Open Second dialog")
    }
}

@Composable
private fun SecondDialogLayout(
    onOpenThirdDialogButtonClick: () -> Unit
) = DialogLayout(
    title = "Second dialog"
) {
    Button(onClick = onOpenThirdDialogButtonClick) {
        Text("Open Third dialog")
    }
}

@Composable
private fun ThirdDialogLayout() = DialogLayout(
    title = "Second dialog"
) {
    Text("Hello!")
}