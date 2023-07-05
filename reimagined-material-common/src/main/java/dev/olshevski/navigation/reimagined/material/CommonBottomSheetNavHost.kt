package dev.olshevski.navigation.reimagined.material

import android.os.Parcelable
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.olshevski.navigation.reimagined.BaseNavHost
import dev.olshevski.navigation.reimagined.ComponentsProvider
import dev.olshevski.navigation.reimagined.ExperimentalReimaginedApi
import dev.olshevski.navigation.reimagined.InternalReimaginedApi
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.NavScopeSpec
import dev.olshevski.navigation.reimagined.NavSnapshot
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.rememberScopingNavHostState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.parcelize.Parcelize

/**
 * Common theme-independent implementation of `BottomSheetLayoutNavHost`. Not intended for direct
 * use.
 */
@InternalReimaginedApi
@Composable
fun <T, S> CommonBottomSheetNavHost(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    sheetLayoutModifier: Modifier,
    sheetPropertiesSpec: BottomSheetPropertiesSpec<T>,
    scrimColor: Color,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(destination: T) -> Unit,
) = @OptIn(ExperimentalReimaginedApi::class) BaseNavHost(
    state = rememberScopingNavHostState(backstack, scopeSpec),
    transitionQueueing = NavTransitionQueueing.ConflateQueued
) { snapshot ->
    val targetSnapshot by rememberUpdatedState(snapshot)
    var currentSnapshot by remember { mutableStateOf(targetSnapshot) }
    var isTransitionAnimating by remember { mutableStateOf(false) }

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
                    initialValue = if (sheetProperties.skipHalfExpanded) {
                        BottomSheetValue.Expanded
                    } else {
                        BottomSheetValue.HalfExpanded
                    },
                    sheetProperties = sheetProperties
                )
            }
        )
    }

    val isScrimVisible by remember {
        derivedStateOf {
            if (currentSnapshot != targetSnapshot) {
                targetSnapshot.items.isNotEmpty()
            } else {
                currentSnapshot.items.isNotEmpty()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scrim(
            color = scrimColor,
            onDismissRequest = {
                if (sheetState?.swipeableState?.confirmValueChange?.invoke(BottomSheetValue.Hidden) == true) {
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
                modifier = sheetLayoutModifier,
                snapshot = currentSnapshot,
                sheetState = sheetState!!,
                onDismissRequest = onDismissRequest,
                contentSelector = contentSelector
            )
        }
    }

    LaunchedEffect(targetSnapshot) {
        if (targetSnapshot.lastEntry != currentSnapshot.lastEntry) {
            try {
                isTransitionAnimating = true
                sheetState?.hide(swipePriority = MutatePriority.PreventUserInput)

                currentSnapshot = targetSnapshot
                sheetState = currentSnapshot.lastEntry?.let { lastEntry ->
                    BottomSheetState(
                        hostEntryId = lastEntry.id,
                        initialValue = BottomSheetValue.Hidden,
                        sheetProperties = sheetPropertiesSpec.getBottomSheetProperties(lastEntry.destination)
                    )
                }

                sheetState?.let { sheetState ->
                    // wait until anchors are calculated
                    snapshotFlow { sheetState.swipeableState.anchors }.filter { it.isNotEmpty() }
                        .first()
                    sheetState.show()
                }
            } finally {
                isTransitionAnimating = false
            }
        } else {
            currentSnapshot = targetSnapshot
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            !isTransitionAnimating && sheetState?.targetValue == BottomSheetValue.Hidden
                    && sheetState?.isAnimationRunning == true
        }.collect {
            if (it) {
                onDismissRequest()
            }
        }
    }

    currentSnapshot
}

@Composable
private fun <T, S> SnapshotBottomSheetLayout(
    modifier: Modifier,
    snapshot: NavSnapshot<T, S>,
    sheetState: BottomSheetState,
    onDismissRequest: () -> Unit,
    contentSelector: @Composable ScopingBottomSheetNavHostScope<T, S>.(destination: T) -> Unit,
) {
    val lastSnapshotItem = snapshot.items.last()
    key(lastSnapshotItem.hostEntry.id) {
        val destination = lastSnapshotItem.hostEntry.destination
        BottomSheetLayout(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            sheetContent = {
                lastSnapshotItem.ComponentsProvider {
                    val scope = remember(snapshot, sheetState) {
                        ScopingBottomSheetNavHostScopeImpl(
                            hostEntries = snapshot.items.map { it.hostEntry },
                            scopedHostEntries = lastSnapshotItem.scopedHostEntries,
                            sheetState = sheetState
                        )
                    }
                    scope.contentSelector(destination)
                }
            },
        )
    }
}

@Parcelize
private data class BottomSheetSavedState(
    val id: NavId,
    val value: BottomSheetValue
) : Parcelable

private val <T, S> NavSnapshot<T, S>.lastEntry get() = items.lastOrNull()?.hostEntry