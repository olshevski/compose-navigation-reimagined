# Overview

<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/234916580-69160e65-8329-41e8-974a-957e629f1376.svg" />
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
implementation("dev.olshevski.navigation:reimagined:1.5.0")
```

Define a set of destinations. It is convenient to use a sealed class for this:

```kotlin
sealed class Destination : Parcelable {

    @Parcelize
    data object First : Destination()

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