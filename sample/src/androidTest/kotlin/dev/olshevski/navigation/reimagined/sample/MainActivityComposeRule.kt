package dev.olshevski.navigation.reimagined.sample

import android.content.Intent
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

typealias MainActivityComposeRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

fun createMainActivityComposeRule(): MainActivityComposeRule =
    createAndroidIntentComposeRule {
        Intent(it, MainActivity::class.java).apply {
            putExtra(MainActivity.SkipSplash, true)
        }
    }

open class MainActivityScope(protected val composeRule: MainActivityComposeRule) {

    companion object {
        const val SomeText = "some text"
    }

    fun recreateActivity() {
        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()
    }

    fun assertActivityClosed() {
        Truth.assertThat(composeRule.activityRule.scenario.state)
            .isEqualTo(Lifecycle.State.DESTROYED)
    }

    fun pressBack() {
        Espresso.pressBack()
        composeRule.waitForIdle()
    }

    fun pressBackUnconditionally() {
        Espresso.pressBackUnconditionally()
        composeRule.waitForIdle()
    }

    fun performTextInput() {
        composeRule.onNodeWithTag(TestInputTag).performTextInput(SomeText)
        Espresso.closeSoftKeyboard()
        composeRule.waitForIdle()
    }

    fun assertInputHasText() {
        composeRule.onNodeWithTag(TestInputTag).assertTextEquals(SomeText)
    }

    fun assertInputIsEmpty() {
        composeRule.onNodeWithTag(TestInputTag).assertTextEquals("")
    }

}
