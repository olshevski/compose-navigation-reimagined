package dev.olshevski.navigation.reimagined.sample.ui.tabs

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
import androidx.compose.ui.window.Dialog
import dev.olshevski.navigation.reimagined.DialogNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popUpTo
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceLast
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.DialogLayout
import dev.olshevski.navigation.reimagined.sample.ui.SubScreenLayout
import dev.olshevski.navigation.reimagined.sample.ui.TestInputTag

@Composable
fun NavHostScreen() {
    val navController = rememberNavController<NavHostDestination>(
        startDestination = NavHostDestination.First
    )
    val dialogController = rememberNavController<DialogDestination>(
        initialBackstack = emptyList()
    )

    NavBackHandler(navController)

    NavHost(controller = navController) { destination ->
        when (destination) {
            NavHostDestination.First -> FirstScreen(
                toSecondScreenButtonClick = {
                    navController.navigate(NavHostDestination.Second(id = 0))
                },
            )
            is NavHostDestination.Second -> SecondScreen(
                id = destination.id,
                toSecondScreenButtonClick = {
                    navController.navigate(NavHostDestination.Second(id = destination.id + 1))
                },
                toThirdScreenButtonClick = {
                    navController.navigate(NavHostDestination.Third)
                }
            )
            NavHostDestination.Third -> ThirdScreen(
                toForthScreenButtonClick = {
                    navController.navigate(NavHostDestination.Forth())
                }
            )
            is NavHostDestination.Forth -> {
                var resultFromFifth by destination.resultFromFifth
                ForthScreen(
                    resultFromFifth = resultFromFifth,
                    toFirstDialogButtonClick = {
                        dialogController.navigate(DialogDestination.First)
                    },
                    toFifthScreenButtonClick = {
                        navController.navigate(NavHostDestination.Fifth)
                    },
                    onClearResultClick = {
                        resultFromFifth = null
                    }
                )
            }
            is NavHostDestination.Fifth -> {
                var text by rememberSaveable { mutableStateOf("") }
                FifthScreen(
                    text = text,
                    onTextChange = { text = it },
                    returnToForthScreenButtonClick = {
                        val previousDestination = navController.backstack.entries.let {
                            it[it.lastIndex - 1].destination
                        }
                        check(previousDestination is AcceptsResultFromFifth)
                        previousDestination.resultFromFifth.value = text
                        navController.pop()
                    },
                    backToFirstScreenButtonClick = {
                        navController.popUpTo { it == NavHostDestination.First }
                    }
                )
            }
        }
    }

    DialogNavHost(
        controller = dialogController,
    ) { destination ->
        Dialog(onDismissRequest = { dialogController.pop() }) {
            when (destination) {
                DialogDestination.First -> FirstDialogLayout(
                    toSecondDialogButtonClick = { dialogController.replaceLast(DialogDestination.Second) }
                )
                DialogDestination.Second -> SecondDialogLayout()
            }
        }
    }
}

@Composable
private fun FirstScreen(
    toSecondScreenButtonClick: () -> Unit,
) = SubScreenLayout(title = stringResource(R.string.navhost_first_screen)) {

    CenteredText(
        text = """NavHost switches between destinations without any animations.
            Go to Second screen and see how it works.""".singleLine(),
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.navhost_to_second_screen_button))
    }

}

@Composable
private fun SecondScreen(
    id: Int,
    toSecondScreenButtonClick: () -> Unit,
    toThirdScreenButtonClick: () -> Unit
) = SubScreenLayout(title = stringResource(R.string.navhost_second_screen, id)) {
    CenteredText(
        text = """You can pass any serializable/parcelable data you want. Here you
            can keep opening more Second screens with incrementing 'id' parameter.
            """.singleLine(),
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text(stringResource(R.string.navhost_to_second_screen_plus_one_button))
    }

    CenteredText(
        text = "Also try pressing back. Or just go to the third screen.",
    )

    Button(onClick = toThirdScreenButtonClick) {
        Text(stringResource(R.string.navhost_to_third_screen_button))
    }
}

@Composable
private fun ThirdScreen(
    toForthScreenButtonClick: () -> Unit
) = SubScreenLayout(title = stringResource(R.string.navhost_third_screen)) {
    CenteredText(
        text = "Now enter some text. This text will be saved while the screen is in the backstack.",
    )

    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = { text = it }
    )

    CenteredText(
        text = """To test it go to the Forth screen and return back. You can also rotate the 
            screen to test saved state restoration.""".singleLine(),
    )

    Button(onClick = toForthScreenButtonClick) {
        Text(stringResource(R.string.navhost_to_forth_screen_button))
    }
}

@Composable
private fun ForthScreen(
    resultFromFifth: String?,
    toFirstDialogButtonClick: () -> Unit,
    toFifthScreenButtonClick: () -> Unit,
    onClearResultClick: () -> Unit
) = SubScreenLayout(title = stringResource(R.string.navhost_forth_screen)) {

    CenteredText(
        text = "You can also use a separate DialogNavHost for managing dialogs.",
    )

    Button(onClick = toFirstDialogButtonClick) {
        Text(stringResource(R.string.navhost_to_first_dialog_button))
    }

    CenteredText(
        text = "To check out how you can return values from destinations go to the next screen.",
    )

    Button(onClick = toFifthScreenButtonClick) {
        Text(stringResource(R.string.navhost_to_fifth_screen_button))
    }

    if (resultFromFifth != null) {
        Text(stringResource(R.string.navhost_result_from_fifth, resultFromFifth))

        Button(onClick = onClearResultClick) {
            Text(stringResource(R.string.navhost_clear_result_from_fifth_button))
        }
    }

}

@Composable
private fun FirstDialogLayout(
    toSecondDialogButtonClick: () -> Unit
) {
    DialogLayout(title = stringResource(R.string.navhost_first_dialog)) {
        Button(onClick = toSecondDialogButtonClick) {
            Text(stringResource(R.string.navhost_to_second_dialog_button))
        }
    }
}

@Composable
private fun SecondDialogLayout() {
    DialogLayout(title = stringResource(R.string.navhost_second_dialog)) {
        Text("Hello!")
    }
}

@Composable
private fun FifthScreen(
    text: String,
    onTextChange: (String) -> Unit,
    returnToForthScreenButtonClick: () -> Unit,
    backToFirstScreenButtonClick: () -> Unit,
) = SubScreenLayout(title = stringResource(R.string.navhost_fifth_screen)) {

    CenteredText(
        text = "Here you can enter some text and pass it back to the previous screen.",
    )

    OutlinedTextField(
        modifier = Modifier.testTag(TestInputTag),
        value = text,
        onValueChange = onTextChange
    )

    Button(onClick = returnToForthScreenButtonClick) {
        Text(stringResource(R.string.navhost_return_to_forth_screen_button))
    }

    CenteredText(
        text = """Note: use it carefully. Mutable state increases the complexity of the backstack 
            logic. Sometimes it is more reasonable to have a hoisted data holder.""".singleLine(),
    )

    CenteredText(
        text = """Finally when you are done, you may go back to the very beginning.
            All previous screens will be removed from the backstack.
            """.singleLine(),
    )

    Button(onClick = backToFirstScreenButtonClick) {
        Text(stringResource(R.string.navhost_back_to_first_screen_button))
    }

}
