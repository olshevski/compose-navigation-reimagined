package dev.olshevski.navigation.reimagined.material

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import dev.olshevski.navigation.reimagined.BaseNavHost
import dev.olshevski.navigation.reimagined.ComponentsProvider
import dev.olshevski.navigation.reimagined.EmptyScopeSpec
import dev.olshevski.navigation.reimagined.ExperimentalReimaginedApi
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.NavScopeSpec
import dev.olshevski.navigation.reimagined.NavSnapshot
import dev.olshevski.navigation.reimagined.ScopingNavHost
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.parcelize.Parcelize

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [NavHost].
 *
 * @param controller a navigation controller that will provide its backstack to this
 * BottomSheetNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You *must* handle it and remove
 * the current destination from the backstack either with [pop], [popAll] or any other method
 * that will actually remove the current entry from the backstack. Failing to do so may result in
 * undefined UI state.
 *
 * @param sheetShape the shape of the bottom sheet
 *
 * @param sheetElevation the elevation of the bottom sheet
 *
 * @param sheetBackgroundColor the background color of the bottom sheet
 *
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
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
@ExperimentalMaterialApi
@Composable
fun <T> BottomSheetNavHost(
    controller: NavController<T>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetShape: Shape = BottomSheetDefaults.shape,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = BottomSheetDefaults.scrimColor,
    contentSelector: @Composable BottomSheetNavHostScope<T>.(T) -> Unit,
) = BottomSheetNavHost(
    backstack = controller.backstack,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetShape = sheetShape,
    sheetElevation = sheetElevation,
    sheetBackgroundColor = sheetBackgroundColor,
    sheetContentColor = sheetContentColor,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [NavHost].
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You *must* handle it and remove
 * the current destination from the backstack either with [pop], [popAll] or any other method
 * that will actually remove the current entry from the backstack. Failing to do so may result in
 * undefined UI state.
 *
 * @param sheetShape the shape of the bottom sheet
 *
 * @param sheetElevation the elevation of the bottom sheet
 *
 * @param sheetBackgroundColor the background color of the bottom sheet
 *
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
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
@ExperimentalMaterialApi
@Composable
fun <T> BottomSheetNavHost(
    backstack: NavBackstack<T>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetShape: Shape = BottomSheetDefaults.shape,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = BottomSheetDefaults.scrimColor,
    contentSelector: @Composable BottomSheetNavHostScope<T>.(T) -> Unit,
) = ScopingBottomSheetNavHost(
    backstack = backstack,
    scopeSpec = EmptyScopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetShape = sheetShape,
    sheetElevation = sheetElevation,
    sheetBackgroundColor = sheetBackgroundColor,
    sheetContentColor = sheetContentColor,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [ScopingNavHost].
 *
 * @param controller a navigation controller that will provide its backstack to this
 * BottomSheetNavHost. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but setting a different [NavController] will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You *must* handle it and remove
 * the current destination from the backstack either with [pop], [popAll] or any other method
 * that will actually remove the current entry from the backstack. Failing to do so may result in
 * undefined UI state.
 *
 * @param sheetShape the shape of the bottom sheet
 *
 * @param sheetElevation the elevation of the bottom sheet
 *
 * @param sheetBackgroundColor the background color of the bottom sheet
 *
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
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
@ExperimentalMaterialApi
@Composable
fun <T, S> ScopingBottomSheetNavHost(
    controller: NavController<T>,
    scopeSpec: NavScopeSpec<T, S>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetShape: Shape = BottomSheetDefaults.shape,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = BottomSheetDefaults.scrimColor,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(T) -> Unit,
) = ScopingBottomSheetNavHost(
    backstack = controller.backstack,
    scopeSpec = scopeSpec,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetShape = sheetShape,
    sheetElevation = sheetElevation,
    sheetBackgroundColor = sheetBackgroundColor,
    sheetContentColor = sheetContentColor,
    sheetPropertiesSpec = sheetPropertiesSpec,
    scrimColor = scrimColor,
    contentSelector = contentSelector
)

/**
 * NavHost analogue of [ModalBottomSheetLayout] from Material package. Provides better visual
 * transitions between different BottomSheets, as well as all other features of the regular
 * [ScopingNavHost].
 *
 * @param backstack the backstack from a [NavController] that will be used to observe navigation
 * changes. The last entry of the backstack is always the currently displayed entry.
 * You should do all backstack modifications through the same instance of [NavController],
 * but using a different [NavController] and setting its backstack will be handled correctly.
 *
 * @param scopeSpec specifies scopes for every destination. This gives you the ability to easily
 * create and access shared ViewModels.
 *
 * @param onDismissRequest dismiss request caused by user interaction. Called either when the scrim
 * is clicked or when the bottom sheet is hidden with swipe. You *must* handle it and remove
 * the current destination from the backstack either with [pop], [popAll] or any other method
 * that will actually remove the current entry from the backstack. Failing to do so may result in
 * undefined UI state.
 *
 * @param sheetShape the shape of the bottom sheet
 *
 * @param sheetElevation the elevation of the bottom sheet
 *
 * @param sheetBackgroundColor the background color of the bottom sheet
 *
 * @param sheetContentColor the preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
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
@OptIn(ExperimentalReimaginedApi::class)
@ExperimentalMaterialApi
@Composable
fun <T, S> ScopingBottomSheetNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetShape: Shape = BottomSheetDefaults.shape,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T> = DefaultBottomSheetPropertiesSpec,
    scrimColor: Color = BottomSheetDefaults.scrimColor,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(T) -> Unit,
) = BaseNavHost(
    backstack = backstack,
    scopeSpec = scopeSpec
) { snapshot ->
    val targetSnapshot by rememberUpdatedState(snapshot)
    var currentSnapshot by remember { mutableStateOf(targetSnapshot) }
    var isTransitionAnimating by remember { mutableStateOf(false) }
    val isTransitionRunningState = remember {
        derivedStateOf { currentSnapshot != targetSnapshot || isTransitionAnimating }
    }
    val isTransitionRunning by isTransitionRunningState
    var sheetState: BottomSheetState? by rememberSaveable(
        saver = Saver(
            save = { mutableState ->
                val sheetState = mutableState.value
                if (sheetState != null && sheetState.currentValue != BottomSheetValue.Hidden) {
                    BottomSheetSavedState(
                        id = sheetState.hostEntryId,
                        value = sheetState.currentValue,
                    )
                } else {
                    null
                }
            },
            restore = { savedState ->
                val lastEntry = currentSnapshot.lastEntry
                if (lastEntry != null && lastEntry.id == savedState.id) {
                    mutableStateOf(
                        BottomSheetState(
                            hostEntryId = lastEntry.id,
                            initialValue = savedState.value,
                            isTransitionRunningState = isTransitionRunningState,
                            sheetProperties = sheetPropertiesSpec.getBottomSheetProperties(lastEntry.destination)
                        )
                    )
                } else {
                    null
                }
            }
        )
    ) {
        mutableStateOf(
            currentSnapshot.lastEntry?.let { lastEntry ->
                val sheetProperties =
                    sheetPropertiesSpec.getBottomSheetProperties(lastEntry.destination)
                BottomSheetState(
                    hostEntryId = lastEntry.id,
                    initialValue = if (sheetProperties.isSkipHalfExpanded) {
                        BottomSheetValue.Expanded
                    } else {
                        BottomSheetValue.HalfExpanded
                    },
                    isTransitionRunningState = isTransitionRunningState,
                    sheetProperties = sheetProperties
                )
            }
        )
    }

    val isScrimVisible by remember {
        derivedStateOf {
            if (isTransitionRunning) {
                targetSnapshot.items.isNotEmpty()
            } else {
                currentSnapshot.items.isNotEmpty()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scrim(
            color = scrimColor,
            onDismiss = {
                if (sheetState?.confirmStateChange?.invoke(BottomSheetValue.Hidden) == true) {
                    onDismissRequest()
                }
            },
            visible = isScrimVisible
        )
        if (currentSnapshot.items.isNotEmpty()) {

            // Need to isolate BottomSheetLayout into a separate composable, so it doesn't
            // capture currentSnapshot and sheetState as MutableState instances.
            //
            // The latter causes the underlying SubcomposeLayout to recompose independently of
            // outer recomposition path, which causes logical issues and may lead to crashes.
            //
            // The SubcomposeLayout issue is tracked here:
            // https://issuetracker.google.com/issues/254645321

            SnapshotBottomSheetLayout(
                snapshot = currentSnapshot,
                sheetState = sheetState!!,
                sheetShape = sheetShape,
                sheetElevation = sheetElevation,
                sheetBackgroundColor = sheetBackgroundColor,
                sheetContentColor = sheetContentColor,
                onDismissRequest = onDismissRequest,
                contentSelector = contentSelector
            )
        }
    }

    LaunchedEffect(targetSnapshot) {
        if (targetSnapshot.lastEntry != currentSnapshot.lastEntry) {
            isTransitionAnimating = true
            sheetState?.let { sheetState ->
                if (sheetState.currentValue != BottomSheetValue.Hidden) {
                    sheetState.hide()
                }
            }

            currentSnapshot = targetSnapshot
            sheetState = currentSnapshot.lastEntry?.let { lastEntry ->
                BottomSheetState(
                    hostEntryId = lastEntry.id,
                    initialValue = BottomSheetValue.Hidden,
                    isTransitionRunningState = isTransitionRunningState,
                    sheetProperties = sheetPropertiesSpec.getBottomSheetProperties(lastEntry.destination)
                )
            }

            sheetState?.let { sheetState ->
                // wait until anchors are calculated
                snapshotFlow { sheetState.anchors }.filter { it.isNotEmpty() }.first()
                sheetState.show()
            }
            isTransitionAnimating = false
        } else {
            currentSnapshot = targetSnapshot
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState?.currentValue }
            .filter { it == BottomSheetValue.Hidden }
            .collect {
                if (!isTransitionRunning) {
                    onDismissRequest()
                }
            }
    }

    currentSnapshot
}

@ExperimentalReimaginedApi
@ExperimentalMaterialApi
@Composable
private fun <T, S> SnapshotBottomSheetLayout(
    snapshot: NavSnapshot<T, S>,
    sheetState: BottomSheetState,
    sheetShape: Shape,
    sheetElevation: Dp,
    sheetBackgroundColor: Color,
    sheetContentColor: Color,
    onDismissRequest: () -> Unit,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(T) -> Unit,
) {
    val lastSnapshotItem = snapshot.items.last()
    key(lastSnapshotItem.hostEntry.id) {
        BottomSheetLayout(
            sheetState = sheetState,
            sheetShape = sheetShape,
            sheetElevation = sheetElevation,
            sheetBackgroundColor = sheetBackgroundColor,
            sheetContentColor = sheetContentColor,
            onDismissRequest = onDismissRequest,
            sheetContent = {
                lastSnapshotItem.hostEntry.ComponentsProvider {
                    val scope = remember(snapshot, sheetState) {
                        ScopingBottomSheetNavHostScopeImpl(
                            hostEntries = snapshot.items.map { it.hostEntry },
                            scopedHostEntries = lastSnapshotItem.scopedHostEntries,
                            sheetState = sheetState
                        )
                    }
                    scope.contentSelector(lastSnapshotItem.hostEntry.destination)
                }
            },
        )
    }
}

@ExperimentalMaterialApi
@Parcelize
private data class BottomSheetSavedState(
    val id: NavId,
    val value: BottomSheetValue
) : Parcelable

@ExperimentalReimaginedApi
private val <T, S> NavSnapshot<T, S>.lastEntry
    get() = items.lastOrNull()?.hostEntry