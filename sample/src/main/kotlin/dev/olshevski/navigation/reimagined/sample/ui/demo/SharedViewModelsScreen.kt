package dev.olshevski.navigation.reimagined.sample.ui.demo

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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.NavHostScope
import dev.olshevski.navigation.reimagined.navKey
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun SharedViewModelsScreen() = ScreenLayout(
    title = stringResource(R.string.shared_view_models__demo_screen_title)
) {
    val navController =
        rememberNavController<SharedViewModelsDestination>(startDestination = SharedViewModelsDestination.First)

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            SharedViewModelsDestination.First -> FirstScreen(
                toSecondScreenButtonClick = { navController.navigate(SharedViewModelsDestination.Second) }
            )
            SharedViewModelsDestination.Second -> SecondScreen(
                toThirdScreenButtonClick = { navController.navigate(SharedViewModelsDestination.Third) }
            )
            SharedViewModelsDestination.Third -> ThirdScreen()
        }
    }
}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.shared_view_models__first_screen_title)
) {

    CenteredText(
        text = """Shared ViewModelStore may come in handy when you need to share
            the same ViewModel instance between arbitrary destinations in the NavHost.
            """.singleLine(),
    )

    CenteredText(
        text = """To create it, simply call getSharedViewModelStoreOwner() with the same key within
            different destinations. NavHost will remember that this destination requested this
            shared ViewModelStore.""".singleLine(),
    )

    CenteredText(
        text = """Only when all entries that requested the shared the same ViewModelStore
            are remove, the ViewModelStore and all its ViewModels will be cleared.""".singleLine(),
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.shared_view_models__to_second_screen_button))
    }

}

private val SharedKey = navKey("shared-view-model-store-key")

class SharedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val text = savedStateHandle.getStateFlow("text", "")

    fun onTextChange(text: String) {
        savedStateHandle["text"] = text
    }
}

@Composable
private fun NavHostScope<SharedViewModelsDestination>.SecondScreen(
    toThirdScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.shared_view_models__second_screen_title)
) {
    val sharedViewModel = viewModel<SharedViewModel>(
        viewModelStoreOwner = getSharedViewModelStoreOwner(SharedKey)
    )

    CenteredText(
        text = """In this demo the ViewModelStore is shared between the Second and the Third
            screens. The shared ViewModelStore will be cleared only when returning to the First
            screen.""".singleLine(),
    )

    CenteredText(
        text = "Please enter some text. It will be stored in a SharedViewModel.",
    )

    val text by sharedViewModel.text.collectAsState()
    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { sharedViewModel.onTextChange(it) }
    )

    Button(onClick = toThirdScreenButtonClick) {
        Text(stringResource(R.string.shared_view_models__to_third_screen_button))
    }
}

@Composable
private fun NavHostScope<SharedViewModelsDestination>.ThirdScreen() = ContentLayout(
    title = stringResource(R.string.shared_view_models__third_screen_title)
) {
    val sharedViewModel = viewModel<SharedViewModel>(
        viewModelStoreOwner = getSharedViewModelStoreOwner(SharedKey)
    )

    val text by sharedViewModel.text.collectAsState()
    CenteredText(
        text = stringResource(R.string.shared_view_models__text_from_shared_view_model, text),
    )
}