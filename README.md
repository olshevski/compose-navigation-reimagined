<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/153590758-1591f745-be66-42f5-bd1a-3ef3c5b2453c.svg" />
</p>

A small and simple, yet fully fledged and customizable navigation library for [Jetpack Compose](https://developer.android.com/jetpack/compose):

- Full **type-safety**
- Built-in state restoration
- Nested navigation with independent backstacks
- Own Lifecycle, ViewModelStore and SavedStateRegistry for every backstack entry
- Animated transitions
- Dialog and bottom sheet navigation
- Ability to define scopes for easy sharing of ViewModels
- No builders, no obligatory superclasses for your composables

## Quick start

Add a single dependency to your project:

```kotlin
implementation("dev.olshevski.navigation:reimagined:1.4.0")
```

Define a set of screens. It is convenient to use a sealed class for this:

```kotlin
sealed class Screen : Parcelable {

    @Parcelize
    object First : Screen()

    @Parcelize
    data class Second(val id: Int) : Screen()

    @Parcelize
    data class Third(val text: String) : Screen()

}
```

Create a composable with `NavController`, `NavBackHandler` and `NavHost`:

```kotlin
@Composable
fun NavHostScreen() {
    val navController = rememberNavController<Screen>(
        startDestination = Screen.First
    )

    NavBackHandler(navController)

    NavHost(navController) { screen ->
        when (screen) {
            is Screen.First -> Column {
                Text("First screen")
                Button(onClick = {
                    navController.navigate(Screen.Second(id = 42))
                }) {
                    Text("Open Second screen")
                }
            }

            is Screen.Second -> Column {
                Text("Second screen: ${screen.id}")
                Button(onClick = {
                    navController.navigate(Screen.Third(text = "Hello"))
                }) {
                    Text("Open Third screen")
                }
            }

            is Screen.Third -> {
                Text("Third screen: ${screen.text}")
            }
        }
    }
}
```

As you can see, `NavController` is used for switching between screens, `NavBackHandler` handles back presses and `NavHost` provides a composable corresponding to the last destination in the backstack. As simple as that.

## Documentation

Full documentation is available [here](https://olshevski.github.io/compose-navigation-reimagined).

## Additional dependencies

Library-specific `hiltViewModel()` implementation:

```koltin
implementation("dev.olshevski.navigation:reimagined-hilt:<latest-version>")
```

`BottomSheetNavHost` implementation:

```koltin
implementation("dev.olshevski.navigation:reimagined-material:<latest-version>")
```

`BottomSheetNavHost`, but only with Material 3 dependencies:

```koltin
implementation("dev.olshevski.navigation:reimagined-material3:<latest-version>")
```

## Sample

Explore the [sample](https://github.com/olshevski/compose-navigation-reimagined/tree/main/sample). It demonstrates:

- passing values and returning results
- animated transitions
- dialog and bottom sheet navigation
- nested navigation
- [BottomNavigation](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#BottomNavigation(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,kotlin.Function1)) integration
- entry-scoped and shared ViewModels
- hoisting of NavController to the ViewModel layer
- deeplinks

## About

I've been thinking about Android app architecture and navigation in particular for the longest time. After being introduced to Compose I could finally create the navigation structure that satisfies all my needs perfectly.

I hope it can help you as well.

<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/227801130-39bee5cf-cf75-47c1-8791-f7753b5c7c0d.svg" />
</p>

*If you like this library and find it useful, please star the project and share it with your fellow developers. You can also [buy me a coffee](https://www.buymeacoffee.com/olshevski).*
