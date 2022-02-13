package dev.olshevski.navigation.reimagined

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Dialog

@ExperimentalAnimationApi
private val NoneTransitionSpec = AnimatedNavHostTransitionSpec<Any?> { _, _, _ ->
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
 * Note that [DialogNavHost] doesn't wrap your composables into a [Dialog]. You need to use
 * either `Dialog` or `AlertDialog` composable inside a [contentSelector] yourself.
 *
 * @param controller a navigation controller that will provide its backstack to this
 * `DialogNavHost`. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param emptyBackstackPlaceholder an optional placeholder composable that will
 * be displayed when the backstack is empty. In the majority of cases you don't need
 * to set this. Note that the provided composable wouldn't get its own scoped components.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@ExperimentalAnimationApi
@Composable
fun <T> DialogNavHost(
    controller: NavController<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable (T) -> Unit
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
 * Note that [DialogNavHost] doesn't wrap your composables into a [Dialog]. You need to use
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
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen).
 */
@ExperimentalAnimationApi
@Composable
fun <T> DialogNavHost(
    backstack: NavBackstack<T>,
    emptyBackstackPlaceholder: @Composable () -> Unit = {},
    contentSelector: @Composable (T) -> Unit
) = AnimatedNavHost(
    backstack = backstack,
    transitionSpec = NoneTransitionSpec,
    emptyBackstackPlaceholder = { emptyBackstackPlaceholder() },
    contentSelector = { contentSelector(it) }
)
