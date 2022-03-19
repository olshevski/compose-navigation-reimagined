package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import dev.olshevski.navigation.reimagined.sample.R

enum class BottomNavigationDestination {
    // the first item is treated as the start destination for simplicity
    Home,
    NavHost,
    AnimatedNavHost,
    ViewModel
}

val BottomNavigationDestination.screenTitleId
    get() = when (this) {
        BottomNavigationDestination.Home -> R.string.bottomnavigation_home_screen_title
        BottomNavigationDestination.NavHost -> R.string.bottomnavigation_navhost_screen_title
        BottomNavigationDestination.AnimatedNavHost -> R.string.bottomnavigation_animatednavhost_screen_title
        BottomNavigationDestination.ViewModel -> R.string.bottomnavigation_viewmodel_screen_title
    }

val BottomNavigationDestination.tabTitleId
    get() = when (this) {
        BottomNavigationDestination.Home -> R.string.bottomnavigation_home_tab
        BottomNavigationDestination.NavHost -> R.string.bottomnavigation_navhost_tab
        BottomNavigationDestination.AnimatedNavHost -> R.string.bottomnavigation_animatednavhost_tab
        BottomNavigationDestination.ViewModel -> R.string.bottomnavigation_viewmodel_tab
    }

val BottomNavigationDestination.tabIcon
    get() = when (this) {
        BottomNavigationDestination.Home -> Icons.Outlined.Home
        BottomNavigationDestination.NavHost -> Icons.Outlined.Explore
        BottomNavigationDestination.AnimatedNavHost -> Icons.Outlined.Animation
        BottomNavigationDestination.ViewModel -> Icons.Outlined.Domain
    }
