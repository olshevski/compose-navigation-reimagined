package dev.olshevski.navigation.reimagined

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadOnlyProperty

private fun <T> SavedStateHandle.navControllerInternal(
    key: String?,
    initialBackstack: List<T>
) = ReadOnlyProperty<Any, NavController<T>> { _, property ->
    val navControllerKey = key ?: property.name
    this.get<NavController<T>>(navControllerKey)
        ?: dev.olshevski.navigation.reimagined.navController(initialBackstack).also {
            this[navControllerKey] = it
        }
}

private fun <T> SavedStateHandle.navControllerInternal(
    key: String?,
    startDestination: T
) = navControllerInternal(key, listOf(startDestination))

/**
 * Returns a property delegate that creates and saves a new [NavController] instance in
 * a receiver [SavedStateHandle]. NavController's backstack will be prefilled with destinations
 * from the [initialBackstack].
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost]. The first item of the list will be the last item that can be popped of
 * the backstack.
 *
 * The [initialBackstack] list may be empty.
 *
 * @param key an optional key to use for saving NavController instance inside a SavedStateHandle.
 * By default, the name of the property will be used as a key.
 */
@Deprecated(
    "Use official 'saveable' delegate instead",
    replaceWith = ReplaceWith(
        expression = "this.saveable<NavController<T>>(key) { navController<T>(initialBackstack) }",
        imports = ["androidx.lifecycle.viewmodel.compose.saveable"]
    )
)
fun <T> SavedStateHandle.navController(
    key: String,
    initialBackstack: List<T>
) = navControllerInternal(key, initialBackstack)

/**
 * Returns a property delegate that creates and saves a new [NavController] instance in
 * a receiver [SavedStateHandle]. NavController's backstack will be prefilled with destinations
 * from the [initialBackstack].
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost]. The first item of the list will be the last item that can be popped of
 * the backstack.
 *
 * The [initialBackstack] list may be empty.
 *
 * @param key an optional key to use for saving NavController instance inside a SavedStateHandle.
 * By default, the name of the property will be used as a key.
 */
@Deprecated(
    "Use official 'saveable' delegate instead",
    replaceWith = ReplaceWith(
        expression = "this.saveable<NavController<T>> { navController<T>(initialBackstack) }",
        imports = ["androidx.lifecycle.viewmodel.compose.saveable"]
    )
)
fun <T> SavedStateHandle.navController(
    key: Unit? = null,
    initialBackstack: List<T>
) = navControllerInternal(null, initialBackstack)

/**
 * Returns a property delegate that creates and saves a new [NavController] instance in
 * a receiver [SavedStateHandle]. NavController's backstack will contain a single item -
 * [startDestination].
 *
 * @param key an optional key to use for saving NavController instance inside a SavedStateHandle.
 * By default, the name of the property will be used as a key.
 */
@Deprecated(
    "Use official 'saveable' delegate instead",
    replaceWith = ReplaceWith(
        expression = "this.saveable<NavController<T>>(key) { navController<T>(startDestination) }",
        imports = ["androidx.lifecycle.viewmodel.compose.saveable"]
    )
)
fun <T> SavedStateHandle.navController(
    key: String,
    startDestination: T
) = navControllerInternal(key, startDestination)

/**
 * Returns a property delegate that creates and saves a new [NavController] instance in
 * a receiver [SavedStateHandle]. NavController's backstack will contain a single item -
 * [startDestination].
 *
 * @param key an optional key to use for saving NavController instance inside a SavedStateHandle.
 * By default, the name of the property will be used as a key.
 */
@Deprecated(
    "Use official 'saveable' delegate instead",
    replaceWith = ReplaceWith(
        expression = "this.saveable<NavController<T>> { navController<T>(startDestination) }",
        imports = ["androidx.lifecycle.viewmodel.compose.saveable"]
    )
)
fun <T> SavedStateHandle.navController(
    key: Unit? = null,
    startDestination: T
) = navControllerInternal(null, startDestination)
