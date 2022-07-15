package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun MainScreen() = ScreenLayout(
    title = "Hilt (hiltViewModel) demo"
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
    val viewModel = hiltViewModel<FirstViewModel>()

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
    val viewModel = hiltViewModel<SecondViewModel>(defaultArguments = bundleOf(
        "id" to id
    ))

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
    val viewModel = hiltViewModel<ThirdViewModel>(defaultArguments = bundleOf(
        "text" to text
    ))
}