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
    onScopedViewModelsButtonClick: () -> Unit,
    onBottomNavigationButtonClick: () -> Unit,
    onDeeplinksButtonClick: () -> Unit,
    onModalBottomSheetClick: () -> Unit
) = ScreenLayout(stringResource(R.string.demo_selection__screen_title)) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            R.string.demo_selection__pass_values_button to onPassValuesButtonClick,
            R.string.demo_selection__return_results_button to onReturnResultsButtonClick,
            R.string.demo_selection__animated_nav_host_button to onAnimatedNavHostButtonClick,
            R.string.demo_selection__dialog_nav_host_button to onDialogNavHostButtonClick,
            R.string.demo_selection__view_models_button to onViewModelsButtonClick,
            R.string.demo_selection__scoped_view_models_button to onScopedViewModelsButtonClick,
            R.string.demo_selection__bottom_navigation_button to onBottomNavigationButtonClick,
            R.string.demo_selection__deeplinks_button to onDeeplinksButtonClick,
            R.string.demo_selection__bottom_sheet_button to onModalBottomSheetClick
        ).forEach {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Max),
                onClick = { it.second() }
            ) {
                Text(
                    text = stringResource(it.first)
                )
            }
        }
    }

}