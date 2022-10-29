# Dialogs

If you need to handle a backstack of dialogs in your application, simply add DialogNavHost to the same composition layer where your regular NavHost lives. This way you may show and control the backstack of regular screen destinations, as well as a second backstack of dialogs:

```kotlin
@Composable
fun NavHostScreen() {
    val navController = rememberNavController<ScreenDestination>(
        startDestination = ScreenDestination.First,
    )

    val dialogController = rememberNavController<DialogDestination>(
        initialBackstack = emptyList()
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            ScreenDestination.First -> { /* ... */ }
            ScreenDestination.Second -> { /* ... */ }
        }
    }

    DialogNavHost(dialogController) { destination ->
        Dialog(onDismissRequest = { dialogController.pop() }) {
            when (destination) {
                DialogDestination.First -> { /* ... */ }
                DialogDestination.Second -> { /* ... */ }
            }
        }
    }
}
```

DialogNavHost is an alternative version of NavHost that is better suited for showing dialogs. It is based on AnimatedNavHost and provides smoother transition between dialogs without scrim flickering:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329122-b1631692-8b38-4397-a81a-dad5bbfa85e7.gif" />
</p>

And this is how it looks in the regular NavHost:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/155152679-d8e8ee0a-85a8-4254-8091-b7b18ba83707.gif" />
</p>

!!! note
    DialogNavHost doesn't wrap your composables into a Dialog. You need to use either Dialog or AlertDialog composable inside a `contentSelector` yourself.