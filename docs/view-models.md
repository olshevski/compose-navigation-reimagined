# ViewModels

Every unique NavEntry in NavHost provides its own ViewModelStore. Every such ViewModelStore is guaranteed to exist for as long as the associated NavEntry is present in the backstack.

As soon as NavEntry is removed from the backstack, its ViewModelStore with all ViewModels is cleared.

You can get ViewModels as you do it usually, by using composable `viewModel` from `androidx.lifecycle:lifecycle-viewmodel-compose` artifact, for example:

```kotlin
@Composable
fun SomeScreen() {
    val someViewModel = viewModel<SomeViewModel>()
    // ...
}
```

## Accessing ViewModels of backstack entries

It is possible to access ViewModelStoreOwner of any entry that is currently present on the backstack. It is done through the `NavHostScope` receiver of `contentSelector` function parameter of NavHost:

```kotlin
@Composable
fun NavHostScope<Screen>.SomeScreen() {
    val previousViewModel = viewModel<PreviousViewModel>(
        viewModelStoreOwner = findHostEntry { it is Screen.Previous }!!
    )
    // ...
}
```

## Passing parameters into a ViewModel

There is no such thing as a Bundle of arguments for navigation entries in this library. This means that there is literally nothing to pass into `SavedStateHandle` of your ViewModel as the default arguments.

I personally recommend passing all parameters into a ViewModel constructor directly. This keeps everything clean and type-safe.

If you use dependency injections in your project, explore the [samples](https://github.com/olshevski/compose-navigation-reimagined/tree/main/samples-di) that show how to pass parameters into a ViewModel and inject all other dependencies:

- **Dagger/Anvil/Hilt** use [@AssistedInject](https://dagger.dev/dev-guide/assisted-injection)

- **Koin** supports ViewModel parameters out of the box and does it charmingly simple

## hiltViewModel()

If Hilt is the DI library of your choice and you want to use `hiltViewModel()` method that you may already be familiar with from the official Navigation Component, you can add the dependency:

```kotlin
implementation("dev.olshevski.navigation:reimagined-hilt:<latest-version>")
```

It provides a similar `hiltViewModel()` method that works with the Reimagined library. The only catch is that by default it doesn't know how to pass arguments to the SavedStateHandle of your ViewModel. For this you can use an additional `defaultArguments` parameter:

```kotlin
val viewModel = hiltViewModel<SomeViewModel>(
    defaultArguments = bundleOf("id" to id)
)
```

And in ViewModel you can read this argument as such:

```kotlin
@HiltViewModel
class SomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : LoggingViewModel() {

    private val id: Int = savedStateHandle["id"]!!

}
```

!!! tip
    Don't forget to annotate your view models with `@HiltViewModel` annotation.

!!! warning
    Do not pass mutable data structures as `defaultArguments` and expect the external changes to be reflected through the SavedStateHandle inside a ViewModel, e.g. when trying to return results as described [here](/compose-navigation-reimagined/return-results/).

    As soon as SavedStateHandle parcelize/unparcelize data once, it becomes the only source of truth for the data it holds.
    
    If you still need to pass mutable data structure into your ViewModel, it would be more reliable to pass it directly as a [constructor parameter](#passing-parameters-into-a-viewmodel)).