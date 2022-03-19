package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.olshevski.navigation.reimagined.sample.ui.BottomNavigationDestination
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag
import org.junit.Rule
import org.junit.Test

private class ViewModelScreenScope(composeRule: MainActivityComposeRule) :
    BottomNavigationScreenScope(composeRule) {

    companion object {
        private const val SomeText = "some text"
    }

    fun assertFirstScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.viewmodel_first_screen_title))
            .assertIsDisplayed()
    }

    fun assertSecondScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.viewmodel_second_screen_title))
            .assertIsDisplayed()
    }

    fun assertThirdScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.viewmodel_third_screen_title))
            .assertIsDisplayed()
    }

    fun performToSecondScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.viewmodel_to_second_screen_button))
            .performClick()
    }

    fun performToThirdScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.viewmodel_to_third_screen_button))
            .performClick()
    }

    fun performTextInput() {
        composeRule.onNodeWithTag(TestInputTag).performTextInput(SomeText)
        closeKeyboard()
    }

    fun assertInputHasText() {
        composeRule.onNodeWithTag(TestInputTag).assertTextEquals(SomeText)
    }

    fun assertInputIsEmpty() {
        composeRule.onNodeWithTag(TestInputTag).assertTextEquals("")
    }

    fun assertPassedTextIsDisplayed() {
        composeRule.onNodeWithText(
            getString(R.string.viewmodel_text_from_previous_screen, SomeText)
        ).assertIsDisplayed()
    }

}

private fun MainActivityComposeRule.viewModelScreenScope(block: ViewModelScreenScope.() -> Unit) =
    ViewModelScreenScope(this).block()

class ViewModelScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Test
    fun generalFlow() = composeRule.viewModelScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        assertFirstScreenIsDisplayed()
        performToSecondScreenButtonClick()
        assertSecondScreenIsDisplayed()
        performTextInput()
        performToThirdScreenButtonClick()
        assertThirdScreenIsDisplayed()
        assertPassedTextIsDisplayed()

        pressBack()
        assertSecondScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertFirstScreenIsDisplayed()
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun generalFlow_recreateActivity() = composeRule.viewModelScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        assertFirstScreenIsDisplayed()
        performToSecondScreenButtonClick()
        assertSecondScreenIsDisplayed()
        performTextInput()
        performToThirdScreenButtonClick()
        assertThirdScreenIsDisplayed()
        assertPassedTextIsDisplayed()

        recreateActivity()
        assertThirdScreenIsDisplayed()
        assertPassedTextIsDisplayed()

        pressBack()
        assertSecondScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertFirstScreenIsDisplayed()
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun stateIsSavedWhenReturnedToTab() = composeRule.viewModelScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        performToSecondScreenButtonClick()
        performTextInput()
        performTabClick(BottomNavigationDestination.Home)

        pressBack()
        assertSecondScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun stateIsSavedWhenReturnedToTab_recreateActivity() = composeRule.viewModelScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        performToSecondScreenButtonClick()
        performTextInput()
        performTabClick(BottomNavigationDestination.Home)

        recreateActivity()

        pressBack()
        assertSecondScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun stateIsClearedWhenDestinationIsReentered() = composeRule.viewModelScreenScope {
        performTabClick(BottomNavigationDestination.ViewModel)
        performToSecondScreenButtonClick()
        performTextInput()
        pressBack()
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
    }

}