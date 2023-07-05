# NavController

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/198884509-70f9b031-3a75-4835-99a2-1b3f6f8423f2.svg" />
</p>

This is the main control point of navigation. It keeps record of all current backstack entries and preserves them on activity/process recreation.

NavController may be created with `rememberNavController` method in a composable function or with `navController` outside of composition. The latter may be used for storing NavController in a ViewModel. As it implements Parcelable interface, it could be stored in a [SavedStateHandle](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle).

Both `rememberNavController` and `navController` methods accept `startDestination` as a parameter:

```kotlin
val navController = rememberNavController<Destination>(
    startDestination = Destination.First
)
```

If you want to create NavController with an arbitrary number of backstack items, you may use `initialBackstack` parameter instead:

```kotlin
val navController = rememberNavController<Destination>(
    initialBackstack = listOf(Destination.First, Destination.Second, Destination.Third)
)
```

`Destination.Third` will become the currently displayed item. `Destination.First` and `Destination.Second` will be stored in the backstack.

If you want to store NavController in a ViewModel use `saveable` delegate for SavedStateHandle:

```kotlin
class NavigationViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val navController by savedStateHandle.saveable<NavController<Destination>> {
        navController(startDestination = Destination.First)
    }

}
```

## Destinations

NavController accepts all types that meet the requirements as destinations:

1. The type must be writable to [Parcel](https://developer.android.com/reference/android/os/Parcel) - it could be Parcelable, Serializable, string/primitive, or other supported type.

2. The type must be [Stable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Stable), [Immutable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable), or string/primitive type.

Other than that, you are not limited to any particular type.

!!! tip
    It is very convenient to define your set of destinations as a sealed class or enum. This way you will always be notified by the compiler that you have a non-exhaustive `when` statement if you add a new destination.

!!! tip
    You may also define your own base interface for destinations, for example:
 
    ```kotlin
    interface Destination : Parcelable {
        
        @Composable
        fun Content()
    
    }
    ```

    This way you may handle each destinations without checking its instance:

    ```kotlin
    NavHost(navController) { it.Content() }
    ```

## NavEntry

In order to be passed into NavController, each destination should be wrapped into NavEntry. It contains a unique identifier which is used to properly preserve saved state and manage Android architecture components (Lifecycle, ViewModelStore and SavedStateRegistry) for each such entry inside [NavHost](/compose-navigation-reimagined/nav-host/).

Saved state and view models of each entry are guaranteed to be preserved for as long as the associated entry is present in the backstack.

!!! note
    If you add two equal destinations to the backstack, wrapped into two different entries, they will get their own separate identities, saved states and components. However, it is possible to put same exact entry instance into the backstack and it will be correctly treated as the same entry.

## Navigation methods

There is a handful of pre-defined methods suitable for basic app navigation: `navigate`, `moveToTop`, `pop`, `popUpTo`, `popAll`, `replaceLast`, `replaceUpTo`, `replaceAll`. They all are pretty much self-explanatory, except maybe `moveToTop`.

`moveToTop` method searches for some particular destination in the backstack and moves it to the top, effectively making it the currently displayed destination. This is particularly useful for integration with BottomNavigation/TabRow, when you want to always keep a single instance of every destination in the backstack.

The method is expected to be used in pair with `navigate`:

```kotlin
if (!navController.moveToTop { it is SomeDestination }) {
    // navigate to a new destination if there is no existing one
    navController.navigate(SomeDestination())
}
```

You may see how it is used for BottomNavigation in the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/main/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/demo/BottomNavigationScreen.kt).

### Methods with a search predicate

`moveToTop`, `popUpTo`, `replaceUpTo` methods require the `predicate` parameter to be specified. It provides a selection condition for a destination to search for.

In case multiple destinations match the predicate, you may specify the `match` parameter. `Match.Last` is the default value and in this case the last matching item from the start of the backstack will be selected. Alternatively, you may use `Match.First`.

### New custom methods

If your use-case calls for some advanced backstack manipulations, you may use `setNewBackstack` method. It is in fact the only public method defined in NavController, all other methods are provided as extensions and use `setNewBackstack` under the hood. Here is how a new extension method `moveLastEntryToStart` is implemented in the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/main/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/demo/BottomNavigationScreen.kt):

```kotlin
fun NavController<BottomNavigationDestination>.moveLastEntryToStart() {
    setNewBackstack(
        entries = backstack.entries.toMutableList().also {
            val entry = it.removeLast()
            it.add(0, entry)
        },
        action = NavAction.Pop
    )
}
```

## NavBackstack

You may access current backstack entries and the last [NavAction](/compose-navigation-reimagined/animations/#navaction) through `backstack` property of NavController. This property is backed up by [MutableState](https://developer.android.com/reference/kotlin/androidx/compose/runtime/MutableState) and any changes to it will notify composition.
