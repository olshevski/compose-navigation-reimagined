package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
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
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

private interface AcceptsResultFromSecond {
    val resultFromSecond: MutableState<String?>
}

private sealed class ReturnResultsDestination : Parcelable {

    /*
    * 1) The type may be @Stable, not only @Immutable. This guarantee is backed up by MutableState
    *    here.
    * 2) MutableState is Parcelable, it just doesn't expose this interface, so we are fine.
    */
    @Stable
    @Parcelize
    data class First(
        override val resultFromSecond: @RawValue MutableState<String?> = mutableStateOf(null)
    ) : ReturnResultsDestination(), AcceptsResultFromSecond

    @Immutable
    @Parcelize
    data object Second : ReturnResultsDestination()

}

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
                    onOpenSecondScreenButtonClick = {
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
    onOpenSecondScreenButtonClick: () -> Unit,
    onClearResultClick: () -> Unit
) = ContentLayout(title = stringResource(R.string.return_results__first_screen_title)) {

    CenteredText(
        text = "To check out how you can return values from destinations go to the next screen",
    )

    Button(onClick = onOpenSecondScreenButtonClick) {
        Text(stringResource(R.string.return_results__open_second_screen_button))
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
        text = "Here you can enter some text and pass it back to the previous screen",
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = onTextChange
    )

    Button(onClick = returnToFirstScreenButtonClick) {
        Text(stringResource(R.string.return_results__return_result_to_first_screen_button))
    }
}
