package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun ViewModelsScreen() = ScreenLayout(
    title = stringResource(R.string.view_models__demo_screen_title)
) {
    val navigationViewModel = viewModel<NavigationViewModel>()

    BackHandler(navigationViewModel.backHandlerEnabled) {
        navigationViewModel.onBackPress()
    }

    NavHost(backstack = navigationViewModel.navBackstack) { destination ->
        when (destination) {
            ViewModelsDestination.First -> FirstScreen(
                toSecondScreenButtonClick = navigationViewModel::toSecondScreenButtonClick
            )
            ViewModelsDestination.Second -> SecondScreen(
                toThirdScreenButtonClick = navigationViewModel::toThirdScreenButtonClick
            )
            is ViewModelsDestination.Third -> ThirdScreen(destination.text)
        }
    }
}

class NavigationViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val navController by savedStateHandle.saveable<NavController<ViewModelsDestination>> {
        navController(
            startDestination = ViewModelsDestination.First
        )
    }

    // You may either make navController public or just its navBackstack. The latter is convenient
    // when you don't want to expose navigation methods in the UI layer.
    val navBackstack get() = navController.backstack
    val backHandlerEnabled get() = navController.backstack.entries.size > 1

    fun onBackPress() {
        navController.pop()
    }

    fun toSecondScreenButtonClick() {
        navController.navigate(ViewModelsDestination.Second)
    }

    fun toThirdScreenButtonClick(text: String) {
        navController.navigate(ViewModelsDestination.Third(text))
    }

}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.view_models__first_screen_title)
) {

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
        Text(stringResource(R.string.view_models__to_second_screen_button))
    }

}

@Composable
private fun SecondScreen(
    toThirdScreenButtonClick: (String) -> Unit,
) = ContentLayout(
    title = stringResource(R.string.view_models__second_screen_title)
) {
    val secondViewModel = viewModel<SecondViewModel>()
    val text by secondViewModel.text.collectAsState()

    CenteredText(
        text = """Please enter some text. It will be stored in ViewModel as well as its state
            preserved by SavedStateHandle.""".singleLine(),
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { secondViewModel.onTextChange(it) })

    Button(onClick = { toThirdScreenButtonClick(text) }) {
        Text(stringResource(R.string.view_models__to_third_screen_button))
    }
}

class SecondViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val text = savedStateHandle.getStateFlow("text", "")

    fun onTextChange(text: String) {
        savedStateHandle["text"] = text
    }

}

@Composable
private fun ThirdScreen(text: String) = ContentLayout(
    title = stringResource(R.string.view_models__third_screen_title)
) {
    CenteredText(
        text = stringResource(R.string.view_models__text_from_previous_screen, text),
    )
}