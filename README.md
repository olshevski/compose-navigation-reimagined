<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/234915659-afe98551-5439-4381-aab5-5147fcb8e079.svg" />
</p>

A small and simple, yet fully fledged and customizable navigation library for [Jetpack Compose](https://developer.android.com/jetpack/compose):

- Full **type-safety**
- Built-in state restoration
- Nested navigation with independent backstacks
- Own Lifecycle, ViewModelStore and SavedStateRegistry for every backstack entry
- Animated transitions
- Dialog and bottom sheet navigation
- Scopes for easier ViewModel sharing 
- No builders, no obligatory superclasses for your composables

## Quick start

Add a single dependency to your project:

```kotlin
implementation("dev.olshevski.navigation:reimagined:1.4.0")
```

Define a set of destinations. It is convenient to use a sealed class for this:

```kotlin
sealed class Destination : Parcelable {

    @Parcelize
    object First : Destination()

    @Parcelize
    data class Second(val id: Int) : Destination()

    @Parcelize
    data class Third(val text: String) : Destination()

}
```

Create a composable with `NavController`, `NavBackHandler` and `NavHost`:

```kotlin
@Composable
fun NavHostScreen() {
    val navController = rememberNavController<Destination>(
        startDestination = Destination.First
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            is Destination.First -> Column {
                Text("First destination")
                Button(onClick = {
                    navController.navigate(Destination.Second(id = 42))
                }) {
                    Text("Open Second destination")
                }
            }

            is Destination.Second -> Column {
                Text("Second destination: ${destination.id}")
                Button(onClick = {
                    navController.navigate(Destination.Third(text = "Hello"))
                }) {
                    Text("Open Third destination")
                }
            }

            is Destination.Third -> {
                Text("Third destination: ${destination.text}")
            }
        }
    }
}
```

As you can see, `NavController` is used for switching between destinations, `NavBackHandler` handles back presses and `NavHost` provides a composable corresponding to the last destination in the backstack. As simple as that.

### What about animations?

Just replace `NavHost` with `AnimatedNavHost`. The default transition between destinations is a simple crossfade, but you can customize each transition with the `transitionSpec` parameter:

```kotlin
AnimatedNavHost(
    controller = navController,
    transitionSpec = { action, _, _ ->
        val direction = if (action == NavAction.Pop) {
            AnimatedContentTransitionScope.SlideDirection.End
        } else {
            AnimatedContentTransitionScope.SlideDirection.Start
        }
        slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
    }
) { screen ->
    // ...
}
```

## Documentation

Full documentation is available [here](https://olshevski.github.io/compose-navigation-reimagined).

## Additional dependencies

Library-specific `hiltViewModel()` implementation:

```koltin
implementation("dev.olshevski.navigation:reimagined-hilt:<latest-version>")
```

`BottomSheetNavHost` implementation:

```koltin
// if you are using Material
implementation("dev.olshevski.navigation:reimagined-material:<latest-version>")

// if you are using Material 3
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

*If you like this library and find it useful, please star the project and share it with your fellow developers. You can also buy me a coffee:*

<p align="center"><a href="https://www.buymeacoffee.com/olshevski" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" height="60px"></a></p>
