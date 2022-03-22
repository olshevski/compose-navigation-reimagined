package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class ViewModelsScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertViewModelScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.view_models__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertFirstScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.view_models__first_screen_title))
            .assertIsDisplayed()
    }

    fun assertSecondScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.view_models__second_screen_title))
            .assertIsDisplayed()
    }

    fun assertThirdScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.view_models__third_screen_title))
            .assertIsDisplayed()
    }

    fun performToSecondScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.view_models__to_second_screen_button))
            .performClick()
    }

    fun performToThirdScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.view_models__to_third_screen_button))
            .performClick()
    }

    fun assertPassedTextIsDisplayed() {
        composeRule.onNodeWithText(
            getString(R.string.view_models__text_from_previous_screen, SomeText)
        ).assertIsDisplayed()
    }

}

private fun MainActivityComposeRule.viewModelsScreenScope(block: ViewModelsScreenScope.() -> Unit) =
    ViewModelsScreenScope(this).block()

class ViewModelsScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.viewModelsScreenScope {
        performViewModelsButtonClick()
        assertViewModelScreenIsDisplayed()
        assertFirstScreenIsDisplayed()
    }

    @Test
    fun normalFlow() = composeRule.viewModelsScreenScope {
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
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun normalFlow_recreateActivity() = composeRule.viewModelsScreenScope {
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
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun textInputIsClearedWhenEntryIsRemoved() = composeRule.viewModelsScreenScope {
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        pressBack()
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
    }

}