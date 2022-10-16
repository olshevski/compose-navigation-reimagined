package dev.olshevski.navigation.reimagined.material

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.BaseNavHost
import dev.olshevski.navigation.reimagined.ComponentsProvider
import dev.olshevski.navigation.reimagined.EmptyScopeSpec
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.NavSnapshot
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.parcelize.Parcelize

@ExperimentalMaterialApi
@Composable
fun <T> BottomSheetNavHost(
    backstack: NavBackstack<T>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetShape: Shape = MaterialTheme.shapes.large.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    ),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    contentSelector: @Composable (T) -> Unit,
) = BaseNavHost(
    backstack = backstack,
    scopeSpec = EmptyScopeSpec
) { targetSnapshot ->
    var currentSnapshot by remember { mutableStateOf(targetSnapshot) }
    var sheetState by rememberSaveable(
        saver = Saver(
            save = { mutableState ->
                val currentLastEntry = currentSnapshot.lastEntry
                val sheetState = mutableState.value
                if (sheetState != null && currentLastEntry != null
                    && sheetState.currentValue != ModalBottomSheetValue.Hidden
                ) {
                    ModalBottomSheetSavedState(
                        id = currentLastEntry.id,
                        value = sheetState.currentValue
                    )
                } else {
                    null
                }
            },
            restore = { savedState ->
                val currentLastEntry = currentSnapshot.lastEntry
                if (currentLastEntry != null && currentLastEntry.id == savedState.id) {
                    mutableStateOf(ModalBottomSheetState(savedState.value))
                } else {
                    null
                }
            }
        )
    ) {
        mutableStateOf(
            currentSnapshot.lastEntry?.let {
                ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            }
        )
    }
    var isTransitionRunning by remember { mutableStateOf(false) }
    val isScrimVisible by remember(targetSnapshot) {
        derivedStateOf {
            val visibleBetweenTransitions =
                isTransitionRunning && targetSnapshot.items.isNotEmpty()
            val visibleForExpandedStates = sheetState?.targetValue.let {
                it != null && it != ModalBottomSheetValue.Hidden
            }
            visibleBetweenTransitions || visibleForExpandedStates
        }
    }

    Box(modifier = modifier) {
        Scrim(
            color = scrimColor,
            onDismiss = {
                if (sheetState?.confirmStateChange?.invoke(ModalBottomSheetValue.Hidden) == true) {
                    onDismissRequest()
                }
            },
            visible = isScrimVisible
        )
        currentSnapshot.lastEntry?.let { lastEntry ->
            key(lastEntry.id) {
                ModalBottomSheetLayout(
                    sheetShape = sheetShape,
                    sheetElevation = sheetElevation,
                    sheetBackgroundColor = sheetBackgroundColor,
                    sheetContentColor = sheetContentColor,
                    sheetState = sheetState!!,
                    isTransitionRunning = isTransitionRunning,
                    sheetContent = {
                        lastEntry.ComponentsProvider {
                            contentSelector(lastEntry.destination)
                        }
                    },
                )
            }
        }
    }

    LaunchedEffect(targetSnapshot) {
        if (targetSnapshot.lastEntry != currentSnapshot.lastEntry) {
            isTransitionRunning = true
            sheetState?.hide()
            currentSnapshot = targetSnapshot
            sheetState = currentSnapshot.lastEntry?.let {
                ModalBottomSheetState(ModalBottomSheetValue.Hidden)
            }
            sheetState?.let { sheetState ->
                snapshotFlow { sheetState.anchors }.filter { it.isNotEmpty() }.first()
                sheetState.show()
            }
            isTransitionRunning = false
        } else {
            currentSnapshot = targetSnapshot
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState?.currentValue }
            .filter { it == ModalBottomSheetValue.Hidden }
            .collect {
                if (!isTransitionRunning) {
                    onDismissRequest()
                }
            }
    }

    currentSnapshot
}

@ExperimentalMaterialApi
@Parcelize
private data class ModalBottomSheetSavedState(
    val id: NavId,
    val value: ModalBottomSheetValue
) : Parcelable

private val <T, S> NavSnapshot<T, S>.lastEntry
    get() = items.lastOrNull()?.hostEntry