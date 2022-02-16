package dev.olshevski.navigation.reimagined.sample.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.moveToTop
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.ui.tabs.AnimatedNavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.HomeScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.NavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.ViewModelScreen

@Composable
fun BottomNavigationScreen() {
    val navController = rememberNavController(
        startDestination = BottomNavigationDestination.values()[0],
    )

    // custom back handler implementation
    BottomNavigationBackHandler(navController)

    Column {
        Box(Modifier.weight(1f)) {
            AnimatedNavHost(
                controller = navController
            ) { destination ->
                ScreenLayout(title = destination.screenTitle) {
                    when (destination) {
                        BottomNavigationDestination.Home -> HomeScreen()
                        BottomNavigationDestination.NavHost -> NavHostScreen()
                        BottomNavigationDestination.AnimatedNavHost -> AnimatedNavHostScreen()
                        BottomNavigationDestination.ViewModel -> ViewModelScreen()
                    }
                }
            }
        }

        val lastDestination = navController.backstack.entries.last().destination
        BottomNavigation {
            BottomNavigationDestination.values().forEach { destination ->
                BottomNavigationItem(
                    label = { Text(destination.tabTitle) },
                    icon = {
                        Icon(
                            imageVector = destination.tabIcon,
                            contentDescription = destination.tabTitle
                        )
                    },
                    selected = destination == lastDestination,
                    onClick = {
                        // keep only one instance of a destination in the backstack
                        if (!navController.moveToTop { it == destination }) {
                            // if there are no existing instance, add it
                            navController.navigate(destination)
                        }
                    }
                )
            }
        }
    }

}

private val BottomNavigationDestination.screenTitle
    get() = when (this) {
        BottomNavigationDestination.Home -> "BottomNavigation Demo"
        BottomNavigationDestination.NavHost -> "NavHost Demo"
        BottomNavigationDestination.AnimatedNavHost -> "AnimatedNavHost Demo"
        BottomNavigationDestination.ViewModel -> "ViewModel Demo"
    }

private val BottomNavigationDestination.tabTitle
    get() = when (this) {
        BottomNavigationDestination.Home -> "Home"
        BottomNavigationDestination.NavHost -> "NavHost"
        BottomNavigationDestination.AnimatedNavHost -> "Animations"
        BottomNavigationDestination.ViewModel -> "ViewModel"
    }

private val BottomNavigationDestination.tabIcon
    get() = when (this) {
        BottomNavigationDestination.Home -> Icons.Outlined.Home
        BottomNavigationDestination.NavHost -> Icons.Outlined.Explore
        BottomNavigationDestination.AnimatedNavHost -> Icons.Outlined.Animation
        BottomNavigationDestination.ViewModel -> Icons.Outlined.Domain
    }

@Composable
fun BottomNavigationBackHandler(
    navController: NavController<BottomNavigationDestination>
) {
    BackHandler(enabled = navController.backstack.entries.size > 1) {
        val lastEntry = navController.backstack.entries.last()
        if (lastEntry.destination == BottomNavigationDestination.values()[0]) {
            // The start destination should always be the last to pop. We move it to the start
            // to preserve its saved state and view models.
            navController.moveLastEntryToStart()
        } else {
            navController.pop()
        }
    }
}

private fun NavController<BottomNavigationDestination>.moveLastEntryToStart() {
    setNewBackstackEntries(
        entries = backstack.entries.toMutableList().also {
            val entry = it.removeLast()
            it.add(0, entry)
        },
        action = NavAction.Pop
    )
}