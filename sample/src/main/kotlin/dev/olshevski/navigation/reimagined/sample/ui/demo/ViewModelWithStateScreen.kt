package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.StateBackNavigator
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

sealed class StateViewModelsDestination : Parcelable {

    @Parcelize
    object First : StateViewModelsDestination()

    @Parcelize
    object Second : StateViewModelsDestination()

    @Parcelize
    data class Third(val text: String) : StateViewModelsDestination()
}

@Composable
fun StateViewModelsScreen() = ScreenLayout(
    title = stringResource(R.string.view_models__demo_screen_title),
) {
    val navigationViewModel = viewModel<StateNavigationViewModel>()
    val state by navigationViewModel.state.collectAsStateWithLifecycle()

    val navController = rememberNavController(startDestination = state)

    BackHandler(enabled = navController.backstack.entries.size > 1) {
        navigationViewModel.onBackPress()
    }

    StateBackNavigator(state, navController)

    NavHost(navController) { destination ->
        when (destination) {
            StateViewModelsDestination.First -> FirstScreen(
                onOpenSecondScreenButtonClick = navigationViewModel::onOpenSecondScreenButtonClick,
            )
            StateViewModelsDestination.Second -> SecondScreen(
                onOpenThirdScreenButtonClick = {
                    navigationViewModel.onOpenThirdScreenButtonClick("hi")
                },
            )
            is StateViewModelsDestination.Third -> ThirdScreen(destination.text)
        }
    }
}

class StateNavigationViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _state: BackStackStateFlow<StateViewModelsDestination> =
        BackStackStateFlow(StateViewModelsDestination.First)
    val state = _state.asStateFlow()

    fun onBackPress() {
        _state.back()
    }

    fun onOpenSecondScreenButtonClick() {
        _state.push { StateViewModelsDestination.Second }
    }

    fun onOpenThirdScreenButtonClick(text: String) {
        _state.push { StateViewModelsDestination.Third(text) }
    }
}

@Composable
private fun FirstScreen(
    onOpenSecondScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.view_models__first_screen_title),
) {
    CenteredText(
        text = "This demo shows two use cases:",
    )

    CenteredText(
        text = """1) The ability to hoist NavController to ViewModel,
            effectively bringing all the navigation logic to that level""".singleLine(),
    )

    CenteredText(
        text = """2) The usage of scoped ViewModels.
            Every ViewModel within NavHost is scoped to its backstack entry and
            gets cleared only when the entry is removed from the backstack.""".singleLine(),
    )

    Button(onClick = onOpenSecondScreenButtonClick) {
        Text(stringResource(R.string.view_models__open_second_screen_button))
    }
}

@Composable
private fun SecondScreen(
    onOpenThirdScreenButtonClick: (String) -> Unit,
) = ContentLayout(
    title = stringResource(R.string.view_models__second_screen_title),
) {
    CenteredText(
        text = "Click the button to open the third screen.",
    )

    Button(onClick = { onOpenThirdScreenButtonClick("third screen") }) {
        Text(stringResource(R.string.view_models__open_third_screen_button))
    }
}

@Composable
private fun ThirdScreen(text: String) = ContentLayout(
    title = stringResource(R.string.view_models__third_screen_title),
) {
    CenteredText(
        text = stringResource(R.string.view_models__text_from_previous_screen, text),
    )
}
class BackStackStateFlow<T : Any> constructor(initialValue: T) {
    private val _state: MutableStateFlow<T> = MutableStateFlow(initialValue)
    private val previousStatesByClass: MutableList<T> = mutableListOf()

    fun asStateFlow(): StateFlow<T> = _state.asStateFlow()

    fun push(function: (T) -> T) {
        val prevValue = _state.value
        _state.update(function)
        if (prevValue::class != _state.value::class) {
            previousStatesByClass.add(prevValue)
        }
    }

    fun back(callback: ((T) -> Unit)? = null) {
        previousStatesByClass.removeLastOrNull()?.let { previousState ->
            _state.update { previousState }
            callback?.invoke(previousState)
        }
    }
}
