package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Save
import dev.olshevski.navigation.reimagined.sample.R

enum class BottomNavigationDestination {
    // the first item is treated as the start destination for simplicity
    Home,
    SavedState,
    NestedNavigation,
}

val BottomNavigationDestination.tabTitleId
    get() = when (this) {
        BottomNavigationDestination.Home -> R.string.bottom_navigation__home_tab_title
        BottomNavigationDestination.SavedState -> R.string.bottom_navigation__saved_state_tab_title
        BottomNavigationDestination.NestedNavigation -> R.string.bottom_navigation__nested_navigation_tab_title
    }

val BottomNavigationDestination.tabIcon
    get() = when (this) {
        BottomNavigationDestination.Home -> Icons.Outlined.Home
        BottomNavigationDestination.SavedState -> Icons.Outlined.Save
        BottomNavigationDestination.NestedNavigation -> Icons.Outlined.AccountTree
    }
