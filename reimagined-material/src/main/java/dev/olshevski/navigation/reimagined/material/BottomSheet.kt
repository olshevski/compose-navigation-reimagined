/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.olshevski.navigation.reimagined.material

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.NavId
import dev.olshevski.navigation.reimagined.material.BottomSheetValue.Expanded
import dev.olshevski.navigation.reimagined.material.BottomSheetValue.HalfExpanded
import dev.olshevski.navigation.reimagined.material.BottomSheetValue.Hidden
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/*
 * Based on ModalBottomSheet.kt from androidx.compose.material package (last commit 047c2ef).
 *
 * Reference:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/ModalBottomSheet.kt
 */

/**
 * Possible values of [BottomSheetState].
 */
@ExperimentalMaterialApi
enum class BottomSheetValue {
    /**
     * The bottom sheet is not visible.
     */
    Hidden,

    /**
     * The bottom sheet is visible at full height.
     */
    Expanded,

    /**
     * The bottom sheet is partially visible at 50% of the screen height. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height and
     * [BottomSheetState.isSkipHalfExpanded] is false.
     */
    HalfExpanded
}

/**
 * State of the internal [BottomSheetLayout] composable inside [BottomSheetNavHost]. This is
 * direct analogue of [ModalBottomSheetState] from Material package.
 */
