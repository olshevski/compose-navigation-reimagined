package dev.olshevski.navigation.reimagined.param

import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import dev.olshevski.navigation.reimagined.recreateAndClearViewModels

enum class ActivityRecreateParam {
    RestoreViewModels,
    ClearViewModels
}

fun <A : ComponentActivity> ActivityScenario<A>.recreate(param: ActivityRecreateParam) {
    when (param) {
        ActivityRecreateParam.RestoreViewModels -> recreate()
        ActivityRecreateParam.ClearViewModels -> recreateAndClearViewModels()
    }
}