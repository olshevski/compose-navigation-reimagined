package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout

private val SlideTransitionSpec = NavTransitionSpec<Int> { action, from, to ->
    val directionsCount = 4
    val direction = when (action) {
        NavAction.Pop -> when (from.mod(directionsCount)) {
            0 -> AnimatedContentTransitionScope.SlideDirection.End
            1 -> AnimatedContentTransitionScope.SlideDirection.Down
            2 -> AnimatedContentTransitionScope.SlideDirection.Start
            else -> AnimatedContentTransitionScope.SlideDirection.Up
        }

        else -> when (to.mod(directionsCount)) {
            // opposite directions
            0 -> AnimatedContentTransitionScope.SlideDirection.Start
            1 -> AnimatedContentTransitionScope.SlideDirection.Up
            2 -> AnimatedContentTransitionScope.SlideDirection.End
            else -> AnimatedContentTransitionScope.SlideDirection.Down
        }
    }
    slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
}

@Composable
fun AnimatedNavHostScreen() = ScreenLayout(
    title = stringResource(R.string.animated_nav_host__demo_screen_title)
) {
    val navController = rememberNavController(startDestination = 0)

    NavBackHandler(navController)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = SlideTransitionSpec
    ) { destination ->
        ContentLayout(
            title = stringResource(R.string.animated_nav_host__screen_title, destination)
        ) {

            CenteredText(
                text = """AnimatedNavHost switches between destinations with animations. 
                    You can select a unique animation for every transition.""".singleLine(),
            )

            Button(onClick = { navController.navigate(destination + 1) }) {
                Text(stringResource(R.string.animated_nav_host__open_next_screen_button))
            }
        }
    }
}