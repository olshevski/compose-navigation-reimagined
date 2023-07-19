# Return results

As destination types are not strictly required to be Immutable, you may change them while they are in the backstack. This may be used for returning values from other destinations. Just make a mutable property backed up by `mutableStateOf` and change it when required.

For example, we want to return a string from the Second screen. Here is how destinations may be defined:

```kotlin
interface AcceptsResultFromSecond {
    val resultFromSecond: MutableState<String?>
}

@Stable
sealed class Destination : Parcelable {

    @Parcelize
    data class First(
        override val resultFromSecond: @RawValue MutableState<String?> = mutableStateOf(null)
    ) : Destination(), AcceptsResultFromSecond

    @Parcelize
    data object Second : Destination()

}
```

And to actually set the result from the Second screen you do:

```kotlin
val previousDestination = navController.backstack.entries.let {
    it[it.lastIndex - 1].destination
}
check(previousDestination is AcceptsResultFromSecond)
previousDestination.resultFromSecond.value = text
navController.pop()
```

You may see how it is implemented in the sample [here](https://github.com/olshevski/compose-navigation-reimagined/blob/main/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/demo/ReturnResultsScreen.kt).

!!! warning
    In general, returning values to previous destinations makes the navigation logic more complicated. Also, this approach doesn't guarantee full compile time type-safety. Use it with caution and when you are sure what you are doing. Sometimes it may be easier to use a shared state holder. 