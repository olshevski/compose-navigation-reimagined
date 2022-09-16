package dev.olshevski.navigation.reimagined

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.common.truth.Truth.assertThat
import dev.olshevski.navigation.reimagined.param.NavHostParam
import dev.olshevski.navigation.reimagined.param.ParamNavHost
import dev.olshevski.navigation.testutils.createAndroidIntentComposeRule
import dev.olshevski.navigation.testutils.getExistingViewModel
import dev.olshevski.navigation.testutils.recreateActivity
import dev.olshevski.navigation.testutils.recreateActivityAndClearViewModels
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ViewModelTest(
    private val hostParam: NavHostParam
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = NavHostParam.values().asList()
    }

    @get:Rule
    val composeRule = createAndroidIntentComposeRule<TestActivity> {
        Intent(it, TestActivity::class.java).apply {
            putExtra(TestActivity.Extra.NavHostParam, hostParam)
        }
    }

    enum class Screen {
        A, B, WithNestedEntries
    }

    enum class SubScreen {
        X, Y
    }

    class TestViewModel : ViewModel() {

        var cleared = false
            private set

        override fun onCleared() {
            cleared = true
        }
    }

    class TestActivity : ComponentActivity() {

        object Extra {
            const val NavHostParam = "NavHostParam"
        }

        lateinit var screenController: NavController<Screen>
        internal lateinit var screenState: NavHostState<Screen>

        lateinit var subScreenController: NavController<SubScreen>
        internal lateinit var subScreenState: NavHostState<SubScreen>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val hostParam = intent.getSerializableExtra(Extra.NavHostParam) as NavHostParam
            setContent {
                screenController = rememberNavController(Screen.A)
                screenState = rememberNavHostState(screenController.backstack)
                ParamNavHost(hostParam, screenState) { screen ->
                    hostEntries.forEach {
                        viewModel(
                            viewModelStoreOwner = it
                        ) {
                            TestViewModel()
                        }
                    }

                    if (screen == Screen.WithNestedEntries) {
                        SubScreenHost(hostParam)
                    }
                }
            }
        }

        @Suppress("TestFunctionName")
        @Composable
        private fun SubScreenHost(hostParam: NavHostParam) {
            subScreenController = rememberNavController(SubScreen.X)
            subScreenState = rememberNavHostState(subScreenController.backstack)
            ParamNavHost(hostParam, subScreenState) {
                hostEntries.forEach {
                    viewModel(
                        viewModelStoreOwner = it
                    ) {
                        TestViewModel()
                    }
                }
            }
        }
    }

    private fun viewModelIsRecreated_firstEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])

        composeRule.recreateActivityAndClearViewModels()
        assertThat(viewModel1.cleared).isTrue()

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

    private fun viewModelIsRestored_firstEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])

        composeRule.recreateActivity()
        assertThat(viewModel1.cleared).isFalse()

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        assertThat(viewModel1).isSameInstanceAs(viewModel2)
    }

    @Test
    fun viewModelIsRecreated_currentEntry() {
        viewModelIsRecreated_firstEntry()
    }

    @Test
    fun viewModelIsRestored_currentEntry() {
        viewModelIsRestored_firstEntry()
    }

    @Test
    fun viewModelIsRecreated_backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        viewModelIsRecreated_firstEntry()
    }

    @Test
    fun viewModelIsRestored_backstackEntry() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        viewModelIsRestored_firstEntry()
    }

    @Test
    fun viewModelsAreDifferentForEntries() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])
        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[1])
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

    private fun viewModelIsRecreated_firstNestedEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])

        composeRule.recreateActivityAndClearViewModels()
        assertThat(viewModel1.cleared).isTrue()

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

    private fun viewModelIsRestored_firstNestedEntry() {
        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])

        composeRule.recreateActivity()
        assertThat(viewModel1.cleared).isFalse()

        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        assertThat(viewModel1).isSameInstanceAs(viewModel2)
    }

    @Test
    fun viewModelIsRecreated_currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()

        viewModelIsRecreated_firstNestedEntry()
    }

    @Test
    fun viewModelIsRestored_currentNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()

        viewModelIsRestored_firstNestedEntry()
    }

    @Test
    fun viewModelIsRecreated_backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        viewModelIsRecreated_firstNestedEntry()
    }

    @Test
    fun viewModelIsRestored_backstackNestedEntry() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        viewModelIsRestored_firstNestedEntry()
    }

    @Test
    fun viewModelsAreDifferentForNestedEntries() {
        composeRule.activity.screenController.navigate(Screen.WithNestedEntries)
        composeRule.waitForIdle()
        composeRule.activity.subScreenController.navigate(SubScreen.Y)
        composeRule.waitForIdle()

        val viewModel1 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[0])
        val viewModel2 =
            getExistingViewModel<TestViewModel>(composeRule.activity.subScreenState.hostEntries[1])
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

}