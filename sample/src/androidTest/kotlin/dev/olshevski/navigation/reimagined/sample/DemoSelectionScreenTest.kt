package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
import org.junit.Rule
import org.junit.Test

open class DemoSelectionScreenScope(composeRule: MainActivityComposeRule) :
    MainActivityScope(composeRule) {

    fun assertDemoSelectionScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__screen_title))
            .assertIsDisplayed()
    }

    fun performPassValuesButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__pass_values_button))
            .performClick()
    }

    fun performReturnResultsButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__return_results_button))
            .performClick()
    }

    fun performAnimatedNavHostButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__animated_nav_host_button))
            .performClick()
    }

    fun performDialogNavHostButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__dialog_nav_host_button))
            .performClick()
    }

    fun performBottomSheetNavHostButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__bottom_sheet_nav_host_button))
            .performClick()
    }

    fun performBottomNavigationButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__bottom_navigation_button))
            .performClick()
    }

    fun performViewModelsButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__view_models_button))
            .performClick()
    }

    fun performScopedViewModelsButtonClick() {
        composeRule.onNodeWithText(getString(R.string.demo_selection__scoped_view_models_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.demoSelectionScreenScope(block: DemoSelectionScreenScope.() -> Unit) =
    DemoSelectionScreenScope(this).block()

class DemoSelectionScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Test
    fun activityIsClosedOnBackButtonPress() = composeRule.demoSelectionScreenScope {
        assertDemoSelectionScreenIsDisplayed()
        pressBackUnconditionally()
        assertActivityClosed()
    }

}