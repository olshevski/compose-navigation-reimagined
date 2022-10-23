package dev.olshevski.navigation.reimagined

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelStoreOwner

@ExperimentalAnimationApi
private val NoneTransitionSpec = NavTransitionSpec<Any?> { _, _, _ ->
    EnterTransition.None with ExitTransition.None
}

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible flickering of dialogs' scrim/fade.
 *
 * Same as other NavHosts it also selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * @param controller a navigation controller that will provide its backstack to this
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
@ExperimentalAnimationApi
@Composable
fun <T> DialogNavHost(
    controller: NavController<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable NavHostScope<T>.(T) -> Unit
) = DialogNavHost(
    backstack = controller.backstack,
    emptyBackstackPlaceholder = emptyBackstackPlaceholder,
    contentSelector = contentSelector
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible flickering of dialogs' scrim/fade.
 *
 * Same as other NavHosts it also selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [backstack].
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
@ExperimentalAnimationApi
@Composable
fun <T> DialogNavHost(
    backstack: NavBackstack<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable NavHostScope<T>.(T) -> Unit
) = ScopingDialogNavHost(
    backstack = backstack,
    scopeSpec = EmptyScopeSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible flickering of dialogs' scrim/fade.
 *
 * Same as other NavHosts it also selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [controller's][controller] backstack.
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * **Scoping:**
 *
 * This version of DialogNavHost gives you the ability to define scoped
 * [ViewModelStoreOwners][ViewModelStoreOwner] that can be shared between arbitrary destinations.
 *
 * To do so, you must return a desired set of scopes for each requested destination in
 * [scopeSpec]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStoreOwner
 * and all of its ViewModels will be cleared.
 *
 * To access a scoped ViewModelStoreOwner, you may call
 * [ScopingNavHostScope.getScopedViewModelStoreOwner] inside [contentSelector] with the same scope
 * object you've returned in [scopeSpec]. Then you may pass this scoped ViewModelStoreOwner
 * as a parameter into a ViewModel provider method of choice and create shared ViewModels,
 * easily accessible from different destinations.
 *
 * @param controller a navigation controller that will provide its backstack to this
 * DialogNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingDialogNavHost through the [ScopingNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingDialogNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(T) -> Unit
) = ScopingDialogNavHost(
    backstack = controller.backstack,
    scopeSpec = scopeSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)

/**
 * A navigation host specifically suited for showing dialogs. It is based on [AnimatedNavHost] and
 * provides smoother transition between dialogs rather than simple [NavHost] - there will be
 * no visible flickering of dialogs' scrim/fade.
 *
 * Same as other NavHosts it also selects UI for every destination and provides necessary
 * components (lifecycles, saved states, view models) through [CompositionLocalProvider]
 * for every unique [NavEntry] in the [backstack].
 *
 * Note that DialogNavHost doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * **Scoping:**
 *
 * This version of DialogNavHost gives you the ability to define scoped
 * [ViewModelStoreOwners][ViewModelStoreOwner] that can be shared between arbitrary destinations.
 *
 * To do so, you must return a desired set of scopes for each requested destination in
 * [scopeSpec]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStoreOwner
 * and all of its ViewModels will be cleared.
 *
 * To access a scoped ViewModelStoreOwner, you may call
 * [ScopingNavHostScope.getScopedViewModelStoreOwner] inside [contentSelector] with the same scope
 * object you've returned in [scopeSpec]. Then you may pass this scoped ViewModelStoreOwner
 * as a parameter into a ViewModel provider method of choice and create shared ViewModels,
 * easily accessible from different destinations.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingDialogNavHost through the [ScopingNavHostScope].
 */
@ExperimentalAnimationApi
@Composable
fun <T, S> ScopingDialogNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable ScopingNavHostScope<T, S>.(T) -> Unit
) = ScopingAnimatedNavHost(
    backstack = backstack,
    scopeSpec = scopeSpec,
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)
