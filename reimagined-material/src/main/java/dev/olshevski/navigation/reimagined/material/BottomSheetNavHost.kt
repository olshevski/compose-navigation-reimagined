package dev.olshevski.navigation.reimagined.material

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.BaseNavHost
import dev.olshevski.navigation.reimagined.ComponentsProvider
import dev.olshevski.navigation.reimagined.EmptyScopeSpec
import dev.olshevski.navigation.reimagined.NavBackstack
import dev.olshevski.navigation.reimagined.NavSnapshot

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
) {
    BaseNavHost(
        backstack = backstack,
        scopeSpec = EmptyScopeSpec
    ) { targetSnapshot ->
        var currentSnapshot by remember { mutableStateOf(targetSnapshot) }
        var isTransitionRunning by remember { mutableStateOf(false) }
        val keepScrimVisible by derivedStateOf {
            isTransitionRunning && targetSnapshot.items.isNotEmpty()
        }
        val targetSnapshotKey = targetSnapshot.items.lastOrNull()?.hostEntry?.id
        val currentLastEntry = currentSnapshot.items.lastOrNull()?.hostEntry
        val currentSnapshotKey = currentLastEntry?.id
        val sheetState = rememberModalBottomSheetState(
            initialValue = if (currentLastEntry == null) {
                ModalBottomSheetValue.Hidden
            } else {
                ModalBottomSheetValue.Expanded
            }
        )

        ModalBottomSheetLayout(
            modifier = modifier,
            sheetShape = sheetShape,
            sheetElevation = sheetElevation,
            sheetBackgroundColor = sheetBackgroundColor,
            sheetContentColor = sheetContentColor,
            scrimColor = scrimColor,
            sheetState = sheetState,
            isTransitionRunning = isTransitionRunning,
            keepScrimVisible = keepScrimVisible,
            sheetContent = {
                key(currentSnapshotKey) {
                    if (currentLastEntry == null) {
                        Spacer(Modifier.height(1.dp))
                    } else {
                        currentLastEntry.ComponentsProvider {
                            contentSelector(currentLastEntry.destination)
                        }
                    }
                }
            },
            onDismissRequest = onDismissRequest
        )

        LaunchedEffect(targetSnapshotKey) {
            if (targetSnapshotKey != currentSnapshotKey) {
                isTransitionRunning = true
                sheetState.hide()
                currentSnapshot = targetSnapshot
                if (currentLastEntry != null) {
                    sheetState.show()
                }
                isTransitionRunning = false
            }
        }

        DisposableEffect(sheetState.isVisible) {
            if (!sheetState.isVisible && !isTransitionRunning) {
                onDismissRequest()
            }
            onDispose {}
        }

        currentSnapshot
    }
}

private val <T, S> NavSnapshot<T, S>.key get() = items.lastOrNull()?.hostEntry?.id