@ExperimentalMaterialApi
@Stable
class BottomSheetState internal constructor(
    internal val hostEntryId: NavId,
    initialValue: BottomSheetValue,
    isTransitionRunningState: State<Boolean>,
    animationSpec: AnimationSpec<Float>,
    internal val isSkipHalfExpanded: Boolean,
    confirmStateChange: (newValue: BottomSheetValue) -> Boolean
) : SwipeableState<BottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {

    internal constructor(
        hostEntryId: NavId,
        initialValue: BottomSheetValue,
        isTransitionRunningState: State<Boolean>,
        sheetProperties: BottomSheetProperties
    ) : this(
        hostEntryId = hostEntryId,
        initialValue = initialValue,
        isTransitionRunningState = isTransitionRunningState,
        animationSpec = sheetProperties.animationSpec,
        isSkipHalfExpanded = sheetProperties.isSkipHalfExpanded,
        confirmStateChange = sheetProperties.confirmStateChange
    )

    internal val isTransitionRunning by isTransitionRunningState

    /**
     * Whether the bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = currentValue != Hidden

    /**
     * Whether the bottom sheet has [HalfExpanded] state. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height and
     * [isSkipHalfExpanded] is set to false.
     */
    val hasHalfExpandedState: Boolean
        get() = anchors.values.contains(HalfExpanded)

    init {
        if (isSkipHalfExpanded) {
            require(initialValue != HalfExpanded) {
                "The initial value must not be set to HalfExpanded if skipHalfExpanded is set to" +
                        " true."
            }
        }
    }

    /**
     * Show the bottom sheet with animation and suspend until it's shown. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise it will be
     * fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> HalfExpanded
            else -> Expanded
        }
        animateTo(targetValue = targetValue)
    }

    /**
     * Half expand the bottom sheet if half expand is enabled with animation and suspend until the
     * animation is complete or cancelled.
     *
     * This call will be ignored if [BottomSheetNavHost] is in the middle of transition to another
     * bottom sheet.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun halfExpand() {
        if (hasHalfExpandedState && !isTransitionRunning) {
            animateTo(HalfExpanded)
        }
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it if fully expanded or
     * animation has been cancelled.
     * This call will be ignored if [BottomSheetNavHost] is in the middle of transition to another
     * bottom sheet.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun expand() {
        if (!isTransitionRunning) {
            animateTo(Expanded)
        }
    }

    /**
     * Hide the bottom sheet with animation and suspend until it if fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun hide() = animateTo(Hidden)

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

}

@ExperimentalMaterialApi
@Composable
internal fun BottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    sheetState: BottomSheetState,
    sheetShape: Shape,
    sheetElevation: Dp,
    sheetBackgroundColor: Color,
    sheetContentColor: Color,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val fullHeight = constraints.maxHeight.toFloat()
        val sheetHeightState = remember { mutableStateOf<Float?>(null) }
        Surface(
            Modifier
                .fillMaxWidth()
                .nestedScroll(sheetState.nestedScrollConnection)
                .offset {
                    val y = if (sheetState.anchors.isEmpty()) {
                        // if we don't know our anchors yet, render the sheet as hidden
                        fullHeight.roundToInt()
                    } else {
                        // if we do know our anchors, respect them
                        sheetState.offset.value.roundToInt()
                    }
                    IntOffset(0, y)
                }
                .bottomSheetSwipeable(sheetState, fullHeight, sheetHeightState)
                .onGloballyPositioned {
                    sheetHeightState.value = it.size.height.toFloat()
                }
                .semantics {
                    if (sheetState.isVisible && !sheetState.isTransitionRunning) {
                        dismiss {
                            if (sheetState.confirmStateChange(Hidden)) {
                                onDismissRequest()
                            }
                            true
                        }
                        if (sheetState.currentValue == HalfExpanded) {
                            expand {
                                if (sheetState.confirmStateChange(Expanded)) {
                                    scope.launch { sheetState.expand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) {
                            collapse {
                                if (sheetState.confirmStateChange(HalfExpanded)) {
                                    scope.launch { sheetState.halfExpand() }
                                }
                                true
                            }
                        }
                    }
                },
            shape = sheetShape,
            elevation = sheetElevation,
            color = sheetBackgroundColor,
            contentColor = sheetContentColor
        ) {
            Column(content = sheetContent)
        }
    }
}

@Suppress("ModifierInspectorInfo")
@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.bottomSheetSwipeable(
    sheetState: BottomSheetState,
    fullHeight: Float,
    sheetHeightState: State<Float?>,
): Modifier {
    val sheetHeight = sheetHeightState.value
    val modifier = if (sheetHeight != null) {
        val anchors = if (sheetHeight < fullHeight / 2 || sheetState.isSkipHalfExpanded) {
            mapOf(
                fullHeight to Hidden,
                fullHeight - sheetHeight to Expanded
            )
        } else {
            mapOf(
                fullHeight to Hidden,
                fullHeight / 2 to HalfExpanded,
                max(0f, fullHeight - sheetHeight) to Expanded
            )
        }
        Modifier.swipeable(
            state = sheetState,
            anchors = anchors,
            orientation = Orientation.Vertical,
            enabled = sheetState.isVisible && !sheetState.isTransitionRunning,
            resistance = null
        )
    } else {
        Modifier
    }

    return this.then(modifier)
}

@Composable
internal fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val closeSheet = stringResource(androidx.compose.ui.R.string.close_sheet)
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = closeSheet
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

/**
 * Contains useful Defaults for [BottomSheetLayout].
 */
internal object BottomSheetDefaults {

    /**
     * The default elevation used by [BottomSheetLayout].
     */
    val Elevation = 16.dp

    /**
     * The default scrim color used by [BottomSheetLayout].
     */
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)

    val shape: Shape
        @Composable
        get() = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        )
}

/**
 * Bottom sheet properties.
 *
 * @param animationSpec the default animation that will be used to animate to a new state
 * @param isSkipHalfExpanded whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * @param confirmStateChange optional callback invoked to confirm or veto a pending state change
 */
@ExperimentalMaterialApi
class BottomSheetProperties(
    val animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    val isSkipHalfExpanded: Boolean = false,
    val confirmStateChange: (newValue: BottomSheetValue) -> Boolean = { true }
)

/**
 * Default bottom sheet properties. May be used as a default value for those bottom sheets that
 * do not require any customization.
 */
@ExperimentalMaterialApi
val DefaultBottomSheetProperties = BottomSheetProperties()