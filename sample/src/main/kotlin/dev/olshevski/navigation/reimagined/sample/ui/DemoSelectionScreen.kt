package dev.olshevski.navigation.reimagined.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.sample.R

@Composable
fun DemoSelectionScreen(
    onDemoSelected: (MainDestination) -> Unit
) = ScreenLayout(stringResource(R.string.demo_selection__screen_title)) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            listOf(
                R.string.demo_selection__pass_values_button to MainDestination.PassValues,
                R.string.demo_selection__return_results_button to MainDestination.ReturnResults,
                R.string.demo_selection__animated_nav_host_button to MainDestination.AnimatedNavHost,
                R.string.demo_selection__dialog_nav_host_button to MainDestination.DialogNavHost,
                R.string.demo_selection__bottom_sheet_nav_host_button to MainDestination.BottomSheetNavHost,
                R.string.demo_selection__bottom_navigation_button to MainDestination.BottomNavigation,
                R.string.demo_selection__view_models_button to MainDestination.ViewModels,
                R.string.demo_selection__state_view_models_button to MainDestination.StateViewModels,
                R.string.demo_selection__scoped_view_models_button to MainDestination.ScopedViewModels,
                R.string.demo_selection__deeplinks_button to MainDestination.Deeplinks(),
                R.string.demo_selection__better_dialog_transitions to MainDestination.BetterDialogTransitions
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDemoSelected(it.second) },
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    text = stringResource(it.first),
                    style = MaterialTheme.typography.subtitle1,
                )
                Divider()
            }
        }
    }

}