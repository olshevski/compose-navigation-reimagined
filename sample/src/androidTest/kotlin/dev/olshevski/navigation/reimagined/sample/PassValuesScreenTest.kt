package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private open class PassValuesScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertPassValuesScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.pass_values__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertScreenAIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.pass_values__screen_a_title))
            .assertIsDisplayed()
    }

    fun assertScreenBIsDisplayed(id: Int) {
        composeRule.onNodeWithText(getString(R.string.pass_values__screen_b_title, id))
            .assertIsDisplayed()
    }

    fun assertScreenCIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.pass_values__screen_c_title))
            .assertIsDisplayed()
    }

    fun performOpenScreenBButtonClick() {
        composeRule.onNodeWithText(getString(R.string.pass_values__open_screen_b_button))
            .performClick()
    }

    fun performOpenScreenBPlusOneButtonClick() {
        composeRule.onNodeWithText(getString(R.string.pass_values__open_screen_b_plus_one_button))
            .performClick()
    }

    fun performReturnBackToScreenAButtonClick() {
        composeRule.onNodeWithText(getString(R.string.pass_values__back_to_screen_a_button))
            .performClick()
    }

    fun assertReturnBackToScreenAButtonIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.pass_values__back_to_screen_a_button))
            .assertIsDisplayed()
    }

    fun assertReturnBackToScreenAButtonDoesNotExist() {
        composeRule.onNodeWithText(getString(R.string.pass_values__back_to_screen_a_button))
            .assertDoesNotExist()
    }

    fun assertTextIsPassed() {
        composeRule.onNodeWithText(getString(R.string.pass_values__passed_text, SomeText))
            .assertIsDisplayed()
    }

    fun performOpenScreenCButtonClick() {
        composeRule.onNodeWithText(getString(R.string.pass_values__open_screen_c_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.passValuesScreenScope(block: PassValuesScreenScope.() -> Unit) =
    PassValuesScreenScope(this).block()

class PassValuesScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.passValuesScreenScope {
        performPassValuesButtonClick()
        assertPassValuesScreenIsDisplayed()
        assertScreenAIsDisplayed()
    }

    @Test
    fun screenB() = composeRule.passValuesScreenScope {
        performOpenScreenBButtonClick()
        assertScreenBIsDisplayed(0)
        assertReturnBackToScreenAButtonDoesNotExist()
        performOpenScreenBPlusOneButtonClick()
        assertScreenBIsDisplayed(1)
        assertReturnBackToScreenAButtonIsDisplayed()

        performReturnBackToScreenAButtonClick()
        assertScreenAIsDisplayed()
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun screenC() = composeRule.passValuesScreenScope {
        performTextInput()
        performOpenScreenCButtonClick()
        assertScreenCIsDisplayed()
        assertTextIsPassed()

        pressBack()
        assertScreenAIsDisplayed()
        assertInputHasText()
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun screenC_recreateActivity() = composeRule.passValuesScreenScope {
        performTextInput()
        performOpenScreenCButtonClick()
        assertScreenCIsDisplayed()
        assertTextIsPassed()

        recreateActivity()
        assertScreenCIsDisplayed()
        assertTextIsPassed()

        pressBack()
        assertScreenAIsDisplayed()
        assertInputHasText()
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

}