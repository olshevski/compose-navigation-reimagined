package dev.olshevski.navigation.reimagined

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelStore

private val NoneTransitionSpec = NavTransitionSpec<Any?> { _, _, _ ->
    EnterTransition.None togetherWith ExitTransition.None
}

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible scrim flickering.
 *
 * Same as other NavHosts it also selects UI for every destination and provides saved state and
 * Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry) through
 * [CompositionLocalProvider] for every unique [NavEntry] in the [controller's][controller]
 * backstack.
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * @param controller the navigation controller that will provide its backstack to this
 * DialogNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the DialogNavHost through the [NavHostScope].
 */
@Composable
fun <T> DialogNavHost(
    controller: NavController<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable NavHostScope<T>.(destination: T) -> Unit
) = @OptIn(ExperimentalReimaginedApi::class) ScopingAnimatedNavHost(
    state = rememberScopingNavHostState(controller.backstack, EmptyScopeSpec),
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = contentSelector
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible scrim flickering.
 *
 * Same as other NavHosts it also selects UI for every destination and provides saved state and
 * Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry) through
 * [CompositionLocalProvider] for every unique [NavEntry] in the [backstack].
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the DialogNavHost through the [NavHostScope].
 */
@Composable
fun <T> DialogNavHost(
    backstack: NavBackstack<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable NavHostScope<T>.(destination: T) -> Unit
) = @OptIn(ExperimentalReimaginedApi::class) ScopingAnimatedNavHost(
    state = rememberScopingNavHostState(backstack, EmptyScopeSpec),
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible scrim flickering.
 *
 * Same as other NavHosts it also selects UI for every destination and provides saved state and
 * Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry) through
 * [CompositionLocalProvider] for every unique [NavEntry] in the [controller's][controller]
 * backstack.
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * **Scoping:** This version of DialogNavHost gives you the ability to define scopes.
 * Read more about it in [NavScopeSpec].
 *
 * @param controller the navigation controller that will provide its backstack to this
 * DialogNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access scoped [ViewModelStores][ViewModelStore].
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingDialogNavHost through the [ScopingNavHostScope].
 */
@Composable
fun <T, S> ScopingDialogNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(destination: T) -> Unit
) = @OptIn(ExperimentalReimaginedApi::class) ScopingAnimatedNavHost(
    state = rememberScopingNavHostState(controller.backstack, scopeSpec),
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible scrim flickering.
 *
 * Same as other NavHosts it also selects UI for every destination and provides saved state and
 * Android architecture components (Lifecycle, ViewModelStore, SavedStateRegistry) through
 * [CompositionLocalProvider] for every unique [NavEntry] in the [backstack].
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * **Scoping:** This version of DialogNavHost gives you the ability to define scopes.
 * Read more about it in [NavScopeSpec].
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access scoped [ViewModelStores][ViewModelStore].
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingDialogNavHost through the [ScopingNavHostScope].
 */
@Composable
fun <T, S> ScopingDialogNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(destination: T) -> Unit
) = @OptIn(ExperimentalReimaginedApi::class) ScopingAnimatedNavHost(
    state = rememberScopingNavHostState(backstack, scopeSpec),
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)
