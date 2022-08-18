package dev.olshevski.navigation.reimagined

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import org.checkerframework.checker.units.qual.A
import org.checkerframework.checker.units.qual.C

// TODO share this code in common module

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

/**
 * Additionally cleans ViewModelStore when Activity is recreated. This emulates the case of
 * full Activity recreation when no non-configuration instances are restored.
 */
fun <A : ComponentActivity> ActivityScenario<A>.recreateAndClearViewModels() {
    UiThreadStatement.runOnUiThread {
        onActivity {
            it.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    (owner as ComponentActivity).viewModelStore.clear()
                }
            })
        }
    }
    recreate()
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

fun <A, B> cartesianProduct(
    listA: List<A>,
    listB: List<B>
) = listA.flatMap { a ->
    listB.map { b -> arrayOf(a, b) }
}

fun <A, B, C> cartesianProduct(
    listA: List<A>,
    listB: List<B>,
    listC: List<C>
) = listA.flatMap { a ->
    listB.flatMap { b ->
        listC.map { c -> arrayOf(a, b, c) }
    }
}