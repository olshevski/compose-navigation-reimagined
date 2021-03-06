package dev.olshevski.navigation.reimagined.sample.ui

import android.app.Activity
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.AnimatedNavHostTransitionSpec
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.replaceAll
import dev.olshevski.navigation.reimagined.sample.MainActivity
import dev.olshevski.navigation.reimagined.sample.ui.demo.AnimatedNavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.BottomNavigationScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.DeeplinksDestination
import dev.olshevski.navigation.reimagined.sample.ui.demo.DeeplinksScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.DialogNavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.PassValuesScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.ReturnResultsScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.ViewModelsScreen

private val MainNavHostTransitionSpec =
    AnimatedNavHostTransitionSpec<MainDestination> { _, from, _ ->
        if (from == MainDestination.Splash) {
            val outDuration = 100
            fadeIn(
                animationSpec = tween(durationMillis = 200, delayMillis = outDuration)
            ) with fadeOut(
                animationSpec = tween(durationMillis = outDuration)
            ) + scaleOut(
                targetScale = 2f,
                animationSpec = tween(durationMillis = outDuration)
            )
        } else {
            fadeIn(tween()) with fadeOut(tween())
        }
    }

@Composable
fun MainScreen() {
    val activity = LocalContext.current as Activity
    val navController = rememberSaveable {
        val uri = activity.intent.data
        val initialBackstack = when {
            uri != null && uri.containsSupportedDeeplink() -> {
                uri.constructBackstack()
            }
            activity.intent.getBooleanExtra(MainActivity.SkipSplash, false) -> {
                listOf(MainDestination.DemoSelection)
            }
            else -> {
                listOf(MainDestination.Splash)
            }
        }
        navController(initialBackstack)
    }

    NavBackHandler(navController)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = MainNavHostTransitionSpec
    ) { destination ->
        when (destination) {
            MainDestination.Splash -> SplashScreen(onFinishedShowing = {
                navController.replaceAll(MainDestination.DemoSelection)
            })
            MainDestination.DemoSelection -> DemoSelectionScreen(
                onPassValuesButtonClick = {
                    navController.navigate(MainDestination.PassValues)
                },
                onReturnResultsButtonClick = {
                    navController.navigate(MainDestination.ReturnResults)
                },
                onAnimatedNavHostButtonClick = {
                    navController.navigate(MainDestination.AnimatedNavHost)
                },
                onDialogNavHostButtonClick = {
                    navController.navigate(MainDestination.DialogNavHost)
                },
                onViewModelsButtonClick = {
                    navController.navigate(MainDestination.ViewModels)
                },
                onBottomNavigationButtonClick = {
                    navController.navigate(MainDestination.BottomNavigation)
                },
                onDeeplinksButtonClick = {
                    navController.navigate(MainDestination.Deeplinks())
                }
            )
            MainDestination.PassValues -> PassValuesScreen()
            MainDestination.ReturnResults -> ReturnResultsScreen()
            MainDestination.AnimatedNavHost -> AnimatedNavHostScreen()
            MainDestination.DialogNavHost -> DialogNavHostScreen()
            MainDestination.ViewModels -> ViewModelsScreen()
            MainDestination.BottomNavigation -> BottomNavigationScreen()
            is MainDestination.Deeplinks -> DeeplinksScreen(destination.initialBackstack)
        }
    }
}

private fun Uri.containsSupportedDeeplink() =
    this.host.equals("olshevski.dev") && this.pathSegments.first() == "deeplinksdemo"

private fun Uri.constructBackstack(): List<MainDestination> {
    val deeplinksDestination = when {
        this.pathSegments.getOrNull(1) == "second" -> {
            MainDestination.Deeplinks(
                listOf(
                    DeeplinksDestination.First,
                    DeeplinksDestination.Second
                )
            )
        }
        this.pathSegments.getOrNull(1) == "third" -> {
            MainDestination.Deeplinks(
                listOf(
                    DeeplinksDestination.First,
                    DeeplinksDestination.Second,
                    DeeplinksDestination.Third(
                        this.pathSegments.getOrNull(2) ?: error("Third path must contain parameter")
                    )
                )
            )
        }
        else -> MainDestination.Deeplinks()
    }
    return listOf(MainDestination.DemoSelection, deeplinksDestination)
}