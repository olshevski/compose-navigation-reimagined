<p align="center">
    <img width="600" src="https://user-images.githubusercontent.com/5606565/154991686-ea2cc9bd-9bc9-4088-91d3-64ec684861fb.svg" />
</p>

A small and simple, yet fully fledged and customizable navigation library for [Jetpack Compose](https://developer.android.com/jetpack/compose):

- Full **type-safety**
- State restoration
- Nested navigation with independent backstacks
- Easy integration with [BottomNavigation](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#BottomNavigation(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,kotlin.Function1)) and [TabRow](https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TabRow(kotlin.Int,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,kotlin.Function1,kotlin.Function0,kotlin.Function0))
- Own lifecycle, saved state and view models for every backstack entry
- Animated transitions
- Navigation logic may be easily moved to the ViewModel layer
- No builders, no obligatory superclasses for your composables

&nbsp;

# Why?

Dissatisfied and frustrated with the existing navigation solutions for Compose, I was thinking countless days about how it could all be done better. In the end I came up with a set of ideas that eventually evolved into a full-blown library.

I feel that it handles navigation greatly, and I would appreciate it if more people try it for themselves.