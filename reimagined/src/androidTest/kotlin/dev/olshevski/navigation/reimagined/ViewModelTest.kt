package dev.olshevski.navigation.reimagined

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        A, B
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
        internal lateinit var screenState: NavHostState<Screen, Nothing>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val hostParam = intent.getSerializableExtra(Extra.NavHostParam) as NavHostParam
            setContent {
                screenController = rememberNavController(Screen.A)
                screenState = rememberNavHostState(screenController.backstack, EmptyScopeSpec)
                ParamNavHost(hostParam, screenState) { _ ->
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

    @Test
    fun poppedViewModelIsCleared() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        val viewModel =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[1])

        composeRule.activity.screenController.pop()
        composeRule.waitForIdle()

        assertThat(viewModel.cleared).isTrue()
    }

    @Test
    fun removedBackstackViewModelIsCleared() {
        composeRule.activity.screenController.navigate(Screen.B)
        composeRule.waitForIdle()

        val viewModel =
            getExistingViewModel<TestViewModel>(composeRule.activity.screenState.hostEntries[0])

        composeRule.activity.screenController.setNewBackstack(
            entries = composeRule.activity.screenController.backstack.entries.drop(1)
        )
        composeRule.waitForIdle()

        assertThat(viewModel.cleared).isTrue()
    }

}