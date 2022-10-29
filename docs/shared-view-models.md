# Shared ViewModels

Sometimes you need to access the same ViewModel instance from several destinations. This library provides several different ways to achieve this. You may choose the one that suits your needs depending on the requirements.

## Nested navigation

The easiest way to share ViewModel between several destinations is to use [nested NavHost](/compose-navigation-reimagined/nested-navigation/). Simply collect all the required destinations into a separate nested NavHost, and pass ViewModel of the parent entry to each destination.

But it may not work for all scenarios, sometimes it is not logically possible to create such nested groups of destinations. For these cases, it would be better to use scoping NavHosts.

## Scoping NavHosts

Each NavHost in the library has its own scoping version of the NavHost. For NavHost it is ScopingNavHost, for AnimatedNavHost - ScopingAnimatedNavHost, and so on.

Every such scoping NavHost gives you the ability to assign scopes to some destinations and then access scoped ViewModelStoreOwners bound to each of the scope. Such scoped ViewModelStoreOwner is created when there is at least one entry in the backstack that is marked with the corresponding scope, and removed when there are none of the entries marked with this scope.

In the next image, ViewModelStoreOwner for **Scope X** will exist only when any of destinations **B** or **C** is in the backstack (their position doesn't matter). Both **B** and **C** can access the same ViewModelStoreOwner instance. Destination **A** cannot access it.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199308024-d4e3d00f-957a-41df-9f0f-bee60f88e354.svg" />
</p>

When both **B** and **C** are popped off the backstack and there is only **A** left, ViewModelStoreOwner of **Scope X** will be cleared and removed.

Note that if you replace **B** and **C** with a new destination **D** that is also marked with **Scope X**, ViewModelStoreOwner will not be recreated, but left as is.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199308037-df45a689-cfeb-48d3-ab97-3fadb6214e7f.svg" />
</p>

In order to user ScopingNavHost you need to implement NavScopeSpec and pass it as a `scopeSpec` parameter. NavScopeSpec requests a set of scopes for each destination that is on the backstack:

```kotlin
@Parcelize
object ScopeX : Parcelable

val ScreenScopeSpec = NavScopeSpec<Screen, ScopeX> { screen ->  
    when (screen) {
        Screen.B, Screen.C, Screen.D -> setOf(ScopeX)
        else -> emptySet()
    }
}
```

Note that a screen may belong to several scopes at once, that's why NavScopeSpec requires you to return a set.

To acquire scoped ViewModelStoreOwner you call `getScopedViewModelStoreOwner` method, that is available for `ScopingNavHostScope` receiver of `contentSelector` function parameter:

```kotlin
ScopingNavHost(
    controller = sheetController,
    scopeSpec = ScreenScopeSpec
) { destination ->
    when (destination) {
        Screen.A -> { /* ... */ }
        Screen.B -> {
            val scopedViewModelStoreOwner = getScopedViewModelStoreOwner(ScopeX)
            val sharedViewModel = viewModel<SharedViewModel>(scopedViewModelStoreOwner)
        }
        Screen.C -> { /* same code as for B */ }
        Screen.D -> { /* same code as for B */ }
    }
}
```

Then you just pass this ViewModelStoreOwner as a `viewModelStoreOwner` parameter of `viewModel` method.

## Access ViewModels of backstack entries

If two previous solutions didn't work for you for some reason, you may fall back to accessing ViewModels of neighbour backstack entries. Read about it [here](/compose-navigation-reimagined/view-models/#accessing-viewmodels-of-backstack-entries).