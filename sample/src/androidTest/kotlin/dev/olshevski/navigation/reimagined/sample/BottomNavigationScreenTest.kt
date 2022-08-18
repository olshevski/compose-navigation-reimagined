package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.reimagined.sample.ui.demo.BottomNavigationDestination
import dev.olshevski.navigation.reimagined.sample.ui.demo.tabTitleId
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BottomNavigationScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertBottomNavigationScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__demo_screen_title))
            .assertIsDisplayed()
    }

    fun performTabClick(bottomNavigationDestination: BottomNavigationDestination) {
        composeRule.onNodeWithText(getString(bottomNavigationDestination.tabTitleId))
            .performClick()
    }

    fun assertHomeScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__home_screen_title))
            .assertIsDisplayed()
    }

    fun assertSavedStateScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__saved_state_screen_title))
            .assertIsDisplayed()
    }

    fun assertNestedNavigationScreenAIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__nested_navigation_screen_a_title))
            .assertIsDisplayed()
    }

    fun assertNestedNavigationScreenBIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__nested_navigation_screen_b_title))
            .assertIsDisplayed()
    }

    fun performToScreenBButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_navigation__to_nested_navigation_screen_b_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.bottomNavigationScreenScope(block: BottomNavigationScreenScope.() -> Unit) =
    BottomNavigationScreenScope(this).block()

class BottomNavigationScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.bottomNavigationScreenScope {
        performBottomNavigationButtonClick()
        assertBottomNavigationScreenIsDisplayed()
        assertHomeScreenIsDisplayed()
    }

    private fun generalFlow(middleBlock: BottomNavigationScreenScope.() -> Unit) =
        composeRule.bottomNavigationScreenScope {
            performTabClick(BottomNavigationDestination.SavedState)
            performTabClick(BottomNavigationDestination.NestedNavigation)
            performToScreenBButtonClick()
            performTabClick(BottomNavigationDestination.Home)

            middleBlock()
            assertHomeScreenIsDisplayed()

            pressBack()
            assertNestedNavigationScreenBIsDisplayed()
            pressBack()
            assertNestedNavigationScreenAIsDisplayed()
            pressBack()
            assertSavedStateScreenIsDisplayed()
            pressBack()
            assertHomeScreenIsDisplayed()
            pressBack()
            assertDemoSelectionScreenIsDisplayed()
        }

    @Test
    fun generalFlow() = generalFlow(middleBlock = {})

    @Test
    fun generalFlow_recreateActivity() = generalFlow(middleBlock = {
        recreateActivity()
    })

    @Test
    fun generalFlow_recreateActivityAndViewModels() = generalFlow(middleBlock = {
        recreateActivityAndClearViewModels()
    })

    @Test
    fun nonRepeatingEntries() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.SavedState)
        performTabClick(BottomNavigationDestination.NestedNavigation)
        performTabClick(BottomNavigationDestination.Home)
        performTabClick(BottomNavigationDestination.SavedState)
        performTabClick(BottomNavigationDestination.NestedNavigation)

        pressBack()
        assertSavedStateScreenIsDisplayed()
        pressBack()
        assertHomeScreenIsDisplayed()
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun textInputIsClearedWhenEntryIsRemoved() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.SavedState)
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        pressBack()
        performTabClick(BottomNavigationDestination.SavedState)
        assertInputIsEmpty()
    }

    @Test
    fun textInputIsSavedWhenGoingBetweenTabs() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.SavedState)
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        performTabClick(BottomNavigationDestination.Home)
        performTabClick(BottomNavigationDestination.SavedState)
        assertInputHasText()
    }

    @Test
    fun nestedNavigationIsSavedWhenGoingBetweenTabs() = composeRule.bottomNavigationScreenScope {
        performTabClick(BottomNavigationDestination.NestedNavigation)
        performToScreenBButtonClick()
        assertNestedNavigationScreenBIsDisplayed()

        performTabClick(BottomNavigationDestination.Home)
        performTabClick(BottomNavigationDestination.NestedNavigation)
        assertNestedNavigationScreenBIsDisplayed()
    }

}