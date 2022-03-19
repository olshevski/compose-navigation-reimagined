package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.sample.R

@Composable
fun DemoSelectionScreen(
    onPassValuesButtonClick: () -> Unit,
    onReturnResultsButtonClick: () -> Unit,
    onAnimatedNavHostButtonClick: () -> Unit,
    onDialogNavHostButtonClick: () -> Unit,
    onViewModelsButtonClick: () -> Unit,
    onBottomNavigationButtonClick: () -> Unit
) = ScreenLayout(stringResource(R.string.demo_selection__screen_title)) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onPassValuesButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__pass_values_button))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onReturnResultsButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__return_results_button))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAnimatedNavHostButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__animated_nav_host_button))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDialogNavHostButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__dialog_nav_host_button))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onViewModelsButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__view_models_button))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onBottomNavigationButtonClick() }
        ) {
            Text(stringResource(R.string.demo_selection__bottom_navigation_button))
        }

    }

}