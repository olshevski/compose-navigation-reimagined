package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
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
class LifecycleTest(private val useAnimatedNavHost: Boolean) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "animated={0}")
        fun data() = listOf(false, true)
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    enum class Screen {
        A, B, C
    }

    private lateinit var navController: NavController<Screen>
    private lateinit var lifecycleChanges: MutableList<Pair<Screen, Lifecycle.Event>>

    @OptIn(ExperimentalAnimationApi::class)
    @Before
    fun before() {
        navController = navController(Screen.A)
        lifecycleChanges = mutableListOf<Pair<Screen, Lifecycle.Event>>()
        val observedEntries = mutableSetOf<LifecycleOwner>()

        composeRule.setContent {
            val content = @Composable { screen: Screen ->
                val lifecycleOwner = LocalLifecycleOwner.current
                assertThat(lifecycleOwner).isInstanceOf(NavHostEntry::class.java)
                DisposableEffect(Unit) {
                    if (!observedEntries.contains(lifecycleOwner)) {
                        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                            lifecycleChanges.add(screen to event)
                        })
                        observedEntries.add(lifecycleOwner)
                    }
                    onDispose {}
                }
            }
            if (useAnimatedNavHost) {
                AnimatedNavHost(navController) { content(it) }
            } else {
                NavHost(navController) { content(it) }
            }
        }
    }

    @Test
    fun singleEntryNavigateAndPop() {
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
        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_CREATE,
            Screen.A to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_RESUME
        ).inOrder()

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
    fun activityClose_singleEntry() {
        assertThat(lifecycleChanges).containsExactly(
            Screen.A to Lifecycle.Event.ON_CREATE,
            Screen.A to Lifecycle.Event.ON_START,
            Screen.A to Lifecycle.Event.ON_RESUME
        ).inOrder()

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
    fun activityClose_twoEntries() {
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

}