package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.viewModelEntryPoint
import dev.olshevski.viewmodel.easyfactories.savedStateViewModel
import dev.olshevski.viewmodel.easyfactories.viewModel

@Composable
fun MainScreen() = ScreenLayout(
    title = "Hilt demo"
) {
    val navController = rememberNavController<MainDestination>(
        startDestination = MainDestination.First
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            MainDestination.First -> FirstScreen(
                toSecondScreenButtonClick = {
                    navController.navigate(MainDestination.Second(123))
                }
            )
            is MainDestination.Second -> SecondScreen(
                id = destination.id,
                toThirdScreenButtonClick = {
                    navController.navigate(MainDestination.Third("hello"))
                }
            )
            is MainDestination.Third -> ThirdScreen(destination.text)
        }
    }
}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit
) = ContentLayout(
    title = "First screen"
) {
    val viewModelEntryPoint = LocalContext.current.viewModelEntryPoint
    val viewModel = viewModel {
        viewModelEntryPoint.firstViewModel()
    }

    Button(
        onClick = { toSecondScreenButtonClick() }
    ) {
        Text("To Second screen")
    }
}

@Composable
private fun SecondScreen(
    id: Int,
    toThirdScreenButtonClick: () -> Unit
) = ContentLayout(
    title = "Second screen id=$id"
) {
    val viewModelEntryPoint = LocalContext.current.viewModelEntryPoint
    val viewModel = viewModel {
        viewModelEntryPoint.secondViewModelFactory().create(id)
    }

    Button(
        onClick = { toThirdScreenButtonClick() }
    ) {
        Text("To Third screen")
    }
}

@Composable
private fun ThirdScreen(
    text: String
) = ContentLayout(
    title = "Third screen text=$text"
) {
    val viewModelEntryPoint = LocalContext.current.viewModelEntryPoint
    val viewModel = savedStateViewModel { savedStateHandle ->
        viewModelEntryPoint.thirdViewModelFactory().create(text, savedStateHandle)
    }
}