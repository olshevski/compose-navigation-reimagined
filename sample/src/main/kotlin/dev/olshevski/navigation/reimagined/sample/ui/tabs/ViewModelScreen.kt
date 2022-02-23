package dev.olshevski.navigation.reimagined.sample.ui.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.SubScreenLayout

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

    // You may expose only read-only NavBackstack as it is done here, but it doesn't matter
    // to you, just make navController public and use NavBackHandler.
    val navBackstack = navController.backstack
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
) = SubScreenLayout(title = "First screen") {

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
        Text("To Second screen")
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
) = SubScreenLayout(title = "Second screen") {

    CenteredText(
        text = """Please enter some text. It will be stored in ViewModel as well as its state
            preserved by SavedStateHandle.""".singleLine(),
    )


    OutlinedTextField(value = text, onValueChange = { onTextChange(it) })

    Button(onClick = toThirdScreenButtonClick) {
        Text("To Third screen")
    }
}

@Composable
private fun ThirdScreen(text: String) = SubScreenLayout(title = "Third screen") {
    CenteredText(
        text = "Text from previous screen: $text",
    )
}