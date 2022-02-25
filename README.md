<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/153590758-1591f745-be66-42f5-bd1a-3ef3c5b2453c.svg" />
</p>

A small and simple, yet fully fledged and customizable navigation library for [Jetpack Compose](https://developer.android.com/jetpack/compose):

- Full **type-safety**
- State restoration
- Nested navigation with independent backstacks
- Easy integration with [BottomNavigation](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#BottomNavigation(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,kotlin.Function1)) and [TabRow](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TabRow(kotlin.Int,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,kotlin.Function1,kotlin.Function0,kotlin.Function0))
- Own lifecycle, saved state and view models for every backstack entry
- Animated transitions
- Navigation logic may be easily moved to the ViewModel layer
- No builders, no obligatory superclasses for your composables

## Quick start

Add a single dependency to your project:

```kotlin
implementation("dev.olshevski.navigation:reimagined:1.0.0")
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
            Screen.First -> Column {
                Text("First screen")
                Button(onClick = {
                    navController.navigate(Screen.Second(id = 42))
                }) {
                    Text("To Second screen")
                }
            }

            is Screen.Second -> Column {
                Text("Second screen: ${screen.id}")
                Button(onClick = {
                    navController.navigate(Screen.Third(text = "Hello"))
                }) {
                    Text("To Third screen")
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

## Sample

Explore the [sample](https://github.com/olshevski/compose-navigation-reimagined/tree/main/sample). It demonstrates:

- nested navigation
- BottomNavigation
- NavHost/AnimatedNavHost usage
- passing values and returning results
- dialog navigation
- entry-scoped ViewModels
- usage of NavController within the ViewModel layer

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/155094899-7cb20a5a-c5e4-4235-8fe0-e22218ddef35.gif" />
</p>

## About

I've been thinking about Android app architecture and navigation in particular for the longest time. After being introduced to Compose I could finally create the navigation structure that satisfies all my needs perfectly.

I hope it can help you as well.

<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/153843642-7eb6252f-cabc-4f80-9377-4c66567c98a4.svg" />
</p>

*If you like this library and find it useful, please star the project and share it with your fellow developers. A little bit of promotion never hurts.*

## License

```
MIT License

Copyright (c) 2022 Vitali Olshevski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```