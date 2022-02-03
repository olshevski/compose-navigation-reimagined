package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.AnimatedNavHostTransitionSpec
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll

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
    val navController = rememberNavController(
        startDestination = MainDestination.Splash
    )

    NavBackHandler(navController)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = MainNavHostTransitionSpec
    ) { destination ->
        when (destination) {
            MainDestination.Splash -> SplashScreen(onFinishedShowing = {
                navController.replaceAll(MainDestination.Tabs)
            })
            MainDestination.Tabs -> TabsScreen()
        }
    }
}