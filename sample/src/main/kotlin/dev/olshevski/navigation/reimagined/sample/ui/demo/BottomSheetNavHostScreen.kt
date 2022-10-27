package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHostScope
import dev.olshevski.navigation.reimagined.material.BottomSheetValue
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.BottomSheetLayout
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import kotlinx.coroutines.launch

@Composable
fun BottomSheetNavHostScreen() = Box {
    val navController = rememberNavController<BottomSheetNavHostDestination>(
        initialBackstack = emptyList()
    )

    BackHandler(navController.backstack.entries.isNotEmpty()) {
        navController.pop()
    }

    ScreenLayout(
        title = stringResource(R.string.bottom_sheet_nav_host__demo_screen_title)
    ) {
        ContentLayout {
            CenteredText(
                text = "Use BottomSheetNavHost to open and switch between bottom sheets",
            )

            Button(
                onClick = {
                    navController.navigate(BottomSheetNavHostDestination.First)
                }
            ) {
                Text(stringResource(R.string.bottom_sheet_nav_host__open_first_sheet_button))
            }
        }
    }

    BottomSheetNavHost(
        backstack = navController.backstack,
        onDismissRequest = { navController.pop() }
    ) { destination ->
        when (destination) {
            BottomSheetNavHostDestination.First -> FirstBottomSheet(
                onOpenSecondSheetClick = {
                    navController.navigate(BottomSheetNavHostDestination.Second)
                }
            )
            BottomSheetNavHostDestination.Second -> SecondBottomSheet(
                onCloseClick = {
                    navController.pop()
                },
                onCloseAllClick = {
                    navController.popAll()
                }
            )
        }
    }
}

@Composable
private fun FirstBottomSheet(
    onOpenSecondSheetClick: () -> Unit
) = BottomSheetLayout(
    title = stringResource(R.string.bottom_sheet_nav_host__first_sheet_title),
) {
    CenteredText(
        text = "BottomSheetNavHost provides all the functionality of a regular NavHost"
    )

    Button(
        onClick = onOpenSecondSheetClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__open_second_sheet_button))
    }
}

@Composable
private fun BottomSheetNavHostScope<BottomSheetNavHostDestination>.SecondBottomSheet(
    onCloseClick: () -> Unit,
    onCloseAllClick: () -> Unit
) = BottomSheetLayout(
    title = stringResource(R.string.bottom_sheet_nav_host__second_sheet_title),
    modifier = Modifier.fillMaxHeight()
) {
    val scope = rememberCoroutineScope()
    val isExpanded = sheetState.currentValue == BottomSheetValue.Expanded

    CenteredText(
        text = """You can swipe this bottom sheet to close it or fully expand. Alternatively, you 
            can control its state programmatically.""".singleLine()
    )

    Button(
        onClick = {
            scope.launch {
                if (isExpanded) {
                    this@SecondBottomSheet.sheetState.halfExpand()
                } else {
                    this@SecondBottomSheet.sheetState.expand()
                }
            }
        }
    ) {
        if (isExpanded) {
            Text(stringResource(R.string.bottom_sheet_nav_host__half_expand_sheet_button))
        } else {
            Text(stringResource(R.string.bottom_sheet_nav_host__expand_sheet_button))
        }
    }

    CenteredText(
        text = "To close a bottom sheet you need to remove its entry from NavController"
    )

    Button(
        onClick = onCloseClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__close_sheet_button))
    }

    CenteredText(
        text = "Or even remove all entries"
    )

    Button(
        onClick = onCloseAllClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__close_all_sheets_button))
    }
}