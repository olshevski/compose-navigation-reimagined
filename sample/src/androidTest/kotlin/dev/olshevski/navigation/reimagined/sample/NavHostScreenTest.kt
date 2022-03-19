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

private class NavHostScreenScope(composeRule: MainActivityComposeRule) :
    BottomNavigationScreenScope(composeRule) {

    companion object {
        private const val SomeText = "some text"
    }

    fun assertFirstScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_first_screen))
            .assertIsDisplayed()
    }

    fun assertSecondScreenIsDisplayed(id: Int) {
        composeRule.onNodeWithText(getString(R.string.navhost_second_screen, id))
            .assertIsDisplayed()
    }

    fun assertThirdScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_third_screen))
            .assertIsDisplayed()
    }

    fun assertForthScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_forth_screen))
            .assertIsDisplayed()
    }

    fun assertFifthScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_fifth_screen))
            .assertIsDisplayed()
    }

    fun assertFirstDialogIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_first_dialog))
            .assertIsDisplayed()
    }

    fun assertSecondDialogIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.navhost_second_dialog))
            .assertIsDisplayed()
    }

    fun assertFirstDialogDoesNotExist() {
        composeRule.onNodeWithText(getString(R.string.navhost_first_dialog))
            .assertDoesNotExist()
    }

    fun assertSecondDialogDoesNotExist() {
        composeRule.onNodeWithText(getString(R.string.navhost_second_dialog))
            .assertDoesNotExist()
    }

    fun performToSecondScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_second_screen_button))
            .performClick()
    }

    fun performToSecondScreenPlusOneButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_second_screen_plus_one_button))
            .performClick()
    }

    fun performToThirdScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_third_screen_button))
            .performClick()
    }

    fun performToForthScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_forth_screen_button))
            .performClick()
    }

    fun performToFifthScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_fifth_screen_button))
            .performClick()
    }

    fun performToFirstDialogButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_first_dialog_button))
            .performClick()
    }

    fun performToSecondDialogButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_to_second_dialog_button))
            .performClick()
    }

    fun performReturnToForthScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_return_to_forth_screen_button))
            .performClick()
    }

    fun performBackToFirstScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_back_to_first_screen_button))
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

    fun assertResultFromFifthIsDisplayed() {
        composeRule.onNodeWithText(
            getString(R.string.navhost_result_from_fifth, SomeText)
        ).assertIsDisplayed()
    }

    fun assertResultFromFifthDoesNotExist() {
        composeRule.onNodeWithText(
            getString(R.string.navhost_result_from_fifth, SomeText)
        ).assertDoesNotExist()
    }

    fun performClearResultFromFifthButtonClick() {
        composeRule.onNodeWithText(getString(R.string.navhost_clear_result_from_fifth_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.navHostScreenScope(block: NavHostScreenScope.() -> Unit) =
    NavHostScreenScope(this).block()

class NavHostScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Test
    fun generalFlow() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        assertFirstScreenIsDisplayed()
        performToSecondScreenButtonClick()
        assertSecondScreenIsDisplayed(0)
        performToSecondScreenPlusOneButtonClick()
        assertSecondScreenIsDisplayed(1)
        performToThirdScreenButtonClick()
        assertThirdScreenIsDisplayed()
        performTextInput()
        performToForthScreenButtonClick()
        assertForthScreenIsDisplayed()
        performToFifthScreenButtonClick()
        assertFifthScreenIsDisplayed()

        pressBack()
        assertForthScreenIsDisplayed()
        pressBack()
        assertThirdScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertSecondScreenIsDisplayed(1)
        pressBack()
        assertSecondScreenIsDisplayed(0)
        pressBack()
        assertFirstScreenIsDisplayed()
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun generalFlow_recreateActivity() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        assertFirstScreenIsDisplayed()
        performToSecondScreenButtonClick()
        assertSecondScreenIsDisplayed(0)
        performToSecondScreenPlusOneButtonClick()
        assertSecondScreenIsDisplayed(1)
        performToThirdScreenButtonClick()
        assertThirdScreenIsDisplayed()
        performTextInput()
        performToForthScreenButtonClick()
        assertForthScreenIsDisplayed()
        performToFifthScreenButtonClick()
        assertFifthScreenIsDisplayed()

        recreateActivity()
        assertFifthScreenIsDisplayed()

        pressBack()
        assertForthScreenIsDisplayed()
        pressBack()
        assertThirdScreenIsDisplayed()
        assertInputHasText()
        pressBack()
        assertSecondScreenIsDisplayed(1)
        pressBack()
        assertSecondScreenIsDisplayed(0)
        pressBack()
        assertFirstScreenIsDisplayed()
        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun dialogs() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performToThirdScreenButtonClick()
        performToForthScreenButtonClick()

        performToFirstDialogButtonClick()
        assertFirstDialogIsDisplayed()
        performToSecondDialogButtonClick()
        assertFirstDialogDoesNotExist()
        assertSecondDialogIsDisplayed()

        pressBack()
        assertSecondDialogDoesNotExist()
        assertForthScreenIsDisplayed()
    }

    @Test
    fun dialogs_recreateActivity() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performToThirdScreenButtonClick()
        performToForthScreenButtonClick()

        performToFirstDialogButtonClick()
        assertFirstDialogIsDisplayed()
        performToSecondDialogButtonClick()
        assertFirstDialogDoesNotExist()
        assertSecondDialogIsDisplayed()

        recreateActivity()
        assertSecondDialogIsDisplayed()

        pressBack()
        assertSecondDialogDoesNotExist()
        assertForthScreenIsDisplayed()
    }

    @Test
    fun returnValue() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performToThirdScreenButtonClick()
        performToForthScreenButtonClick()
        assertResultFromFifthDoesNotExist()
        performToFifthScreenButtonClick()

        performTextInput()
        performReturnToForthScreenButtonClick()
        assertResultFromFifthIsDisplayed()
        performClearResultFromFifthButtonClick()
        assertResultFromFifthDoesNotExist()
    }

    @Test
    fun backToFirstScreen() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performToThirdScreenButtonClick()
        performToForthScreenButtonClick()
        performToFifthScreenButtonClick()

        performBackToFirstScreenButtonClick()
        assertFirstScreenIsDisplayed()

        pressBack()
        assertScreenIsDisplayed(BottomNavigationDestination.Home)
    }

    @Test
    fun stateIsSavedWhenReturnedToTab() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performTabClick(BottomNavigationDestination.Home)

        pressBack()
        assertSecondScreenIsDisplayed(0)
        pressBack()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun stateIsSavedWhenReturnedToTab_recreateActivity() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performTabClick(BottomNavigationDestination.Home)

        recreateActivity()

        pressBack()
        assertSecondScreenIsDisplayed(0)
        pressBack()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun stateIsClearedWhenDestinationIsReentered() = composeRule.navHostScreenScope {
        performTabClick(BottomNavigationDestination.NavHost)
        performToSecondScreenButtonClick()
        performToThirdScreenButtonClick()
        performTextInput()
        pressBack()
        performToThirdScreenButtonClick()
        assertInputIsEmpty()
    }

}