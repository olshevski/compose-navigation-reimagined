# Animations

If you want to show animated transitions between destinations use AnimatedNavHost. The default transition is a simple crossfade, but you can granularly customize every transition with your own `AnimatedNavHostTransitionSpec` implementation.

Here is one possible implementation of AnimatedNavHostTransitionSpec:

```kotlin
val CustomTransitionSpec = AnimatedNavHostTransitionSpec<Any?> { action, _, _ ->
    val direction = if (action == NavAction.Pop) {
        AnimatedContentScope.SlideDirection.End
    } else {
        AnimatedContentScope.SlideDirection.Start
    }
    slideIntoContainer(direction) with slideOutOfContainer(direction)
}
```

Set it into AnimatedNavHost:

```kotlin
AnimatedNavHost(
    controller = navController,
    transitionSpec = CustomTransitionSpec
) { destination ->
    // ...
}
```

and it'll end up looking like this:

<p align="center">
    <img width="240" src="https://user-images.githubusercontent.com/5606565/152329115-827e073e-c59d-4793-9f03-f9f684037a28.gif" />
</p>

In AnimatedNavHostTransitionSpec you get the parameters:

- `action` - a hint about the last NavController method that changed the backstack
- `from` - a previous visible destination
- `to` - a target visible destination

This information is plenty enough to choose a transition for every possible combination of screens and navigation actions.

## NavAction

There are four default NavAction types:

- `Pop`, `Replace` and `Navigate` - objects that correspond to `pop…`, `replace…`, `navigate` methods of NavController
- `Idle` - the default action of a newly created NavController

You can also create new action types by extending abstract `NavAction` class. Pass any of the type into `setNewBackstackEntries` method of NavController and handle it in AnimatedNavHostTransitionSpec.

The last action can also be accessed through `action` property of NavBackstack.