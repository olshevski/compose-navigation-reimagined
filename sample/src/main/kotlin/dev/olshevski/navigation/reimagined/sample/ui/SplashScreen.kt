package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinishedShowing: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.h2,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    val currentOnFinishedShowing by rememberUpdatedState(onFinishedShowing)
    LaunchedEffect(Unit) {
        delay(2000)
        currentOnFinishedShowing()
    }
}