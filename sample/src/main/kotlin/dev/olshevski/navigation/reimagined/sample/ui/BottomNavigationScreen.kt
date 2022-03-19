package dev.olshevski.navigation.reimagined.sample.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                ScreenLayout(title = stringResource(destination.screenTitleId)) {
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
                val tabTitle = stringResource(destination.tabTitleId)
                BottomNavigationItem(
                    label = { Text(tabTitle) },
                    icon = {
                        Icon(
                            imageVector = destination.tabIcon,
                            contentDescription = tabTitle
                        )
                    },
                    selected = destination == lastDestination,
                    onClick = {
                        // keep only one instance of a destination in the backstack
                        if (!navController.moveToTop { it == destination }) {
                            // if there is no existing instance, add it
                            navController.navigate(destination)
                        }
                    }
                )
            }
        }
    }

}

@Composable
private fun BottomNavigationBackHandler(
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
    setNewBackstack(
        entries = backstack.entries.toMutableList().also {
            val entry = it.removeLast()
            it.add(0, entry)
        },
        action = NavAction.Pop
    )
}