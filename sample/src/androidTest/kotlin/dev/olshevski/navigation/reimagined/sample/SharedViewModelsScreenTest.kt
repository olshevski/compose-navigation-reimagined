package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class SharedViewModelsScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertSharedViewModelScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertFirstScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__first_screen_title))
            .assertIsDisplayed()
    }

    fun assertSecondScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__second_screen_title))
            .assertIsDisplayed()
    }

    fun assertThirdScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__third_screen_title))
            .assertIsDisplayed()
    }

    fun performToSecondScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__to_second_screen_button))
            .performClick()
    }

    fun performToThirdScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.shared_view_models__to_third_screen_button))
            .performClick()
    }

    fun assertPassedTextIsDisplayed() {
        composeRule.onNodeWithText(
            getString(R.string.shared_view_models__text_from_shared_view_model, SomeText)
        ).assertIsDisplayed()
    }

}

private fun MainActivityComposeRule.sharedViewModelsScreenScope(block: SharedViewModelsScreenScope.() -> Unit) =
    SharedViewModelsScreenScope(this).block()

class SharedViewModelsScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.sharedViewModelsScreenScope {
        performSharedViewModelsButtonClick()
        assertSharedViewModelScreenIsDisplayed()
        assertFirstScreenIsDisplayed()
    }

    private fun generalFlow(middleBlock: SharedViewModelsScreenScope.() -> Unit) =
        composeRule.sharedViewModelsScreenScope {
            performToSecondScreenButtonClick()
            assertSecondScreenIsDisplayed()
            performTextInput()
            performToThirdScreenButtonClick()
            assertThirdScreenIsDisplayed()
            assertPassedTextIsDisplayed()

            middleBlock()
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
    fun textInputIsClearedWhenEntryIsRemoved() = composeRule.sharedViewModelsScreenScope {
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
        performTextInput()
        assertInputHasText()

        pressBack()
        performToSecondScreenButtonClick()
        assertInputIsEmpty()
    }

}