# Nested navigation

Nested navigation is actually quite simple. You just need to place NavHost (let's call it a child) into any entry of the other NavHost (a parent). You may want to add decoration around your child NavHost or leave it within the same viewport of the parent NavHost.

<p align="center">
    <img src="https://user-images.githubusercontent.com/5606565/199273157-711169f7-b465-4a74-8677-07e6504ba88d.svg" />
</p>

There may be different reasons for nesting your NavHosts:

- It may be useful when you need to have several backstacks at once, as in case of [BottomNavigation](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#BottomNavigation(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,kotlin.Function1)), [TabRow](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TabRow(kotlin.Int,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,kotlin.Function1,kotlin.Function0,kotlin.Function0)), or similar, where each item has it's own inner independent layer of navigation.

- You want to contain some particular flow of destinations within a single composable function. This flow may also contain some shared static layout elements. 

- You want to share a ViewModel between several destinations that logically and visually may be grouped into a single flow.

!!! note
    There is no depth limit for nesting NavHosts. In fact, each NavHost is completely oblivious to its placement in the hierarchy.