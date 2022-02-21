# NavHost

NavHost is a composable that shows the last entry of a backstack and provides all components associated with this particular entry: [Lifecycle](https://developer.android.com/reference/androidx/lifecycle/Lifecycle), [SavedStateRegistry](https://developer.android.com/reference/androidx/savedstate/SavedStateRegistry) and [ViewModelStore](https://developer.android.com/reference/androidx/lifecycle/ViewModelStore). All these components are provided through [CompositionLocalProvider](https://developer.android.com/jetpack/compose/compositionlocal) within their corresponding owners `LocalLifecycleOwner`, `LocalSavedStateRegistryOwner` and `LocalViewModelStoreOwner`.

The components are kept around until its associated entry is removed from the backstack (or until the parent entry containing the current child NavHost is removed).

NavHost by itself doesn't provide any animated transitions, it simply jump-cuts to the next destination:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329130-7a90c412-197a-4930-baac-8af81ef16fee.gif" />
</p>
