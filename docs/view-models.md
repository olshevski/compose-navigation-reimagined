# ViewModels

Every unique NavEntry in NavHost provides its own ViewModelStore. Every such ViewModelStores is guaranteed to exist for as long as the associated NavEntry is present in the backstack.

As soon as NavEntry is removed from the backstack, its ViewModelStore with all ViewModels is cleared.

You can get ViewModels as you do it usually, by using composable `viewModel` from `androidx.lifecycle:lifecycle-viewmodel-compose` artifact, for example:

```kotlin
@Composable
fun SomeScreen() {
    val someViewModel = viewModel<SomeViewModel>()
    // ...
}
```

## Passing parameters into a ViewModel

There is no such thing as a Bundle of arguments for navigation entries in this library. This means that there is literally nothing to pass into `SavedStateHandle` of your ViewModel as the default arguments.

I personally recommend passing all parameters into a ViewModel constructor directly. This keeps everything clean and type-safe. For this you may use my [Easy Factories](https://github.com/olshevski/viewmodel-easy-factories) library that simplifies the creation of ViewModels with arbitrary parameters.

If you use dependency injections in your project, explore the [samples](https://github.com/olshevski/compose-navigation-reimagined/tree/main/samples-di) that show how to pass parameters into a ViewModel and inject all other dependencies:

- **Dagger/Anvil/Hilt** use a combination of Easy Factories library mentioned above and [@AssistedInject](https://dagger.dev/dev-guide/assisted-injection)

- **Koin** supports ViewModel parameters out of the box and does it charmingly simple