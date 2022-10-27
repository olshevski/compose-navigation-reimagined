package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
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

    fun performOpenNextScreenButtonClick() {
        composeRule.onNodeWithText(getString(R.string.animated_nav_host__open_next_screen_button))
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

    private fun generalFlow(middleBlock: AnimatedNavHostScreenScope.() -> Unit) =
        composeRule.animatedNavHostScreenScope {
            val count = 5
            repeat(count) {
                performOpenNextScreenButtonClick()
                assertScreenIsDisplayed(it + 1)
            }

            middleBlock()
            assertScreenIsDisplayed(count)

            repeat(count) {
                pressBack()
                assertScreenIsDisplayed(count - it - 1)
            }
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

}