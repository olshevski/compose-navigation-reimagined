package dev.olshevski.navigation.reimagined.sample

import android.content.Intent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth

typealias MainActivityComposeRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

fun createMainActivityComposeRule(): MainActivityComposeRule =
    createAndroidIntentComposeRule {
        Intent(it, MainActivity::class.java).apply {
            putExtra(MainActivity.SkipSplash, true)
        }
    }

open class MainActivityScope(protected val composeRule: MainActivityComposeRule) {

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

    fun closeKeyboard() {
        Espresso.closeSoftKeyboard()
        composeRule.waitForIdle()
    }

}
