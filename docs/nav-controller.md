# NavController

This is the main control point of navigation. It keeps record of all current backstack entries and preserves them on activity/process recreation.

NavController may be created with `rememberNavController` method within composition or with `navController` outside of it. The latter may be used for storing NavController in a ViewModel. As it implements Parcelable interface, it may be (and should be) stored in a [SavedStateHandle](https://developer.android.com/reference/androidx/lifecycle/SavedStateHandle).

Both `rememberNavController` and `navController` methods accept `startDestination` as a parameter:

```kotlin
val navController = rememberNavController<Screen>(
    startDestination = Screen.First
)
```

If you want to create NavController with an arbitrary number of backstack items, you may use `initialBackstack` parameter instead:

```kotlin
val navController = rememberNavController<Screen>(
    initialBackstack = listOf(Screen.First, Screen.Second, Screen.Third)
)
```

`Screen.Third` will become the currently displayed item. `Screen.First` and `Screen.Second` will be stored in the backstack.

## NavController in a ViewModel

The library provides a property delegate for creating and saving NavController in a SavedStateHandle:

```kotlin
class NavigationViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val navController by savedStateHandle.navController<Screen>(
        startDestination = Screen.First
    )

}
```

## Destinations

NavController accepts all types meeting the requirements as destinations. The requirements are:

1. The type must be writable to [Parcel](https://developer.android.com/reference/android/os/Parcel) - it could be Parcelable, Serializable, string/primitive or other supported type.

2. The type must be either [Stable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Stable), or [Immutable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable), or string/primitive type.

Other than that, you are not limited to any particular type.

!!! tip
    It is very convenient to define your set of destinations as a sealed class or enum. This way you will always be notified by the compiler that you have a non-exhaustive `when` statement if you add a new destination.

!!! tip
    You may also define your own base interface for destinations, for example:
 
    ```kotlin
    interface Screen : Parcelable {
        
        val title: String
    
        @Composable
        fun Content()
    
    }
    ```

    This way you may handle your destinations without checking for their particular instances.

## NavEntry

NavEntry wraps around every destination passed into NavController and provides a unique identifier to each of them. Every unique entry in the backstack then gets its own independent lifecycle, saved state and ViewModelStore.

In other words, if you add 2 or more equal destination to the backstack, they will get their own separate identity and a set of components.

Saved state and view models of each entry are guaranteed to be preserved for as long as the entry is present in the backstack.

## Navigation methods

There is a handful of pre-defined methods suitable for basic app navigation: `navigate`, `moveToTop`, `pop`, `popUpTo`, `popAll`, `replaceLast`, `replaceUpTo`, `replaceAll`. They all should be pretty much self-explanatory. But let's go into details about some of them:

### moveToTop

This method searches for some particular destination in the backstack and, as the method says, moves it to the top. This is particularly useful for integration with BottomNavigation/TabRow, when you want to always keep a single instance of every destination in the backstack.

The method is expected to be used in pair with `navigate`:

```kotlin
if (!navController.moveToTop { it is SomeDestination }) {
    // navigate to a new destination if there is no existing one
    navController.navigate(SomeDestination())
}
```

You may see how it is used for BottomNavigation in the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/main/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/demo/BottomNavigationScreen.kt).

### Methods with a search predicate

`moveToTop`, `popUpTo`, `replaceUpTo` - all these methods require the `predicate` parameter to be specified. It provides a selection condition for a destination to search for.

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

If you want to listen for backstack changes outside of composition you may set `onBackstackChange` listener of NavController.
