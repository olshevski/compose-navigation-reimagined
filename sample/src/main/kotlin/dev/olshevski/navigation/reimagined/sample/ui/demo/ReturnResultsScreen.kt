package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun ReturnResultsScreen() = ScreenLayout(
    title = stringResource(R.string.return_results__demo_screen_title)
) {

    val navController = rememberNavController<ReturnResultsDestination>(
        startDestination = ReturnResultsDestination.First()
    )

    NavBackHandler(navController)

    NavHost(controller = navController) { destination ->
        when (destination) {
            is ReturnResultsDestination.First -> {
                var resultFromSecond by destination.resultFromSecond
                FirstScreen(
                    resultFromSecond = resultFromSecond,
                    toSecondScreenButtonClick = {
                        navController.navigate(ReturnResultsDestination.Second)
                    },
                    onClearResultClick = {
                        resultFromSecond = null
                    }
                )
            }
            is ReturnResultsDestination.Second -> {
                var text by rememberSaveable { mutableStateOf("") }
                SecondScreen(
                    text = text,
                    onTextChange = { text = it },
                    returnToFirstScreenButtonClick = {
                        val previousDestination = navController.backstack.entries.let {
                            it[it.lastIndex - 1].destination
                        }
                        check(previousDestination is AcceptsResultFromSecond)
                        previousDestination.resultFromSecond.value = text
                        navController.pop()
                    }
                )
            }
        }
    }

}

@Composable
private fun FirstScreen(
    resultFromSecond: String?,
    toSecondScreenButtonClick: () -> Unit,
    onClearResultClick: () -> Unit
) = ContentLayout(title = stringResource(R.string.return_results__first_screen_title)) {

    CenteredText(
        text = "To check out how you can return values from destinations go to the next screen.",
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.return_results__to_second_screen_button))
    }

    if (resultFromSecond != null) {
        Text(stringResource(R.string.return_results__result_from_second_screen, resultFromSecond))

        Button(onClick = onClearResultClick) {
            Text(stringResource(R.string.return_results__clear_result_button))
        }
    }

}

@Composable
private fun SecondScreen(
    text: String,
    onTextChange: (String) -> Unit,
    returnToFirstScreenButtonClick: () -> Unit
) = ContentLayout(title = stringResource(R.string.return_results__second_screen_title)) {

    CenteredText(
        text = "Here you can enter some text and pass it back to the previous screen.",
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = onTextChange
    )

    Button(onClick = returnToFirstScreenButtonClick) {
        Text(stringResource(R.string.return_results__return_result_to_first_screen_button))
    }

    CenteredText(
        text = """Note: use it carefully. Mutable state increases the complexity of the backstack 
            logic. Sometimes it is more reasonable to have a hoisted data holder.""".singleLine(),
    )
}
