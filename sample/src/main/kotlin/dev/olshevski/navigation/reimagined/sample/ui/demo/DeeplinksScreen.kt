package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import kotlinx.parcelize.Parcelize

sealed class DeeplinksDestination : Parcelable {

    @Parcelize
    object First : DeeplinksDestination()

    @Parcelize
    object Second : DeeplinksDestination()

    @Parcelize
    data class Third(val id: String) : DeeplinksDestination()

}

@Composable
fun DeeplinksScreen(
    initialBackstack: List<DeeplinksDestination>
) = ScreenLayout(
    title = stringResource(R.string.deeplinks__demo_screen_title)
) {
    val navController = rememberNavController(initialBackstack)

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            DeeplinksDestination.First -> FirstScreen(
                onOpenSecondScreenButtonClick = {
                    navController.navigate(DeeplinksDestination.Second)
                }
            )
            DeeplinksDestination.Second -> SecondScreen(
                onOpenThirdScreenButtonClick = {
                    navController.navigate(DeeplinksDestination.Third("Hi"))
                }
            )
            is DeeplinksDestination.Third -> ThirdScreen(destination.id)
        }
    }

}

@Composable
private fun FirstScreen(
    onOpenSecondScreenButtonClick: () -> Unit
) = ContentLayout(
    title = stringResource(R.string.deeplinks__first_screen_title)
) {

    CenteredText(
        text = "Please visit the URL below to see all test deeplinks:",
    )

    val uriHandler = LocalUriHandler.current
    val uri = "https://olshevski.dev/deeplinks"
    ClickableText(
        text = AnnotatedString(uri),
        style = MaterialTheme.typography.body1.copy(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        )
    ) {
        uriHandler.openUri(uri)
    }

    CenteredText(
        text = """If the links are not opened in the app, please make sure that "Open supported
            links" is enabled in the application settings and "olshevski.dev" is a verified link.
            """.singleLine(),
    )

    Button(
        onClick = { onOpenSecondScreenButtonClick() }
    ) {
        Text(stringResource(R.string.deeplinks__open_second_screen_button))
    }
}

@Composable
private fun SecondScreen(
    onOpenThirdScreenButtonClick: () -> Unit
) = ContentLayout(
    title = stringResource(R.string.deeplinks__second_screen_title)
) {
    Button(
        onClick = { onOpenThirdScreenButtonClick() }
    ) {
        Text(stringResource(R.string.deeplinks__open_third_screen_button))
    }
}

@Composable
private fun ThirdScreen(
    id: String
) = ContentLayout(
    title = stringResource(R.string.deeplinks__third_screen_title, id)
)