package dev.olshevski.navigation.reimagined.sample.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.sample.singleLine

@Preview
@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = """This is a simple implementation of BottomNavigation with same backstack logic
                as in the official Youtube app.""".singleLine(),
            textAlign = TextAlign.Center
        )

        Text(
            text = """Here every destination appears in the backstack only once and preserves
                its saved state until explicitly popped off the backstack. Also, the home
                destination is always the last one to be closed by the back button.""".singleLine(),
            textAlign = TextAlign.Center
        )

        Text(
            text = """Every screen has its own nested navigation with its own back handling.
                The back handling of a nested navigation always takes precedence over
                the BottomNavigation back handling.
                """.singleLine(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Check out other screens for more supported features.",
            textAlign = TextAlign.Center
        )
    }
}