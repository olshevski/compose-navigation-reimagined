package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class ReturnResultsScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertReturnResultsScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.return_results__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertFirstScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.return_results__first_screen_title))
            .assertIsDisplayed()
    }

    fun assertSecondScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.return_results__second_screen_title))
            .assertIsDisplayed()
    }

    fun performToSecondScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.return_results__to_second_screen_button))
            .performClick()
    }

    fun performReturnResultToFirstScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.return_results__return_result_to_first_screen_button))
            .performClick()
    }

    fun assertReturnedResultIsDisplayed() {
        composeRule.onNodeWithText(
            getString(R.string.return_results__result_from_second_screen, SomeText)
        ).assertIsDisplayed()
    }

    fun assertReturnedResultDoesNotExist() {
        composeRule.onNodeWithText(
            getString(R.string.return_results__result_from_second_screen, SomeText)
        ).assertDoesNotExist()
    }

    fun performClearResultButtonClick() {
        composeRule.onNodeWithText(getString(R.string.return_results__clear_result_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.returnResultsScreenScope(block: ReturnResultsScreenScope.() -> Unit) =
    ReturnResultsScreenScope(this).block()

class ReturnResultsScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.returnResultsScreenScope {
        performReturnResultsButtonClick()
        assertReturnResultsScreenIsDisplayed()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun generalFlow() = composeRule.returnResultsScreenScope {
        assertReturnedResultDoesNotExist()
        performToSecondScreenButtonClick()
        assertSecondScreenIsDisplayed()
        performTextInput()
        performReturnResultToFirstScreenButtonClick()
        assertFirstScreenIsDisplayed()
        assertReturnedResultIsDisplayed()
        performClearResultButtonClick()
        assertReturnedResultDoesNotExist()
    }

    @Test
    fun nothingReturnedWhenPressedBack() = composeRule.returnResultsScreenScope {
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        pressBack()
        assertReturnedResultDoesNotExist()
    }

    @Test
    fun textInputIsClearedWhenEntryIsRemoved() = composeRule.returnResultsScreenScope {
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        pressBack()
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
    }

}