# Shared ViewModels

Sometimes you need to access the same ViewModel instance from several destinations. The library provides multiple ways to achieve this.

## Nested navigation

The easiest way to share a ViewModel between several destinations is to use [a nested NavHost](/compose-navigation-reimagined/nested-navigation/). Simply collect all required destinations into a separate nested NavHost, and pass a ViewModel of the parent entry to each destination.

However, it may not work for all scenarios. Sometimes it is not desirable or possible to group destinations into a single nested NavHost. For such cases, it would be more convenient to use scoping NavHosts.

## Scoping NavHosts

Each NavHost in the library has its own `Scoping` counterpart. For `NavHost` it is `ScopingNavHost`, for `AnimatedNavHost` it is `ScopingAnimatedNavHost`, and so on.

Every scoping NavHost gives you the ability to assign scopes to destinations and access scoped ViewModelStoreOwners bound to each of the defined scope. Such scoped ViewModelStoreOwner is created when there is at least one backstack entry marked with the corresponding scope, and removed when there are none of the entries marked with it.

For example, the ViewModelStoreOwner for **Scope X** will exist only when at least one of the destinations **B** or **C** is in the backstack (their positions don't matter). Both **B** and **C** can access the same ViewModelStoreOwner instance. **A** cannot access it as it is not marked with **Scope X**.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199459154-80017d8c-f5d4-4e74-b3a1-9dca6c84f53a.svg" />
</p>

When both **B** and **C** are popped off the backstack and there is only **A** left, the ViewModelStoreOwner for **Scope X** will be cleared and removed.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199460324-6d4bfe70-631c-4080-b6bd-39837a06cc02.svg" />
</p>

Note that if you replace **B** and **C** with a new destination **D** that is also marked with **Scope X**, the ViewModelStoreOwner will not be recreated, but left as is.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199459161-ee9a01e1-215c-487e-b454-f82fa7f6967f.svg" />
</p>

In order to use scoping NavHost, you need to implement `NavScopeSpec` and pass it as the `scopeSpec` parameter. `NavScopeSpec` requests a set of scopes for each destination in the backstack:

```kotlin
@Parcelize
object ScopeX : Parcelable

val DestinationScopeSpec = NavScopeSpec<Destination, ScopeX> { destination ->  
    when (destination) {
        Destination.B, Destination.C, Destination.D -> setOf(ScopeX)
        else -> emptySet()
    }
}
```

Note that a destination may belong to several scopes at once, that's why NavScopeSpec requires you to return a `Set`.

Scoped ViewModelStoreOwner is implemented by `ScopedNavHostEntry` class. You can acquire all scoped entries associated with the current destination through the `ScopingNavHostScope` receiver of the `contentSelector` parameter:

```kotlin
ScopingNavHost(
    controller = navController,
    scopeSpec = DestinationScopeSpec
) { destination ->
    when (destination) {
        Destination.A -> { /* ... */ }
        Destination.B -> {
            val sharedViewModel = viewModel<SharedViewModel>(
                viewModelStoreOwner = scopedHostEntries[ScopeX]!!
            )
        }
        Destination.C -> { /* same code as for B */ }
        Destination.D -> { /* same code as for B */ }
    }
}
```

You just have to pass this ScopedNavHostEntry as the `viewModelStoreOwner` parameter of the `viewModel` method.

Alternatively, you can access scoped ViewModelStoreOwners through the `LocalScopedViewModelStoreOwners` composition local.

## Access ViewModels of backstack entries

If the two previous solutions are not suitable for your case, you may always access ViewModels of neighbour entries directly. Read more about it [here](/compose-navigation-reimagined/view-models/#accessing-viewmodels-of-backstack-entries).