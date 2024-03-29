package dev.olshevski.navigation.testutils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Factory method to provide android specific implementation of createComposeRule, for a given
 * activity class type A that needs to be launched via an intent.
 */
fun <A : ComponentActivity> createAndroidIntentComposeRule(intentFactory: (context: Context) -> Intent): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val intent = intentFactory(context)
    return AndroidComposeTestRule(
        activityRule = ActivityScenarioRule(intent),
        activityProvider = { scenarioRule -> scenarioRule.getActivity() }
    )
}

private fun <A : ComponentActivity> ActivityScenarioRule<A>.getActivity(): A {
    var activity: A? = null
    scenario.onActivity { activity = it }
    if (activity == null) {
        throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
    }
    return activity!!
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.recreateActivity() {
    activityRule.scenario.recreate()
    waitForIdle()
}

/**
 * Additionally clears ViewModelStore when Activity is recreated. This emulates the case of
 * full Activity recreation when no non-configuration instances are restored.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.recreateActivityAndClearViewModels() {
    runOnUiThread {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                (owner as ComponentActivity).viewModelStore.clear()
            }
        })
    }
    recreateActivity()
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.pressBack() {
    Espresso.pressBack()
    waitForIdle()
}

inline fun <reified T : ViewModel> getExistingViewModel(viewModelStoreOwner: ViewModelStoreOwner) =
    ViewModelProvider(
        owner = viewModelStoreOwner,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                error("Requested ViewModel should already exist")
            }
        }
    )[T::class.java]

fun getString(@StringRes resId: Int, vararg formatArgs: Any) =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(resId, *formatArgs)

fun <A, B> cartesianProduct(
    arrayA: Array<A>,
    arrayB: Array<B>
) = arrayA.flatMap { a ->
    arrayB.map { b -> arrayOf(a, b) }
}

fun <A, B, C> cartesianProduct(
    arrayA: Array<A>,
    arrayB: Array<B>,
    arrayC: Array<C>
) = arrayA.flatMap { a ->
    arrayB.flatMap { b ->
        arrayC.map { c -> arrayOf(a, b, c) }
    }
}