package dev.olshevski.navigation.reimagined.material

import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStoreOwner
import dev.olshevski.navigation.reimagined.EmptyScopeSpec
import dev.olshevski.navigation.reimagined.InternalReimaginedApi
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.NavScopeSpec
import dev.olshevski.navigation.reimagined.ScopingNavHost
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [NavHost].
 *
 * Unlike for ModalBottomSheetLayout you need to use Surface in [contentSelector] yourself. This
 * allows you to customize Surface for each destination.
 *
 * @param controller the navigation controller that will provide its backstack to this
 * BottomSheetNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You should handle it and remove
 * the current entry from the backstack either with [pop], [popAll] or some other method.
 *
 * @param modifier the modifier of the whole layout (the scrim and the bottom sheet)
 *
 * @param sheetLayoutModifier the modifier applied to the inner bottom sheet layout only. May be
 * used to handle window insets.
 *
 * @param sheetPropertiesSpec specifies [BottomSheetProperties] for every BottomSheet destination
 *
 * @param scrimColor the color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the BottomSheetNavHost through
 * the [BottomSheetNavHostScope].
 */
@Composable
fun <T> BottomSheetLayoutNavHost(
    controller: NavController<T>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetLayoutModifier: Modifier = Modifier,
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    contentSelector: @Composable BottomSheetNavHostScope<T>.(destination: T) -> Unit,
) = @OptIn(InternalReimaginedApi::class) CommonBottomSheetLayoutNavHost(
    backstack = controller.backstack,
    scopeSpec = EmptyScopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetLayoutModifier = sheetLayoutModifier,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [NavHost].
 *
 * Unlike for ModalBottomSheetLayout you need to use Surface in [contentSelector] yourself. This
 * allows you to customize Surface for each destination.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You should handle it and remove
 * the current entry from the backstack either with [pop], [popAll] or some other method.
 *
 * @param modifier the modifier of the whole layout (the scrim and the bottom sheet)
 *
 * @param sheetLayoutModifier the modifier applied to the inner bottom sheet layout only. May be
 * used to handle window insets.
 *
 * @param sheetPropertiesSpec specifies [BottomSheetProperties] for every BottomSheet destination
 *
 * @param scrimColor the color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the BottomSheetNavHost through
 * the [BottomSheetNavHostScope].
 */
@Composable
fun <T> BottomSheetLayoutNavHost(
    backstack: NavBackstack<T>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetLayoutModifier: Modifier = Modifier,
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    contentSelector: @Composable BottomSheetNavHostScope<T>.(destination: T) -> Unit,
) = @OptIn(InternalReimaginedApi::class) CommonBottomSheetLayoutNavHost(
    backstack = backstack,
    scopeSpec = EmptyScopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetLayoutModifier = sheetLayoutModifier,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of `ModalBottomSheetLayout` from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [ScopingNavHost].
 *
 * Unlike for ModalBottomSheetLayout you need to use Surface in [contentSelector] yourself. This
 * allows you to customize Surface for each destination.
 *
 * @param controller the navigation controller that will provide its backstack to this
 * BottomSheetNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access scoped [ViewModelStoreOwners][ViewModelStoreOwner].
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You should handle it and remove
 * the current entry from the backstack either with [pop], [popAll] or some other method.
 *
 * @param modifier the modifier of the whole layout (the scrim and the bottom sheet)
 *
 * @param sheetLayoutModifier the modifier applied to the inner bottom sheet layout only. May be
 * used to handle window insets.
 *
 * @param sheetPropertiesSpec specifies [BottomSheetProperties] for every BottomSheet destination
 *
 * @param scrimColor the color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingBottomSheetNavHost through
 * the [ScopingBottomSheetNavHostScope].
 */
@Composable
fun <T, S> ScopingBottomSheetLayoutNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetLayoutModifier: Modifier = Modifier,
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(destination: T) -> Unit,
) = @OptIn(InternalReimaginedApi::class) CommonBottomSheetLayoutNavHost(
    backstack = controller.backstack,
    scopeSpec = scopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetLayoutModifier = sheetLayoutModifier,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [ScopingNavHost].
 *
 * Unlike for ModalBottomSheetLayout you need to use Surface in [contentSelector] yourself. This
 * allows you to customize Surface for each destination.
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access scoped [ViewModelStoreOwners][ViewModelStoreOwner].
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You should handle it and remove
 * the current entry from the backstack either with [pop], [popAll] or some other method.
 *
 * @param modifier the modifier of the whole layout (the scrim and the bottom sheet)
 *
 * @param sheetLayoutModifier the modifier applied to the inner bottom sheet layout only. May be
 * used to handle window insets.
 *
 * @param sheetPropertiesSpec specifies [BottomSheetProperties] for every BottomSheet destination
 *
 * @param scrimColor the color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 *
 * @param contentSelector provides a composable that corresponds to the current last destination
 * in the backstack. In other words, here is where you select UI to show (e.g. a screen). Also,
 * provides additional functionality of the ScopingBottomSheetNavHost through
 * the [ScopingBottomSheetNavHostScope].
 */
@Composable
fun <T, S> ScopingBottomSheetLayoutNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetLayoutModifier: Modifier = Modifier,
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(destination: T) -> Unit,
) = @OptIn(InternalReimaginedApi::class) CommonBottomSheetLayoutNavHost(
    backstack = backstack,
    scopeSpec = scopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetLayoutModifier = sheetLayoutModifier,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)