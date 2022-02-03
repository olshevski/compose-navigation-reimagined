package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.AnimatedNavHostTransitionSpec
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.ui.tabs.AnimatedNavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.NavHostScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.TabsInfoScreen
import dev.olshevski.navigation.reimagined.sample.ui.tabs.ViewModelScreen

private val TabsNavHostTransitionSpec =
    AnimatedNavHostTransitionSpec<TabsDestination> { _, from, to ->
        val direction = if (from < to) {
            AnimatedContentScope.SlideDirection.Start
        } else {
            AnimatedContentScope.SlideDirection.End
        }
        slideIntoContainer(direction) with slideOutOfContainer(direction)
    }

@Preview
@Composable
fun TabsScreenPreview() {
    TabsScreen()
}

@Composable
fun TabsScreen() {
    val navController = rememberNavController(
        startDestination = TabsDestination.values()[0],
    )

    NavBackHandler(navController)

    Column {
        Box(Modifier.weight(1f)) {
            AnimatedNavHost(
                controller = navController,
                transitionSpec = TabsNavHostTransitionSpec
            ) { destination ->
                ScreenLayout(title = destination.screenTitle) {
                    when (destination) {
                        TabsDestination.NavHost -> NavHostScreen()
                        TabsDestination.AnimatedNavHost -> AnimatedNavHostScreen()
                        TabsDestination.ViewModel -> ViewModelScreen()
                        TabsDestination.TabsInfo -> TabsInfoScreen()
                    }
                }
            }
        }

        val selectedTab = navController.backstack.entries.last().destination
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            selectedTabIndex = TabsDestination.values().indexOf(selectedTab)
        ) {
            TabsDestination.values().forEach { tab ->
                Tab(
                    selected = tab == selectedTab,
                    onClick = {
                        navController.navigateToTab(tab)
                    }
                ) {
                    Text(tab.tabTitle)
                }
            }
        }
    }

}

private val TabsDestination.screenTitle
    get() = when (this) {
        TabsDestination.NavHost -> "NavHost Demo"
        TabsDestination.AnimatedNavHost -> "AnimatedNavHost Demo"
        TabsDestination.ViewModel -> "ViewModel Demo"
        TabsDestination.TabsInfo -> "Tabs Demo"
    }

private val TabsDestination.tabTitle
    get() = when (this) {
        TabsDestination.NavHost -> "NavHost"
        TabsDestination.AnimatedNavHost -> "Animated"
        TabsDestination.ViewModel -> "ViewModel"
        TabsDestination.TabsInfo -> "Tabs"
    }

/**
 * Tab navigation pattern as seen in the Youtube app.
 *
 * The start screen may appear twice in the backstack - once as the first item and the second time
 * when it was reopened after clicking some other tab. Identity (saved state, view models) are the
 * same for both items in the backstack.
 *
 * All other tabs appear only once in the backstack.
 *
 * This serves as an example of creating custom backstack manipulation patterns.
 */
fun NavController<TabsDestination>.navigateToTab(tab: TabsDestination) {
    val lastTab = backstack.entries.last().destination
    if (lastTab != tab) {
        val tabIndex = backstack.entries.indexOfLast { it.destination == tab }
        val newEntries = backstack.entries.toMutableList()
        when {
            tabIndex < 0 -> {
                // entry not found, create a new one
                newEntries.add(navEntry(tab))
            }
            tabIndex == 0 -> {
                // Start destination may appear in the backstack twice. We add the same
                // entry instance to the end so it shares the same identity.
                newEntries.add(backstack.entries[0])
            }
            else -> {
                // move the entry to the top
                newEntries.removeAt(tabIndex)
                newEntries.add(backstack.entries[tabIndex])
            }
        }

        // If we moved all other entries to the top, the start entry may appears twice
        // at the start of the backstack. Leave only one copy of the entry.
        //
        // IMPORTANT: Always compare entries by their unique id. Even if you placed the same
        // instance yourself twice in the backstack, it may become different instances after
        // the process/activity recreation. The id is what guarantees the identity.
        if (newEntries[0].id == newEntries[1].id) {
            newEntries.removeFirst()
        }
        setNewBackstackEntries(
            entries = newEntries,
            action = NavAction.Navigate
        )
    }
}