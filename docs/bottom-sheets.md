# Bottom sheets

Similar to dialogs, you may use BottomSheetNavHost to handle a backstack of bottom sheets alongside the backstack of screens.

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/199266283-a4e879fc-29d0-4f80-b86f-1d63118147f0.gif" />
</p>

To use it, you need to add the dependency:

```kotlin
// if you are using Material
implementation("dev.olshevski.navigation:reimagined-material:<latest-version>")

// if you are using Material 3
implementation("dev.olshevski.navigation:reimagined-material3:<latest-version>")
```

The usage would look like this:

```kotlin
@Composable
fun NavHostScreen() {
    val navController = rememberNavController<ScreenDestination>(
        startDestination = ScreenDestination.First,
    )

    val sheetController = rememberNavController<SheetDestination>(
        initialBackstack = emptyList()
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            ScreenDestination.First -> { /* ... */ }
            ScreenDestination.Second -> { /* ... */ }
        }
    }

    BackHandler(enabled = sheetController.backstack.entries.isNotEmpty()) {
        sheetController.pop()
    }

    BottomSheetNavHost(
        controller = sheetController,
        onDismissRequest = { sheetController.pop() }
    ) { destination ->
        Surface(
            elevation = ModalBottomSheetDefaults.Elevation
        ) {
            when (destination) {
                SheetDestination.First -> { /* ... */ }
                SheetDestination.Second -> { /* ... */ }
            }
        }
    }
}
```

BottomSheetNavHost is based on the source code of [ModalBottomSheetLayout](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#ModalBottomSheetLayout(kotlin.Function1,androidx.compose.ui.Modifier,androidx.compose.material.ModalBottomSheetState,androidx.compose.ui.graphics.Shape,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,kotlin.Function0)), but with some improvements for switching between multiple bottom sheets. The API also has some similarities.

!!! tip
    You can access current `sheetState` through the `BottomSheetNavHostScope` receiver of the `contentSelector` parameter.