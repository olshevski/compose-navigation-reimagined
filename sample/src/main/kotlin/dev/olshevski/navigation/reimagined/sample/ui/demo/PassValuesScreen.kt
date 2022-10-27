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
import dev.olshevski.navigation.reimagined.popUpTo
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun PassValuesScreen() = ScreenLayout(
    title = stringResource(R.string.pass_values__demo_screen_title)
) {
    val navController = rememberNavController<PassValuesDestination>(
        startDestination = PassValuesDestination.A
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            PassValuesDestination.A -> {
                var text by rememberSaveable { mutableStateOf("") }
                ScreenA(
                    text = text,
                    onTextChange = { text = it },
                    onOpenScreenBButtonClick = {
                        navController.navigate(PassValuesDestination.B(0))
                    },
                    onOpenScreenCButtonClick = {
                        navController.navigate(PassValuesDestination.C(text))
                    }
                )
            }
            is PassValuesDestination.B -> ScreenB(
                id = destination.id,
                onOpenScreenBPlusOneButtonClick = {
                    navController.navigate(PassValuesDestination.B(destination.id + 1))
                },
                onReturnBackToScreenAButtonClick = {
                    navController.popUpTo { it is PassValuesDestination.A }
                }
            )
            is PassValuesDestination.C -> ScreenC(destination.text)
        }
    }
}

@Composable
private fun ScreenA(
    text: String,
    onTextChange: (String) -> Unit,
    onOpenScreenBButtonClick: () -> Unit,
    onOpenScreenCButtonClick: () -> Unit
) = ContentLayout(
    title = stringResource(R.string.pass_values__screen_a_title)
) {
    CenteredText(
        text = """You can pass any serializable/parcelable data you want. Here you
            can open a screen B with an integer parameter 'id' equal to 0.""".singleLine(),
    )

    Button(
        onClick = { onOpenScreenBButtonClick() }
    ) {
        Text(stringResource(R.string.pass_values__open_screen_b_button))
    }

    CenteredText(
        text = """Alternatively, you can enter some text in the text field below and pass it to
            screen C""".singleLine(),
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { onTextChange(it) })

    Button(
        onClick = { onOpenScreenCButtonClick() }
    ) {
        Text(stringResource(R.string.pass_values__open_screen_c_button))
    }

    CenteredText(
        text = """Of course, you are not limited just to primitive types. Create complex data
            structures and add @Parcelize to them.""".singleLine(),
    )
}

@Composable
private fun ScreenB(
    id: Int,
    onOpenScreenBPlusOneButtonClick: () -> Unit,
    onReturnBackToScreenAButtonClick: () -> Unit
) = ContentLayout(
    title = stringResource(R.string.pass_values__screen_b_title, id)
) {
    CenteredText(
        text = "You can keep opening new screens B with an incrementing parameter id",
    )

    Button(
        onClick = { onOpenScreenBPlusOneButtonClick() }
    ) {
        Text(stringResource(R.string.pass_values__open_screen_b_plus_one_button))
    }

    if (id > 0) {
        Button(
            onClick = { onReturnBackToScreenAButtonClick() }
        ) {
            Text(stringResource(R.string.pass_values__back_to_screen_a_button))
        }
    }
}

@Composable
private fun ScreenC(text: String) = ContentLayout(
    title = stringResource(R.string.pass_values__screen_c_title)
) {
    Text(stringResource(R.string.pass_values__passed_text, text))
}