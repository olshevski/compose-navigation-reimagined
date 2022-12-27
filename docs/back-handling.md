# Back handling
Back handling in the library is opt-in, rather than opt-out. By itself, neither NavController nor NavHost handles the back button press. You can add `NavBackHandler` or usual `BackHandler` in order to react to back presses where you need to.

NavBackHandler is the most basic implementation of BackHandler - it calls `pop` only when there are more than one item in the backstack. When there is only one backstack item left, NavBackHandler is disabled, and any upper-level BackHandler may take its turn to react to back button presses.

If you want to specify your own backstack logic, use BackHandler directly. For example, this is how back navigation is handled for BottomNavigation in the [sample](https://github.com/olshevski/compose-navigation-reimagined/blob/main/sample/src/main/kotlin/dev/olshevski/navigation/reimagined/sample/ui/demo/BottomNavigationScreen.kt):

```kotlin
@Composable
private fun BottomNavigationBackHandler(
    navController: NavController<BottomNavigationDestination>
) {
    BackHandler(enabled = navController.backstack.entries.size > 1) {
        val lastEntry = navController.backstack.entries.last()
        if (lastEntry.destination == BottomNavigationDestination.values()[0]) {
            // The start destination should always be the last to pop. We move
            // it to the start to preserve its saved state and view models.
            navController.moveLastEntryToStart()
        } else {
            navController.pop()
        }
    }
}
```

!!! bug
    Always place your NavBackHandler/BackHandler **before** the corresponding NavHost.

    As both BackHandler and NavHost use Lifecycle under the hood, there is a case when the order of back handling may be restored incorrectly after process/activity recreation. This is how the framework works and there is nothing to do about it. Simple placement of BackHandler before NavHost guarantees no issues in this part.

