package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import org.koin.androidx.compose.getStateViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen() = ScreenLayout(
    title = "Koin demo"
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
    val viewModel = getViewModel<FirstViewModel>()

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
    val viewModel = getViewModel<SecondViewModel> { parametersOf(id) }

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
    val viewModel = getStateViewModel<ThirdViewModel> { parametersOf(text) }
}