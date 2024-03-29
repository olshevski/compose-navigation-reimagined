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
import dev.olshevski.navigation.reimagined.param.NavHostParam
import dev.olshevski.navigation.reimagined.param.ParamNavHost
import dev.olshevski.navigation.reimagined.param.ViewModelFactoryParam
import dev.olshevski.navigation.reimagined.param.paramViewModel
import dev.olshevski.navigation.testutils.cartesianProduct
import dev.olshevski.navigation.testutils.createAndroidIntentComposeRule
import dev.olshevski.navigation.testutils.getExistingViewModel
import dev.olshevski.navigation.testutils.recreateActivity
import dev.olshevski.navigation.testutils.recreateActivityAndClearViewModels
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SavedStateHandleTest(
    private val hostParam: NavHostParam,
    private val factoryParam: ViewModelFactoryParam,
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}, {1}")
        fun data() = cartesianProduct(
            NavHostParam.values(),
            ViewModelFactoryParam.values()
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
        A, B, WithNestedEntries
    }

    enum class SubScreen {
        X, Y
    }

    object Value {
        const val Value1 = "value1"
        const val Value2 = "value2"
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
        internal lateinit var subScreenState: NavHostStateImpl<SubScreen, *>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            @Suppress("DEPRECATION")
            val hostParam = intent.getSerializableExtra(Extra.NavHostParam) as NavHostParam

            @Suppress("DEPRECATION")
            val factoryParam =
                intent.getSerializableExtra(Extra.FactoryParam) as ViewModelFactoryParam

            setContent {
                screenController = rememberNavController(Screen.A)
                @OptIn(ExperimentalReimaginedApi::class)
                screenState = rememberNavHostState(screenController.backstack,)
                ParamNavHost(hostParam, screenState) { screen ->
                    paramViewModel(
                        factoryParam = factoryParam,
                        viewModelStoreOwner = hostEntries.find { it.destination == Screen.A }!!
                    ) { savedStateHandle ->
                        TestViewModel(savedStateHandle)
                    }

                    if (screen == Screen.WithNestedEntries) {
                        SubScreenHost(hostParam, factoryParam)
                    }
                }
            }
        }

        @Suppress("TestFunctionName")
        @Composable
        private fun SubScreenHost(hostParam: NavHostParam, factoryParam: ViewModelFactoryParam) {
            subScreenController = rememberNavController(SubScreen.X)
            subScreenState = rememberNavHostStateImpl(
                backstack = subScreenController.backstack,
                scopeSpec = EmptyScopeSpec
            )
            ParamNavHost(hostParam, subScreenState) {
                paramViewModel(
                    factoryParam = factoryParam,
                    viewModelStoreOwner = hostEntries.find { it.destination == SubScreen.X }!!
                ) { savedStateHandle ->
                    TestViewModel(savedStateHandle)
                }
            }
        }

    }

    private fun recreateActivity_firstEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.screenState.findHostEntry(Screen.A)
        )
        viewModel1.state = Value.Value2

        composeRule.recreateActivity()
        assertThat(viewModel1.state).isEqualTo(Value.Value2)
    }

    @Test
    fun recreateActivity_currentEntry() {
        recreateActivity_firstEntry()
    }

    @Test
    fun recreateActivity_backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        recreateActivity_firstEntry()
    }

    private fun recreateActivity_firstNestedEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.subScreenState.findHostEntry(SubScreen.X)
        )
        viewModel1.state = Value.Value2

        composeRule.recreateActivity()
        assertThat(viewModel1.state).isEqualTo(Value.Value2)
    }

    @Test
    fun recreateActivity_currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()

        recreateActivity_firstNestedEntry()
    }

    @Test
    fun recreateActivity_backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        recreateActivity_firstNestedEntry()
    }

    private fun recreateActivityAndClearViewModels_firstEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.screenState.findHostEntry(Screen.A)
        )
        viewModel1.state = Value.Value2

        composeRule.recreateActivityAndClearViewModels()
        val viewModel2 = getExistingViewModel<TestViewModel>(
            composeRule.activity.screenState.findHostEntry(Screen.A)
        )
        assertThat(viewModel2.state).isEqualTo(Value.Value2)
    }

    @Test
    fun recreateActivityAndClearViewModels_currentEntry() {
        recreateActivityAndClearViewModels_firstEntry()
    }

    @Test
    fun recreateActivityAndClearViewModels_backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        recreateActivityAndClearViewModels_firstEntry()
    }

    private fun recreateActivityAndClearViewModels_firstNestedEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.subScreenState.findHostEntry(SubScreen.X)
        )
        viewModel1.state = Value.Value2

        composeRule.recreateActivityAndClearViewModels()
        val viewModel2 = getExistingViewModel<TestViewModel>(
            composeRule.activity.subScreenState.findHostEntry(SubScreen.X)
        )
        assertThat(viewModel2.state).isEqualTo(Value.Value2)
    }

    @Test
    fun recreateActivityAndClearViewModels_currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()

        recreateActivityAndClearViewModels_firstNestedEntry()
    }

    @Test
    fun recreateActivityAndClearViewModels_backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        recreateActivityAndClearViewModels_firstNestedEntry()
    }

    private fun savedStateHandleIsReconnected_firstEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.screenState.findHostEntry(Screen.A)
        )
        assertThat(viewModel1.state).isEqualTo(Value.Value1)

        composeRule.recreateActivity()
        viewModel1.state = Value.Value2

        composeRule.recreateActivityAndClearViewModels()
        val viewModel2 = getExistingViewModel<TestViewModel>(
            composeRule.activity.screenState.findHostEntry(Screen.A)
        )
        assertThat(viewModel2.state).isEqualTo(Value.Value2)
    }

    @Test
    fun savedStateHandleIsReconnected_currentEntry() {
        savedStateHandleIsReconnected_firstEntry()
    }

    @Test
    fun savedStateHandleIsReconnected_backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        savedStateHandleIsReconnected_firstEntry()
    }

    private fun savedStateHandleIsReconnected_firstNestedEntry() {
        val viewModel1 = getExistingViewModel<TestViewModel>(
            composeRule.activity.subScreenState.findHostEntry(SubScreen.X)
        )
        assertThat(viewModel1.state).isEqualTo(Value.Value1)

        composeRule.recreateActivity()
        viewModel1.state = Value.Value2

        composeRule.recreateActivityAndClearViewModels()
        val viewModel2 = getExistingViewModel<TestViewModel>(
            composeRule.activity.subScreenState.findHostEntry(SubScreen.X)
        )
        assertThat(viewModel2.state).isEqualTo(Value.Value2)
    }

    @Test
    fun savedStateHandleIsReconnected_currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()

        savedStateHandleIsReconnected_firstNestedEntry()
    }

    @Test
    fun savedStateHandleIsReconnected_backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        savedStateHandleIsReconnected_firstNestedEntry()
    }

}