package dev.olshevski.navigation.reimagined

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.google.common.truth.Truth.assertThat
import dev.olshevski.navigation.reimagined.param.ActivityRecreateParam
import dev.olshevski.navigation.reimagined.param.NavHostParam
import dev.olshevski.navigation.reimagined.param.ParamNavHost
import dev.olshevski.navigation.reimagined.param.ViewModelFactoryParam
import dev.olshevski.navigation.reimagined.param.paramSavedStateViewModel
import dev.olshevski.navigation.reimagined.param.recreate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SavedStateHandleTest(
    private val hostParam: NavHostParam,
    private val factoryParam: ViewModelFactoryParam,
    private val recreateParam: ActivityRecreateParam
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}, {1}, {2}")
        fun data() = cartesianProduct(
            NavHostParam.values().asList(),
            ViewModelFactoryParam.values().asList(),
            ActivityRecreateParam.values().asList()
        )
    }

    @get:Rule
    val composeRule = createAndroidIntentComposeRule<TestActivity> {
        Intent(it, TestActivity::class.java).apply {
            putExtra(TestActivity.Extra.NavHostParam, hostParam)
            putExtra(TestActivity.Extra.FactoryParam, factoryParam)
        }
    }

    enum class Screen {
        A, B, C
    }

    enum class SubScreen {
        X, Y
    }

    object Value {
        const val Value1 = "value1"
        const val Value2 = "value2"
        const val Value3 = "value3"
    }

    @OptIn(SavedStateHandleSaveableApi::class)
    class TestViewModel(
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        var state by savedStateHandle.saveable { mutableStateOf(Value.Value1) }
    }

    class TestActivity : ComponentActivity() {

        object Extra {
            const val NavHostParam = "NavHostParam"
            const val FactoryParam = "FactoryParam"
        }

        lateinit var screenController: NavController<Screen>
        internal lateinit var screenState: NavHostState<Screen>

        lateinit var subScreenController: NavController<SubScreen>
        internal lateinit var subScreenState: NavHostState<SubScreen>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val hostParam = intent.getSerializableExtra(Extra.NavHostParam) as NavHostParam
            val factoryParam =
                intent.getSerializableExtra(Extra.FactoryParam) as ViewModelFactoryParam
            setContent {
                screenController = rememberNavController(Screen.A)
                screenState = rememberNavHostState(screenController.backstack)
                ParamNavHost(hostParam, screenState) { screen ->
                    paramSavedStateViewModel(
                        viewModelStoreOwner = hostEntries.find { it.destination == Screen.A }!!,
                        factoryParam = factoryParam
                    ) { savedStateHandle ->
                        TestViewModel(savedStateHandle)
                    }

                    if (screen == Screen.C) {
                        SubScreenHost(hostParam, factoryParam)
                    }
                }
            }
        }

        @Suppress("TestFunctionName")
        @Composable
        private fun SubScreenHost(hostParam: NavHostParam, factoryParam: ViewModelFactoryParam) {
            subScreenController = rememberNavController(SubScreen.X)
            subScreenState = rememberNavHostState(subScreenController.backstack)
            ParamNavHost(hostParam, subScreenState) {
                paramSavedStateViewModel(
                    viewModelStoreOwner = hostEntries.find { it.destination == SubScreen.X }!!,
                    factoryParam = factoryParam
                ) { savedStateHandle ->
                    TestViewModel(savedStateHandle)
                }
            }
        }

    }

    // NOTE: in all tests we need to do two cycles as the SavedStateHandle may be not reconnected
    // properly on the second cycle

    private fun testFirstEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        assertThat(viewModel1.state).isEqualTo(Value.Value1)

        viewModel1.state = Value.Value2
        composeRule.activityRule.scenario.recreate(recreateParam)

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        assertThat(viewModel2.state).isEqualTo(Value.Value2)

        viewModel2.state = Value.Value3
        composeRule.activityRule.scenario.recreate(recreateParam)

        val viewModel3 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        assertThat(viewModel3.state).isEqualTo(Value.Value3)
    }

    @Test
    fun currentEntry() {
        testFirstEntry()
    }

    @Test
    fun backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        testFirstEntry()
    }

    private fun testFirstNestedEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        assertThat(viewModel1.state).isEqualTo(Value.Value1)

        viewModel1.state = Value.Value2
        composeRule.activityRule.scenario.recreate(recreateParam)

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        assertThat(viewModel2.state).isEqualTo(Value.Value2)

        viewModel2.state = Value.Value3
        composeRule.activityRule.scenario.recreate(recreateParam)

        val viewModel3 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        assertThat(viewModel3.state).isEqualTo(Value.Value3)
    }

    @Test
    fun currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.C)
        composeRule.waitForIdle()

        testFirstNestedEntry()
    }

    @Test
    fun backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.C)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        testFirstNestedEntry()
    }

}