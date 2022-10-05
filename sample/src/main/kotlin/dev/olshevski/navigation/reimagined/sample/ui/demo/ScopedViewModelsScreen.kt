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
import dev.olshevski.navigation.reimagined.NavScopeSpec
import dev.olshevski.navigation.reimagined.navScope
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

private val Scope = navScope("scope-key")

private val ScopeSpec = NavScopeSpec<ScopedViewModelsDestination> {
    if (it is ScopedViewModelsDestination.Second || it is ScopedViewModelsDestination.Third) {
        setOf(Scope)
    } else {
        emptySet()
    }
}

@Composable
fun ScopedViewModelsScreen() = ScreenLayout(
    title = stringResource(R.string.scoped_view_models__demo_screen_title)
) {
    val navController =
        rememberNavController<ScopedViewModelsDestination>(startDestination = ScopedViewModelsDestination.First)

    NavBackHandler(navController)

    NavHost(
        controller = navController,
        scopeSpec = ScopeSpec
    ) { destination ->
        when (destination) {
            ScopedViewModelsDestination.First -> FirstScreen(
                toSecondScreenButtonClick = { navController.navigate(ScopedViewModelsDestination.Second) }
            )
            ScopedViewModelsDestination.Second -> SecondScreen(
                toThirdScreenButtonClick = { navController.navigate(ScopedViewModelsDestination.Third) }
            )
            ScopedViewModelsDestination.Third -> ThirdScreen()
        }
    }
}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.scoped_view_models__first_screen_title)
) {

    CenteredText(
        text = """Scoped ViewModelStore may come in handy when you need to share
            the same ViewModel instance between arbitrary destinations in the NavHost.
            """.singleLine(),
    )

    CenteredText(
        text = """To create it, you need to associate the desired destination with a scope in
             a scopeSpec block. Then you can call getScopedViewModelStoreOwner() with the specified
             scope key from different destinations.""".singleLine(),
    )

    CenteredText(
        text = """Only when all entries from the same defined scoped are removed,
            |the ViewModelStore and all its ViewModels will be cleared.""".singleLine(),
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.scoped_view_models__to_second_screen_button))
    }

}

class ScopedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val text = savedStateHandle.getStateFlow("text", "")

    fun onTextChange(text: String) {
        savedStateHandle["text"] = text
    }
}

@Composable
private fun NavHostScope<ScopedViewModelsDestination>.SecondScreen(
    toThirdScreenButtonClick: () -> Unit,
) = ContentLayout(
    title = stringResource(R.string.scoped_view_models__second_screen_title)
) {
    val scopedViewModel = viewModel<ScopedViewModel>(
        viewModelStoreOwner = getScopedViewModelStoreOwner(Scope)
    )

    CenteredText(
        text = """In this demo the ViewModelStore is shared between the Second and the Third
            screens. The shared ViewModelStore will be cleared only when returning to the First
            screen.""".singleLine(),
    )

    CenteredText(
        text = "Please enter some text. It will be stored in a ScopedViewModel.",
    )

    val text by scopedViewModel.text.collectAsState()
    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { scopedViewModel.onTextChange(it) }
    )

    Button(onClick = toThirdScreenButtonClick) {
        Text(stringResource(R.string.scoped_view_models__to_third_screen_button))
    }
}

@Composable
private fun NavHostScope<ScopedViewModelsDestination>.ThirdScreen() = ContentLayout(
    title = stringResource(R.string.scoped_view_models__third_screen_title)
) {
    val scopedViewModel = viewModel<ScopedViewModel>(
        viewModelStoreOwner = getScopedViewModelStoreOwner(Scope)
    )

    val text by scopedViewModel.text.collectAsState()
    CenteredText(
        text = stringResource(R.string.scoped_view_models__text_from_shared_view_model, text),
    )
}