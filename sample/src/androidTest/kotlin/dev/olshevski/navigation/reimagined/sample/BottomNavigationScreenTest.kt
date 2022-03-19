package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBackUnconditionally
import dev.olshevski.navigation.reimagined.sample.ui.BottomNavigationDestination
import dev.olshevski.navigation.reimagined.sample.ui.screenTitleId
import dev.olshevski.navigation.reimagined.sample.ui.tabTitleId
import org.junit.Rule
import org.junit.Test

open class BottomNavigationScreenScope(composeRule: MainActivityComposeRule) :
    MainActivityScope(composeRule) {

    fun performTabClick(bottomNavigationDestination: BottomNavigationDestination) {
        composeRule.onNodeWithText(getString(bottomNavigationDestination.tabTitleId))
            .performClick()
    }

    fun assertScreenIsDisplayed(bottomNavigationDestination: BottomNavigationDestination) {
        composeRule.onNodeWithText(getString(bottomNavigationDestination.screenTitleId))
            .assertIsDisplayed()
    }

}

private fun MainActivityComposeRule.bottomNavigationScreenScope(block: BottomNavigationScreenScope.() -> Unit) =
    BottomNavigationScreenScope(this).block()

class BottomNavigationScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Test
    fun homeScreenIsDisplayed() = composeRule.bottomNavigationScreenScope {
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun navHostScreenIsDisplayed() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        assertScreenIsDisplayed(BottomNavigationDestination.NavHost)
    }

    @Test
    fun animatedNavHostScreenIsDisplayed() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        assertScreenIsDisplayed(BottomNavigationDestination.AnimatedNavHost)
    }

    @Test
    fun viewModelScreenIsDisplayed() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        assertScreenIsDisplayed(BottomNavigationDestination.ViewModel)
    }

    @Test
    fun backstackHistory() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performTabClick(BottomNavigationDestination.ViewModel)
        performTabClick(BottomNavigationDestination.Home)

        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.ViewModel)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.AnimatedNavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.NavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
        pressBackUnconditionally()
        assertActivityClosed()
    }

    @Test
    fun backstackHistory_recreateActivity() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performTabClick(BottomNavigationDestination.ViewModel)
        performTabClick(BottomNavigationDestination.Home)

        recreateActivity()

        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.ViewModel)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.AnimatedNavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.NavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
        pressBackUnconditionally()
        assertActivityClosed()
    }

    @Test
    fun backstackHistory_nonRepeatingEntries() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performTabClick(BottomNavigationDestination.NavHost)
        performTabClick(BottomNavigationDestination.Home)
        performTabClick(BottomNavigationDestination.NavHost)
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performTabClick(BottomNavigationDestination.ViewModel)

        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.AnimatedNavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.NavHost)
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
        pressBackUnconditionally()
        assertActivityClosed()
    }

}