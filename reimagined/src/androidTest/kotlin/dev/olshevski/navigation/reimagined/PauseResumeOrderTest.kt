package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.compose.runtime.DisposableEffect
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
class PauseResumeOrderTest(private val param: NavHostParam) {

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

    private sealed class EventType {
        data class Lifecycle(val event: androidx.lifecycle.Lifecycle.Event) : EventType()
        sealed class DisposableEffect : EventType() {
            object OnCreate : DisposableEffect()
            object OnDispose : DisposableEffect()
        }
    }

    private val navController = navController(Screen.A)
    private val lifecycleChanges = mutableListOf<Pair<Screen, EventType>>()

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
                                lifecycleChanges.add(it.destination to EventType.Lifecycle(event))
                            })
                            observedEntries.add(it)
                        }
                    }
                }
            }

            ParamNavHost(param, state) { screen ->
                DisposableEffect(Unit) {
                    lifecycleChanges.add(screen to EventType.DisposableEffect.OnCreate)
                    onDispose {
                        lifecycleChanges.add(screen to EventType.DisposableEffect.OnDispose)
                    }
                }
            }
        }
    }

    @Test
    fun navigateToEntryAndPop() {
        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.A to EventType.DisposableEffect.OnCreate
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.A to EventType.Lifecycle(Lifecycle.Event.ON_RESUME)
            )
        )

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.A to EventType.Lifecycle(Lifecycle.Event.ON_PAUSE)
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.A to EventType.DisposableEffect.OnDispose
            )
        )
    }

    @Test
    fun navigateBetweenTwoEntries() {
        lifecycleChanges.clear()
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.A to EventType.Lifecycle(Lifecycle.Event.ON_PAUSE)
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.A to EventType.DisposableEffect.OnDispose
            )
        )

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.B to EventType.DisposableEffect.OnCreate
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.B to EventType.Lifecycle(Lifecycle.Event.ON_RESUME)
            )
        )

        lifecycleChanges.clear()
        navController.pop()
        composeRule.waitForIdle()

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.B to EventType.Lifecycle(Lifecycle.Event.ON_PAUSE)
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.B to EventType.DisposableEffect.OnDispose
            )
        )

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.A to EventType.DisposableEffect.OnCreate
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.A to EventType.Lifecycle(Lifecycle.Event.ON_RESUME)
            )
        )
    }

    @Test
    fun closeActivity_singleEntry() {
        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        composeRule.waitForIdle()

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.A to EventType.Lifecycle(Lifecycle.Event.ON_PAUSE)
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.A to EventType.DisposableEffect.OnDispose
            )
        )
    }

    @Test
    fun closeActivity_twoEntries() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        composeRule.waitForIdle()

        assertThat(
            lifecycleChanges.lastIndexOf(
                Screen.B to EventType.Lifecycle(Lifecycle.Event.ON_PAUSE)
            )
        ).isLessThan(
            lifecycleChanges.indexOf(
                Screen.B to EventType.DisposableEffect.OnDispose
            )
        )
    }

    @Test
    fun removeBackstackEntry() {
        navController.navigate(Screen.B)
        composeRule.waitForIdle()

        lifecycleChanges.clear()
        navController.setNewBackstack(navController.backstack.entries.drop(1))
        composeRule.waitForIdle()

        assertThat(lifecycleChanges.any { it.first == Screen.B }).isFalse()
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

        assertThat(lifecycleChanges.any { it.first == Screen.B }).isFalse()
    }

}