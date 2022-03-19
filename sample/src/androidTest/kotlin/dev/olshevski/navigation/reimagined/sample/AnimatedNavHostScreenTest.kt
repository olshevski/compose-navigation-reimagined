package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.reimagined.sample.ui.BottomNavigationDestination
import org.junit.Rule
import org.junit.Test

private open class AnimatedNavHostScreenScope(composeRule: MainActivityComposeRule) :
    BottomNavigationScreenScope(composeRule) {

    fun assertScreenIsDisplayed(destination: Int) {
        composeRule.onNodeWithText(getString(R.string.animatednavhost_screen_title, destination))
            .assertIsDisplayed()
    }

    fun performToNextScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.animatednavhost_to_next_screen_button))
            .performClick()
    }
}

private fun MainActivityComposeRule.animatedNavHostScreenScope(block: AnimatedNavHostScreenScope.() -> Unit) =
    AnimatedNavHostScreenScope(this).block()

class AnimatedNavHostScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Test
    fun generalFlow() = composeRule.animatedNavHostScreenScope {
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        val count = 5
        repeat(count) {
            assertScreenIsDisplayed(it)
            performToNextScreenButtonClick()
        }
        assertScreenIsDisplayed(count)

        repeat(count) {
            pressBack()
            assertScreenIsDisplayed(count - it - 1)
        }
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun generalFlow_recreateActivity() = composeRule.animatedNavHostScreenScope {
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        val count = 5
        repeat(count) {
            assertScreenIsDisplayed(it)
            performToNextScreenButtonClick()
        }
        assertScreenIsDisplayed(count)

        recreateActivity()
        assertScreenIsDisplayed(count)

        repeat(count) {
            pressBack()
            assertScreenIsDisplayed(count - it - 1)
        }
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun stateIsSavedWhenReturnedToTab() = composeRule.animatedNavHostScreenScope {
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performToNextScreenButtonClick()
        performTabClick(BottomNavigationDestination.Home)

        pressBack()
        assertScreenIsDisplayed(1)
        pressBack()
        assertScreenIsDisplayed(0)
    }

    @Test
    fun stateIsSavedWhenReturnedToTab_recreateActivity() = composeRule.animatedNavHostScreenScope {
        performTabClick(BottomNavigationDestination.AnimatedNavHost)
        performToNextScreenButtonClick()
        performTabClick(BottomNavigationDestination.Home)

        recreateActivity()

        pressBack()
        assertScreenIsDisplayed(1)
        pressBack()
        assertScreenIsDisplayed(0)
    }

}