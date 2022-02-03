package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AboutScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "About",
            style = MaterialTheme.typography.h2,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}