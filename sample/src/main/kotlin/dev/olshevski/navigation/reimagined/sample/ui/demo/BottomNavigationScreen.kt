package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.moveToTop
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

enum class BottomDestination {
    // the first item is treated as the start destination for simplicity
    Home,
    SavedState,
    NestedNavigation,
}

val BottomDestination.title
    get() = when (this) {
        BottomDestination.Home -> R.string.bottom_navigation__home_tab_title
        BottomDestination.SavedState -> R.string.bottom_navigation__saved_state_tab_title
        BottomDestination.NestedNavigation -> R.string.bottom_navigation__nested_navigation_tab_title
    }

private val BottomDestination.icon
    get() = when (this) {
        BottomDestination.Home -> Icons.Outlined.Home
        BottomDestination.SavedState -> Icons.Outlined.Save
        BottomDestination.NestedNavigation -> Icons.Outlined.AccountTree
    }

@Composable
fun BottomNavigationScreen() = ScreenLayout(
    title = stringResource(R.string.bottom_navigation__demo_screen_title)
) {
    val navController = rememberNavController(
        startDestination = BottomDestination.values()[0],
    )

    // custom back handler implementation
    BottomNavigationBackHandler(navController)

    Column {
        Box(Modifier.weight(1f)) {
            AnimatedNavHost(
                controller = navController
            ) { destination ->
                when (destination) {
                    BottomDestination.Home -> HomeScreen()
                    BottomDestination.SavedState -> SavedStateScreen()
                    BottomDestination.NestedNavigation -> NestedNavigationScreen()
                }
            }
        }

        val lastDestination = navController.backstack.entries.last().destination
        BottomNavigation {
            BottomDestination.values().forEach { destination ->
                val title = stringResource(destination.title)
                BottomNavigationItem(
                    label = { Text(title) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = title
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
    navController: NavController<BottomDestination>
) {
    BackHandler(enabled = navController.backstack.entries.size > 1) {
        val lastEntry = navController.backstack.entries.last()
        if (lastEntry.destination == BottomDestination.values()[0]) {
            // The start destination should always be the last to pop. We move it to the start
            // to preserve its saved state and view models.
            navController.moveLastEntryToStart()
        } else {
            navController.pop()
        }
    }
}

private fun NavController<BottomDestination>.moveLastEntryToStart() {
    setNewBackstack(
        entries = backstack.entries.toMutableList().also {
            val entry = it.removeLast()
            it.add(0, entry)
        },
        action = NavAction.Pop
    )
}

@Composable
private fun HomeScreen() = ContentLayout(
    title = stringResource(R.string.bottom_navigation__home_screen_title)
) {
    CenteredText(
        text = """This is a simple implementation of BottomNavigation with same backstack logic
            as in the YouTube app.""".singleLine(),
    )

    CenteredText(
        text = """Here every destination appears in the backstack only once. Also, the home
            destination is always the last one to be closed by the back button.""".singleLine(),
    )
}

@Composable
private fun SavedStateScreen() = ContentLayout(
    title = stringResource(R.string.bottom_navigation__saved_state_screen_title)
) {

    CenteredText(
        text = """Every destination preserves its saved state until explicitly popped off the
            backstack.""".singleLine(),
    )

    CenteredText(
        text = """You may enter some text in the text field below and see how it behaves when
            switching bottom navigation destinations and pressing back button.""".singleLine(),
    )

    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { text = it }
    )

}

private enum class NestedDestination { A, B }

@Composable
private fun NestedNavigationScreen() {
    val navController = rememberNavController(startDestination = NestedDestination.A)

    NavBackHandler(navController)

    NavHost(navController) { nestedDestination ->
        when (nestedDestination) {
            NestedDestination.A -> ContentLayout(
                title = stringResource(R.string.bottom_navigation__nested_navigation_screen_a_title)
            ) {
                CenteredText(
                    text = """Every screen may have a nested navigation with its own back handling.
                        The back handling of a nested navigation always takes precedence over
                        the parent back handling.""".singleLine(),
                )

                Button(
                    onClick = { navController.navigate(NestedDestination.B) }
                ) {
                    Text(stringResource(R.string.bottom_navigation__open_nested_navigation_screen_b_button))
                }
            }

            NestedDestination.B -> ContentLayout(
                title = stringResource(R.string.bottom_navigation__nested_navigation_screen_b_title)
            ) {
                CenteredText(
                    text = """Try pressing back button and switching between bottom navigation
                        destinations""".singleLine(),
                )
            }
        }
    }

}
