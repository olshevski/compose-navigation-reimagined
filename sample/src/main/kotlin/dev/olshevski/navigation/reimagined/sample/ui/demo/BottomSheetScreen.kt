package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHostScope
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import kotlinx.coroutines.launch

@Composable
fun BottomSheetScreen() {
    val navController = rememberNavController<BottomSheetDestination>(
        initialBackstack = emptyList()
    )

    BackHandler(navController.backstack.entries.isNotEmpty()) {
        navController.pop()
    }

    Box {
        ScreenLayout(
            title = "BottomSheet Demo"
        ) {
            Button(
                onClick = {
                    navController.navigate(BottomSheetDestination.First)
                }
            ) {
                Text("Open First sheet")
            }
        }

        BottomSheetNavHost(
            backstack = navController.backstack,
            onDismissRequest = { navController.pop() }
        ) { destination ->
            when (destination) {
                BottomSheetDestination.First -> FirstBottomSheet(
                    toSecondSheetClick = {
                        navController.navigate(BottomSheetDestination.Second)
                    }
                )
                BottomSheetDestination.Second -> SecondBottomSheet(
                    toThirdSheetClick = {
                        navController.navigate(BottomSheetDestination.Third)
                    }
                )
                BottomSheetDestination.Third -> ThirdBottomSheet(
                    onCloseAllClick = {
                        navController.popAll()
                    }
                )
            }
        }
    }
}

@Composable
private fun FirstBottomSheet(
    toSecondSheetClick: () -> Unit
) = Box(
    modifier = Modifier.height(200.dp)
) {
    Button(
        onClick = toSecondSheetClick
    ) {
        Text("Open Second sheet")
    }
}

@Composable
private fun BottomSheetNavHostScope<BottomSheetDestination>.SecondBottomSheet(
    toThirdSheetClick: () -> Unit
) = Column(
    modifier = Modifier.fillMaxHeight()
) {
    Button(
        onClick = toThirdSheetClick
    ) {
        Text("Open Third sheet")
    }
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            scope.launch {
                this@SecondBottomSheet.sheetState.expand()
            }
        }
    ) {
        Text("Expand")
    }
    Button(
        onClick = {
            scope.launch {
                this@SecondBottomSheet.sheetState.halfExpand()
            }
        }
    ) {
        Text("Half expand")
    }
}

@Composable
private fun ThirdBottomSheet(
    onCloseAllClick: () -> Unit
) = Box(
    modifier = Modifier.height(200.dp)
) {
    Button(
        onClick = onCloseAllClick
    ) {
        Text("Close all sheets")
    }
}