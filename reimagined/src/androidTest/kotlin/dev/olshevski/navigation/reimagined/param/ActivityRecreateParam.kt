package dev.olshevski.navigation.reimagined.param

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.olshevski.navigation.testutils.recreateActivity
import dev.olshevski.navigation.testutils.recreateActivityAndClearViewModels

enum class ActivityRecreateParam {
    RestoreViewModels,
    ClearViewModels
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.recreateActivity(
    param: ActivityRecreateParam
) {
    when (param) {
        ActivityRecreateParam.RestoreViewModels -> recreateActivity()
        ActivityRecreateParam.ClearViewModels -> recreateActivityAndClearViewModels()
    }
}