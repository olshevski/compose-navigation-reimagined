package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class LifecycleEventsTest(private val navHostParam: NavHostParam) {

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

    private lateinit var navController: NavController<Screen>
    private lateinit var lifecycleChanges: MutableList<Pair<Screen, Lifecycle.Event>>

    @OptIn(ExperimentalAnimationApi::class)
    @Before
    fun before() {
        navController = navController(Screen.A)
        lifecycleChanges = mutableListOf()
        composeRule.setContent {
            val state = rememberNavHostState(navController.backstack)
            val observedEntries = remember { mutableSetOf<LifecycleOwner>() }

            DisposableEffect(state.hostEntries) {
                state.hostEntries.forEach {
                    if (!observedEntries.contains(it)) {
                        it.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                            lifecycleChanges.add(it.destination to event)
                        })
                        observedEntries.add(it)
                    }
                }
                onDispose {}
            }

            when (navHostParam) {
                NavHostParam.NavHost -> NavHost(state) {}
                NavHostParam.AnimatedNavHost -> AnimatedNavHost(state) {}
            }
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
            Screen.A to Lifecycle.Event.ON_PAUSE,
            Screen.B to Lifecycle.Event.ON_CREATE,
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
            Screen.B to Lifecycle.Event.ON_DESTROY,
            Screen.A to Lifecycle.Event.ON_RESUME
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