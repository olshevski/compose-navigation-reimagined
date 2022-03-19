package dev.olshevski.navigation.reimagined.sample.ui.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.SubScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun ViewModelScreen() {
    val navigationViewModel = viewModel<NavigationViewModel>()

    BackHandler(navigationViewModel.backHandlerEnabled) {
        navigationViewModel.onBackPress()
    }

    NavHost(backstack = navigationViewModel.navBackstack) { destination ->
        when (destination) {
            ViewModelDestination.First -> FirstScreen(
                toSecondScreenButtonClick = navigationViewModel::toSecondScreenButtonClick
            )
            ViewModelDestination.Second -> {
                val secondViewModel = viewModel<SecondViewModel>()
                val text by secondViewModel.text.observeAsState(initial = "")
                SecondScreen(
                    text = text,
                    onTextChange = secondViewModel::onTextChange,
                    toThirdScreenButtonClick = {
                        navigationViewModel.toThirdScreenButtonClick(text)
                    }
                )
            }
            is ViewModelDestination.Third -> ThirdScreen(destination.text)
        }
    }
}

class NavigationViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val navController by savedStateHandle.navController<ViewModelDestination>(
        startDestination = ViewModelDestination.First
    )

    // You may either make navController public or just its navBackstack. The latter is convenient
    // when you don't want to expose navigation methods in the UI layer.
    val navBackstack get() = navController.backstack
    val backHandlerEnabled get() = navController.backstack.entries.size > 1

    fun onBackPress() {
        navController.pop()
    }

    fun toSecondScreenButtonClick() {
        navController.navigate(ViewModelDestination.Second)
    }

    fun toThirdScreenButtonClick(text: String) {
        navController.navigate(ViewModelDestination.Third(text))
    }

}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit,
) = SubScreenLayout(title = stringResource(R.string.viewmodel_first_screen_title)) {

    CenteredText(
        text = "This demo shows two use cases:",
    )

    CenteredText(
        text = """1) the ability to hoist NavController to ViewModel,
            effectively bringing all the navigation logic to that level;""".singleLine(),
    )

    CenteredText(
        text = """2) the usage of scoped ViewModels:
            every ViewModel within NavHost is scoped to its backstack entry and
            gets cleared only when the entry is removed from backstack.""".singleLine(),
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.viewmodel_to_second_screen_button))
    }

}

class SecondViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _text = savedStateHandle.getLiveData<String>("text")
    val text = _text as LiveData<String>

    fun onTextChange(text: String) {
        _text.value = text
    }

}

@Composable
private fun SecondScreen(
    text: String,
    onTextChange: (String) -> Unit,
    toThirdScreenButtonClick: () -> Unit,
) = SubScreenLayout(title = stringResource(R.string.viewmodel_second_screen_title)) {

    CenteredText(
        text = """Please enter some text. It will be stored in ViewModel as well as its state
            preserved by SavedStateHandle.""".singleLine(),
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { onTextChange(it) })

    Button(onClick = toThirdScreenButtonClick) {
        Text(stringResource(R.string.viewmodel_to_third_screen_button))
    }
}

@Composable
private fun ThirdScreen(text: String) =
    SubScreenLayout(title = stringResource(R.string.viewmodel_third_screen_title)) {
        CenteredText(
            text = stringResource(R.string.viewmodel_text_from_previous_screen, text),
        )
    }