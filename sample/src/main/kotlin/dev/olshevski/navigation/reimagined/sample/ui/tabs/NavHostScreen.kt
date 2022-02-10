package dev.olshevski.navigation.reimagined.sample.ui.tabs

import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import dev.olshevski.navigation.reimagined.DialogNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popUpTo
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceLast
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.DialogLayout
import dev.olshevski.navigation.reimagined.sample.ui.SubScreenLayout

@Composable
fun NavHostScreen() {
    val navController = rememberNavController<NavHostDestination>(
        startDestination = NavHostDestination.First,
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
                    backToForthScreenButtonClick = {
                        val previousDestination = navController.backstack.entries.let {
                            it[it.lastIndex - 1].destination
                        }
                        check(previousDestination is AcceptsResultFromFifth)
                        previousDestination.resultFromFifth.value = text
                        navController.pop()
                    },
                    goBackButtonClick = {
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
) = SubScreenLayout(title = "First screen") {

    Text(
        text = """NavHost switches between destinations without any animations.
                Go to Second screen and see how it works.""".singleLine(),
        textAlign = TextAlign.Center
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text("To Second screen")
    }

}

@Composable
private fun SecondScreen(
    id: Int,
    toSecondScreenButtonClick: () -> Unit,
    toThirdScreenButtonClick: () -> Unit
) = SubScreenLayout(title = "Second screen id=$id") {
    Text(
        text = """You can pass any serializable/parcelable data you want. Here you
            can keep opening more Second screens with incrementing 'id' parameter.
            """.singleLine(),
        textAlign = TextAlign.Center
    )

    Button(onClick = toSecondScreenButtonClick) {
        Text("To Second screen + 1")
    }

    Text(
        text = "Also try pressing back. Or just go to the third screen.",
        textAlign = TextAlign.Center
    )

    Button(onClick = toThirdScreenButtonClick) {
        Text("To Third screen")
    }
}

@Composable
private fun ThirdScreen(
    toForthScreenButtonClick: () -> Unit
) = SubScreenLayout(title = "Third screen") {
    Text(
        text = "Now enter some text. This text will be saved while the screen is in the backstack.",
        textAlign = TextAlign.Center
    )

    var text by rememberSaveable { mutableStateOf("") }
    OutlinedTextField(value = text, onValueChange = { text = it })

    Text(
        text = """To test it go to the Forth screen and return back. You can also rotate the 
            screen to test saved state restoration.""".singleLine(),
        textAlign = TextAlign.Center
    )

    Button(onClick = toForthScreenButtonClick) {
        Text("To Forth screen")
    }
}

@Composable
private fun ForthScreen(
    resultFromFifth: String?,
    toFirstDialogButtonClick: () -> Unit,
    toFifthScreenButtonClick: () -> Unit,
    onClearResultClick: () -> Unit
) = SubScreenLayout(title = "Forth screen") {

    Text(
        text = "You can also use a separate DialogNavHost for managing dialogs.",
        textAlign = TextAlign.Center
    )

    Button(onClick = toFirstDialogButtonClick) {
        Text("To First dialog")
    }

    Text(
        text = "To check out how you can return values from destinations go to the next screen.",
        textAlign = TextAlign.Center
    )

    Button(onClick = toFifthScreenButtonClick) {
        Text("To Fifth screen")
    }

    if (resultFromFifth != null) {
        Text("Result from fifth: $resultFromFifth")

        Button(onClick = onClearResultClick) {
            Text("Clear result")
        }
    }

}

@Composable
private fun FirstDialogLayout(
    toSecondDialogButtonClick: () -> Unit
) {
    DialogLayout(title = "First dialog") {
        Button(onClick = toSecondDialogButtonClick) {
            Text("To Second dialog")
        }
    }
}

@Composable
private fun SecondDialogLayout() {
    DialogLayout(title = "Second dialog") {
        Text("Hello!")
    }
}

@Composable
private fun FifthScreen(
    text: String,
    onTextChange: (String) -> Unit,
    backToForthScreenButtonClick: () -> Unit,
    goBackButtonClick: () -> Unit,
) = SubScreenLayout(title = "Fifth screen") {

    Text(
        text = "Here you can enter some text and pass it back to the previous screen.",
        textAlign = TextAlign.Center
    )

    OutlinedTextField(value = text, onValueChange = onTextChange)

    Button(onClick = backToForthScreenButtonClick) {
        Text("Return result to Forth screen")
    }

    Text(
        text = """Note: use it carefully. Mutable state increases the complexity of the backstack 
            logic. Sometimes it is more reasonable to have a hoisted data holder.""".singleLine(),
        textAlign = TextAlign.Center
    )

    Text(
        text = """Finally when you are done, you may go back to the very beginning.
            All previous screens will be removed from the backstack.
            """.singleLine(),
        textAlign = TextAlign.Center
    )

    Button(onClick = goBackButtonClick) {
        Text("Go back to First screen")
    }

}
