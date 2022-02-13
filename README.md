<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/153590758-1591f745-be66-42f5-bd1a-3ef3c5b2453c.svg" />
</p>

A small and simple, yet fully fledged and customizable navigation library for [Jetpack Compose](https://developer.android.com/jetpack/compose):

- Full **type-safety**
- State restoration
- Nested navigation with independent backstacks
- Own lifecycle, saved state and view models for every backstack entry
- Animated transitions
- Navigation logic may be easily moved to the ViewModel layer
- No builders, no obligatory superclasses for your composables
- May be used for managing dialogs


## Getting started

Add a single dependency to your project:

```kotlin
implementation("dev.olshevski.navigation:reimagined:1.0.0-beta02")
```

Define a set of screens, for example, as a sealed class:

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

Create a composable with `NavController` and `NavHost`:

```kotlin
@Composable
fun NavHostScreen() {
    val navController = rememberNavController<Screen>(
        startDestination = Screen.First,
    )

    NavBackHandler(navController)

    NavHost(controller = navController) { screen ->
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

As you can see, `NavController` is used for switching between screens, `NavBackHandler` handles the back presses and `NavHost` simply provides a composable corresponding to the latest destination in the backstack. As simple as that.

## Basics

Here is the general workflow of the library:

<p align="center">
    <img width="700" src="https://user-images.githubusercontent.com/5606565/152329249-055868e6-beff-4f1f-91e3-9a931827215e.svg" />
</p>

Let's go into details about each of them.

### NavController

This is the main control point of navigation. It keeps record of all current backstack entries and preserves them on activity/process recreation.

NavController may be created with `rememberNavController` within composition or with `navController` outside of it. The latter may be used for storing NavController in a ViewModel. As it implements Parcelable interface, it may be (and should be) stored in [SavedStateHandle](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle).

Both `rememberNavController` and `navController` methods accept `startDestination` as a parameter. If you want to create NavController with an arbitrary number of backstack items, you may use `initialBackstack` parameter instead.

#### Destinations

NavController accepts all types meeting the requirements as destinations. The requirements are:

1. The type must be either Parcelable, or Serializable, or primitive, or of any other type that can be written to [Parcel](https://developer.android.com/reference/android/os/Parcel).

2. The type must be either [Stable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Stable), or [Immutable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable), or primitive.

#### Navigation methods

There is a handful of pre-defined methods suitable for a basic app navigation: `navigate`, `pop`, `popUpTo`, `popAll`, `replaceLast`, `replaceUpTo`, `replaceAll`. They all are pretty much self-explanatory.

If your use-case calls for some advanced backstack manipulations, you may use `setNewBackstackEntries` method. In fact, this is the only public method defined in NavController, all other methods are provided as extensions and use `setNewBackstackEntries` under the hood. You may see how a new extension method `navigateToTab` is implemented in the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/master/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/TabsScreen.kt).


### NavBackstack

This is a read-only class that you may use to access current backstack entries and the last NavAction. The properties are backed up by [MutableState](https://developer.android.com/reference/kotlin/androidx/compose/runtime/MutableState), so Compose will be notified about the changes.

If you want to listen for backstack changes outside of composition you may set `onBackstackChange` listener in NavController.

### NavHost

NavHost is a composable that shows the last entry of a backstack and provides all components associated with this particular entry: [Lifecycle](https://developer.android.com/reference/androidx/lifecycle/Lifecycle), [SavedStateRegistry](https://developer.android.com/reference/androidx/savedstate/SavedStateRegistry) and [ViewModelStore](https://developer.android.com/reference/androidx/lifecycle/ViewModelStore). All these components are provided through [CompositionLocalProvider](https://developer.android.com/jetpack/compose/compositionlocal) inside the corresponding owners `LocalLifecycleOwner`, `LocalSavedStateRegistryOwner` and `LocalViewModelStoreOwner`.

The components are kept around until its associated entry is removed from the backstack (or until the parent entry containing the current child NavHost is removed).

NavHost by itself doesn't provide any animated transitions, it simply jump-cuts to the next destination.

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329130-7a90c412-197a-4930-baac-8af81ef16fee.gif" />
</p>

#### AnimatedNavHost

AnimatedNavHost includes all functionality of the regular NavHost, but also supports animated transitions. Default transition is a simple crossfade, but you can granularly customize every transition with your own `AnimatedNavHostTransitionSpec` implementation.

Here is one possible implementation of AnimatedNavHostTransitionSpec:

```kotlin
val CustomTransitionSpec = AnimatedNavHostTransitionSpec<Any?> { action, from, to ->
    val direction = if (action == NavAction.Pop) {
        AnimatedContentScope.SlideDirection.End
    } else {
        AnimatedContentScope.SlideDirection.Start
    }
    slideIntoContainer(direction) with slideOutOfContainer(direction)
}
```

Set it into AnimatedNavHost:

```kotlin
AnimatedNavHost(
    controller = navController,
    transitionSpec = CustomTransitionSpec
) { destination ->
    // ...
}
```

and it'll end up looking like this:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329115-827e073e-c59d-4793-9f03-f9f684037a28.gif" />
</p>

In AnimatedNavHostTransitionSpec you get the parameters:
- `action` - the hint about the last NavController method that changed the backstack
- `from` - the previous visible destination
- `to` - the target visible destination

This information is plenty enough to choose a transition for every possible combination of screens and navigation actions.

#### NavAction

There are four default NavAction types:
- `Pop`, `Replace` and `Navigate` - objects that correspond to `pop…`, `replace…`, `navigate` methods of NavController
- `Idle` - the default action of a newly created NavController

You can also create a new action type by extending abstract `NavAction` class. Pass this new type into `setNewBackstackEntries` method of NavController and handle it in AnimatedNavHostTransitionSpec.

The last action can be also accessed through `action` property of NavBackstack.

#### DialogNavHost

The version of NavHost that is better suited for showing dialogs. It is based on AnimatedNavHost and provides smoother transition between dialogs without scrim/fade flickering.

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329122-b1631692-8b38-4397-a81a-dad5bbfa85e7.gif" />
</p>

If you want to see how you can implement dialogs navigation explore the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/master/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/tabs/NavHostScreen.kt).

Note that DialogNavHost doesn't wrap your composables in a Dialog. You need to use use either Dialog or AlertDialog composable inside a `contentSelector` yourself.

### Back handling

Back handling in the library is opt-in, rather than opt-out. By itself, neither NavController nor NavHost handles the back button press. You can add `NavBackHandler` or usual `BackHandler` in order to react to the back presses where you need to.

NavBackHandler is the most basic implementation of BackHandler that calls `pop` until one item in the backstack is left. Then it is disabled, so any upper-level BackHandler may react to the back button press.

**Important note:** always place your NavBackHandler/BackHandler **before** the corresponding NavHost. Read the explanation [here](https://github.com/olshevski/compose-navigation-reimagined/blob/master/reimagined/src/main/kotlin/dev/olshevski/navigation/reimagined/NavBackHandler.kt).

### Nested navigation

Adding nested navigation is as simple as placing one NavHost into another. Everything is handled correctly and just works.

You may go as many layers deep as you want. It's like [fractals](https://en.wikipedia.org/wiki/Fractal), but in navigation.

### Return values to previous destinations

As destination types are not strictly required to be Immutable, you may change them while they are in the backstack. This may be used for returning values from other destinations. Just make a mutable property backed up by `mutableStateOf` and change it when required. You may see the demo [here](https://github.com/olshevski/compose-navigation-reimagined/blob/master/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/tabs/NavHostScreen.kt).

**Note:** In general, returning values to the previous destination makes the navigation logic more complicated. Also, this approach doesn't guarantee compile time type-safety. Use it with caution and when you are sure what you are doing. Sometimes it may be easier to use a shared state holder. 

## Documentation and sample

Explore the KDoc documentation of the library for more details about every component and every supported features.

Also, explore the sample. It provides demos of all the functionality mentioned above and even more. The sample shows:

- nested navigation
- tab navigation
- NavHost/AnimatedNavHost usage
- dialogs
- passing and returning values
- ViewModels
- hoisting NavController to the ViewModel layer

## Why beta

I'm very satisfied with the shape and form of the library. I have spent long sleepless nights debugging and polishing all corner cases.

For now I'll be glad to hear a feedback and do a minor fine-tunings of the API (if any at all). If there are any changes you may expect a notice in release notes.

## About

I've been thinking about Android app architecture and navigation in particular for the longest time. When introduced to Compose I could finally create the navigation structure that fits perfectly all my needs.

Making it in the form of a public library closes a gestalt for me. I'm finally done with it. Onto new projects!

*If you like this library and find it useful, please star the project and share it with your fellow developers. A little bit of promotion never hurts.*
