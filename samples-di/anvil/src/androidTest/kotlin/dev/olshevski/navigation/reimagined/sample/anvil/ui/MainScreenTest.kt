package dev.olshevski.navigation.reimagined.sample.anvil.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.reimagined.sample.anvil.MainActivity
import dev.olshevski.navigation.reimagined.sample.anvil.R
import dev.olshevski.navigation.testutils.getString
import dev.olshevski.navigation.testutils.pressBack
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun generalFlow() {
        composeRule.onNodeWithText(getString(R.string.main_screen_title)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.first_screen_title)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.to_second_screen_button)).performClick()
        composeRule.onNodeWithText(getString(R.string.second_screen_title, DemoId))
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.to_third_screen_button)).performClick()
        composeRule.onNodeWithText(getString(R.string.third_screen_title, DemoText))
            .assertIsDisplayed()
        composeRule.pressBack()
        composeRule.onNodeWithText(getString(R.string.second_screen_title, DemoId))
            .assertIsDisplayed()
        composeRule.pressBack()
        composeRule.onNodeWithText(getString(R.string.first_screen_title)).assertIsDisplayed()
    }

}