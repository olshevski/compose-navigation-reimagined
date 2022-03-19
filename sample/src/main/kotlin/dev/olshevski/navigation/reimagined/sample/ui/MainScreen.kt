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
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.olshevski.navigation.reimagined.sample.MainActivity

private val MainNavHostTransitionSpec =
    AnimatedNavHostTransitionSpec<MainDestination> { _, _, _ ->
        val outDuration = 100
        fadeIn(
            animationSpec = tween(durationMillis = 200, delayMillis = outDuration)
        ) with fadeOut(
            animationSpec = tween(durationMillis = outDuration)
        ) + scaleOut(
            targetScale = 2f,
            animationSpec = tween(durationMillis = outDuration)
        )
    }

@Composable
fun MainScreen() {
    val activity = LocalContext.current as Activity
    val startDestination =
        if (activity.intent.getBooleanExtra(MainActivity.SkipSplash, false)) {
            MainDestination.BottomNavigation
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
                navController.replaceAll(MainDestination.BottomNavigation)
            })
            MainDestination.BottomNavigation -> BottomNavigationScreen()
        }
    }
}