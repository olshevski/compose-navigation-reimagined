package dev.olshevski.navigation.reimagined.sample.ui

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.AnimatedNavHostTransitionSpec
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.olshevski.navigation.reimagined.sample.MainActivity
import dev.olshevski.navigation.reimagined.sample.ui.demo.AnimatedNavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.demo.BottomNavigationScreen
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
    val startDestination =
        if (activity.intent.getBooleanExtra(MainActivity.SkipSplash, false)) {
            MainDestination.DemoSelection
        } else {
            MainDestination.Splash
        }
    val navController = rememberNavController(startDestination)

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
                }
            )
            MainDestination.PassValues -> PassValuesScreen()
            MainDestination.ReturnResults -> ReturnResultsScreen()
            MainDestination.AnimatedNavHost -> AnimatedNavHostScreen()
            MainDestination.DialogNavHost -> DialogNavHostScreen()
            MainDestination.ViewModels -> ViewModelsScreen()
            MainDestination.BottomNavigation -> BottomNavigationScreen()
        }
    }
}