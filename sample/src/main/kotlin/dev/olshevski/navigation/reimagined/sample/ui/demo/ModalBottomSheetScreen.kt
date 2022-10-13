package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout

@Composable
fun ModalBottomSheetScreen() {
    val navController = rememberNavController<ModalBottomSheetDestination>(
        initialBackstack = emptyList()
    )

    BackHandler(navController.backstack.entries.isNotEmpty()) {
        navController.pop()
    }

    Box {
        ScreenLayout(
            title = "ModalBottomSheet Demo"
        ) {
            Button(
                onClick = {
                    navController.navigate(ModalBottomSheetDestination.First)
                }
            ) {
                Text("Open First sheet")
            }
        }

        BottomSheetNavHost(
            modifier = Modifier.fillMaxSize(),
            backstack = navController.backstack,
            onDismissRequest = { navController.pop() },
        ) { destination ->
            when (destination) {
                ModalBottomSheetDestination.First -> FirstBottomSheet(
                    toSecondSheetClick = {
                        navController.navigate(ModalBottomSheetDestination.Second)
                    }
                )
                ModalBottomSheetDestination.Second -> SecondBottomSheet(
                    toThirdSheetClick = {
                        navController.navigate(ModalBottomSheetDestination.Third)
                    }
                )
                ModalBottomSheetDestination.Third -> ThirdBottomSheet(
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
private fun SecondBottomSheet(
    toThirdSheetClick: () -> Unit
) = Box(
    modifier = Modifier.height(200.dp)
) {
    Button(
        onClick = toThirdSheetClick
    ) {
        Text("Open Third sheet")
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