# NavHost

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199279598-ba27c532-a0b1-45f4-9893-246c28af540f.svg" />
</p>

NavHost is a composable that shows the last entry of a backstack, manages saved state and provides all Android architecture components associated with the entry: [Lifecycle](https://developer.android.com/reference/androidx/lifecycle/Lifecycle), [ViewModelStore](https://developer.android.com/reference/androidx/lifecycle/ViewModelStore) and [SavedStateRegistry](https://developer.android.com/reference/androidx/savedstate/SavedStateRegistry). All these components are provided through [CompositionLocalProvider](https://developer.android.com/jetpack/compose/compositionlocal) within their corresponding owners: `LocalLifecycleOwner`, `LocalViewModelStoreOwner` and `LocalSavedStateRegistryOwner`.

The components are kept around until its associated entry is removed from the backstack (or until the parent entry containing the current child NavHost is removed).

The default NavHost implementation by itself doesn't provide any animated transitions, it simply jump-cuts to the next destination:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329130-7a90c412-197a-4930-baac-8af81ef16fee.gif" />
</p>

## NavHostEntry and NavHostScope

Each NavEntry from the backstack is mapped to NavHostEntry within NavHost. NavHostEntry is what actually implements `LifecycleOwner`, `SavedStateRegistryOwner` and `ViewModelStoreOwner` interfaces.

Usually, you don't need to interact with NavHostEntries directly, everything just works out of the box. But if you have a situation when you need to access all NavHostEntries from the current backstack, e.g. [trying to access a ViewModel of neighbour entry](/compose-navigation-reimagined/view-models/#accessing-viewmodels-of-backstack-entries), you could do it through `NavHostScope` receiver of `contentSelector` function parameter.