package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class DialogNavHostScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertDialogNavHostDemoScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertFirstDialogIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__first_dialog_title))
            .assertIsDisplayed()
    }

    fun assertFirstDialogDoesNotExist() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__first_dialog_title))
            .assertDoesNotExist()
    }

    fun assertSecondDialogIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__second_dialog_title))
            .assertIsDisplayed()
    }

    fun assertSecondDialogDoesNotExist() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__second_dialog_title))
            .assertDoesNotExist()
    }

    fun performToFirstDialogButtonClick() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__to_first_dialog_button))
            .performClick()
    }

    fun performToSecondDialogButtonClick() {
        composeRule.onNodeWithText(getString(R.string.dialog_nav_host__to_second_dialog_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.dialogNavHostScreenScope(block: DialogNavHostScreenScope.() -> Unit) =
    DialogNavHostScreenScope(this).block()

class DialogNavHostScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.dialogNavHostScreenScope {
        performDialogNavHostButtonClick()
        assertDialogNavHostDemoScreenIsDisplayed()
    }

    private fun generalFlow(middleBlock: DialogNavHostScreenScope.() -> Unit) =
        composeRule.dialogNavHostScreenScope {
            performToFirstDialogButtonClick()
            assertFirstDialogIsDisplayed()
            performToSecondDialogButtonClick()
            assertFirstDialogDoesNotExist()
            assertSecondDialogIsDisplayed()

            middleBlock()
            assertFirstDialogDoesNotExist()
            assertSecondDialogIsDisplayed()

            pressBack()
            assertFirstDialogDoesNotExist()
            assertSecondDialogDoesNotExist()
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
        recreateActivityAndViewModels()
    })

}