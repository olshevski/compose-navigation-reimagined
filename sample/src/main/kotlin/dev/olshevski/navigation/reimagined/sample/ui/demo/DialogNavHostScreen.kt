package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import dev.olshevski.navigation.reimagined.DialogNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceLast
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.DialogLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout

private enum class DialogDestination {
    First,
    Second
}

@Composable
fun DialogNavHostScreen() = ScreenLayout(
    title = stringResource(R.string.dialog_nav_host__demo_screen_title)
) {
    val navController = rememberNavController<DialogDestination>(
        initialBackstack = emptyList()
    )

    DialogNavHost(navController) { destination ->
        Dialog(onDismissRequest = { navController.pop() }) {
            when (destination) {
                DialogDestination.First -> FirstDialogLayout(
                    onOpenSecondDialogButtonClick = {
                        navController.replaceLast(
                            DialogDestination.Second
                        )
                    }
                )
                DialogDestination.Second -> SecondDialogLayout()
            }
        }
    }

    ContentLayout {
        CenteredText(
            text = "Use DialogNavHost to manage dialogs",
        )

        Button(onClick = { navController.navigate(DialogDestination.First) }) {
            Text(stringResource(R.string.dialog_nav_host__open_first_dialog_button))
        }
    }
}

@Composable
private fun FirstDialogLayout(
    onOpenSecondDialogButtonClick: () -> Unit
) = DialogLayout(
    title = stringResource(R.string.dialog_nav_host__first_dialog_title)
) {
    Button(onClick = onOpenSecondDialogButtonClick) {
        Text(stringResource(R.string.dialog_nav_host__open_second_dialog_button))
    }
}

@Composable
private fun SecondDialogLayout() = DialogLayout(
    title = stringResource(R.string.dialog_nav_host__second_dialog_title)
) {
    Text("Hello!")
}