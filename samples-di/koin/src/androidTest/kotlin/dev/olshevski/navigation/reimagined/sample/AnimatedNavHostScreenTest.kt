package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private open class AnimatedNavHostScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertAnimatedNavHostScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.animated_nav_host__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertScreenIsDisplayed(destination: Int) {
        composeRule.onNodeWithText(getString(R.string.animated_nav_host__screen_title, destination))
            .assertIsDisplayed()
    }

    fun performToNextScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.animated_nav_host__to_next_screen_button))
            .performClick()
    }
}

private fun MainActivityComposeRule.animatedNavHostScreenScope(block: AnimatedNavHostScreenScope.() -> Unit) =
    AnimatedNavHostScreenScope(this).block()

class AnimatedNavHostScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.animatedNavHostScreenScope {
        performAnimatedNavHostButtonClick()
        assertAnimatedNavHostScreenIsDisplayed()
        assertScreenIsDisplayed(0)
    }

    @Test
    fun normalFlow() = composeRule.animatedNavHostScreenScope {
        val count = 5
        repeat(count) {
            performToNextScreenButtonClick()
            assertScreenIsDisplayed(it + 1)
        }

        repeat(count) {
            pressBack()
            assertScreenIsDisplayed(count - it - 1)
        }
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

    @Test
    fun normalFlow_recreateActivity() = composeRule.animatedNavHostScreenScope {
        val count = 5
        repeat(count) {
            performToNextScreenButtonClick()
            assertScreenIsDisplayed(it + 1)
        }

        recreateActivity()
        assertScreenIsDisplayed(count)

        repeat(count) {
            pressBack()
            assertScreenIsDisplayed(count - it - 1)
        }
        pressBack()
        assertDemoSelectionScreenIsDisplayed()
    }

}