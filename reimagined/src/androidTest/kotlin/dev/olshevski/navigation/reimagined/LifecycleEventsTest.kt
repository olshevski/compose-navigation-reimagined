package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.truth.Truth.assertThat
import dev.olshevski.navigation.reimagined.param.NavHostParam
import dev.olshevski.navigation.reimagined.param.ParamNavHost
import dev.olshevski.navigation.testutils.ImmediateLaunchedEffect
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class LifecycleEventsTest(private val param: NavHostParam) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = NavHostParam.values().asList()
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private enum class Screen {
        A, B, C
    }

    private val navController = navController(Screen.A)
    private val lifecycleChanges = mutableListOf<Pair<Screen, Lifecycle.Event>>()

    @Before
    fun before() {
        composeRule.setContent {
            val state = rememberNavHostState(navController.backstack, EmptyScopeSpec)

            ImmediateLaunchedEffect(state) {
                val observedEntries = mutableSetOf<LifecycleOwner>()
                snapshotFlow { state.hostEntries }.collect { hostEntries ->
                    hostEntries.forEach {
                        if (!observedEntries.contains(it)) {
                            it.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                                lifecycleChanges.add(it.destination to event)
                            })
                            observedEntries.add(it)
                        }
                    }
                }
            }
            ParamNavHost(param, state) {}
        }
    }

    @Test
    fun navigateToEntryAndPop() {
        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_CREATE,
            Screen.A to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_RESUME
        ).inOrder()

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.A to Lifecycle.Event.ON_STOP,
            Screen.A to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun navigateBetweenTwoEntries() {
        lifecycleChanges.clear()
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_CREATE,
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_STOP,
            Screen.B to Lifecycle.Event.ON_RESUME
        ).inOrder()

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_PAUSE,
            Screen.A to Lifecycle.Event.ON_START,
            Screen.B to Lifecycle.Event.ON_STOP,
            Screen.A to Lifecycle.Event.ON_RESUME,
            Screen.B to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun navigateBetweenThreeEntries() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        navController.navigate(Screen.C)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.C to Lifecycle.Event.ON_CREATE,
            Screen.B to Lifecycle.Event.ON_PAUSE,
            Screen.C to Lifecycle.Event.ON_START,
            Screen.B to Lifecycle.Event.ON_STOP,
            Screen.C to Lifecycle.Event.ON_RESUME
        ).inOrder()

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.C to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_START,
            Screen.C to Lifecycle.Event.ON_STOP,
            Screen.B to Lifecycle.Event.ON_RESUME,
            Screen.C to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun navigateToTwoEntriesAndPop() {
        lifecycleChanges.clear()
        navController.navigate(listOf(Screen.B, Screen.C))
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_CREATE,
            Screen.C to Lifecycle.Event.ON_CREATE,
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.C to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_STOP,
            Screen.C to Lifecycle.Event.ON_RESUME
        ).inOrder()

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.C to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_START,
            Screen.C to Lifecycle.Event.ON_STOP,
            Screen.B to Lifecycle.Event.ON_RESUME,
            Screen.C to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun pauseActivity_singleEntry() {
        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_PAUSE,
        ).inOrder()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_RESUME,
        ).inOrder()
    }

    @Test
    fun pauseActivity_twoEntries() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_PAUSE,
        ).inOrder()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_RESUME,
        ).inOrder()
    }

    @Test
    fun stopActivity_singleEntry() {
        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.A to Lifecycle.Event.ON_STOP,
        ).inOrder()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_RESUME,
        ).inOrder()
    }

    @Test
    fun stopActivity_twoEntries() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_STOP,
        ).inOrder()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_START,
            Screen.B to Lifecycle.Event.ON_RESUME,
        ).inOrder()
    }

    @Test
    fun closeActivity_singleEntry() {
        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.A to Lifecycle.Event.ON_STOP,
            Screen.A to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun closeActivity_twoEntries() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.B to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_STOP,
            Screen.A to Lifecycle.Event.ON_DESTROY,
            Screen.B to Lifecycle.Event.ON_DESTROY,
        ).inOrder()
    }

    @Test
    fun removeBackstackEntry() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        navController.setNewBackstack(navController.backstack.entries.drop(1))
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_DESTROY
        ).inOrder()
    }

    @Test
    fun addBackstackEntry() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        navController.setNewBackstack(
            navController.backstack.entries.toMutableList().apply { add(0, navEntry(Screen.C)) }
        )
        composeRule.waitForIdle()

        assertThat(lifecycleChanges).containsExactly(
            Screen.C to Lifecycle.Event.ON_CREATE
        ).inOrder()
    }

}