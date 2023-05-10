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

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
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
 * Based on ModalBottomSheet.kt from androidx.compose.material package (last commit 8cfec3c).
 *
 * Reference:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/ModalBottomSheet.kt
 */

/**
 * Possible values of [BottomSheetState].
 */
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
     * [BottomSheetState.skipHalfExpanded] is false.
     */
    HalfExpanded
}

/**
 * State of the internal [BottomSheetLayout] composable inside [CommonBottomSheetNavHost].
 * This is direct analogue of `ModalBottomSheetState` from Material package.
 */
@Stable
class BottomSheetState internal constructor(
    internal val hostEntryId: NavId,
    initialValue: BottomSheetValue,
    isTransitionRunningState: State<Boolean>,
    animationSpec: AnimationSpec<Float>,
    internal val skipHalfExpanded: Boolean,
    confirmValueChange: (BottomSheetValue) -> Boolean
) {

    init {
        if (skipHalfExpanded) {
            require(initialValue != HalfExpanded) {
                "The initial value must not be set to HalfExpanded if skipHalfExpanded is set to" +
                        " true."
            }
        }
    }

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
        skipHalfExpanded = sheetProperties.skipHalfExpanded,
        confirmValueChange = sheetProperties.confirmValueChange
    )

    internal val isTransitionRunning by isTransitionRunningState

    internal val swipeableState = SwipeableV2State(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        positionalThreshold = PositionalThreshold,
        velocityThreshold = VelocityThreshold
    )

    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the state the bottom sheet is
     * currently in. If a swipe or an animation is in progress, this corresponds the state the sheet
     * was in before the swipe or animation started.
     */
    val currentValue: BottomSheetValue
        get() = swipeableState.currentValue

    /**
     * The target value of the bottom sheet state.
     *
     * If a swipe is in progress, this is the value that the sheet would animate to if the
     * swipe finishes. If an animation is running, this is the target value of that animation.
     * Finally, if no swipe or animation is in progress, this is the same as the [currentValue].
     */
    val targetValue: BottomSheetValue
        get() = swipeableState.targetValue

    /**
     * Whether the bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = swipeableState.currentValue != Hidden

    /**
     * Whether the bottom sheet has [HalfExpanded] state. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height and
     * [skipHalfExpanded] is set to false.
     */
    val hasHalfExpandedState: Boolean
        get() = swipeableState.hasAnchorForValue(HalfExpanded)

    /**
     * Offset (in pixels) from the top of the sheet layout. Zero offset means that the bottom sheet
     * takes up the whole layout height.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val offset: Int by derivedStateOf {
        swipeableState.offset
            ?.coerceIn(swipeableState.minOffset, swipeableState.maxOffset)
            ?.roundToInt()
            ?: Int.MAX_VALUE
    }

    /**
     * Whether the bottom sheet is in [Expanded] state and takes up the whole layout height.
     */
    @Suppress("unused")
    val isFullyExpanded: Boolean by derivedStateOf {
        offset <= 0
    }

    /**
     * Show the bottom sheet with animation and suspend until it's shown. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise, it will
     * be fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> HalfExpanded
            else -> Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Half expand the bottom sheet if half expand is enabled with animation and suspend until its
     * animation is complete or cancelled.
     *
     * This call will be ignored if [CommonBottomSheetNavHost] is in the middle of transition
     * to another bottom sheet.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun halfExpand() {
        if (!hasHalfExpandedState || isTransitionRunning) {
            return
        }
        animateTo(HalfExpanded)
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
     * animation has been cancelled.
     *
     * This call will be ignored if [CommonBottomSheetNavHost] is in the middle of transition
     * to another bottom sheet.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun expand() {
        if (!swipeableState.hasAnchorForValue(Expanded) || isTransitionRunning) {
            return
        }
        animateTo(Expanded)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it is fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun hide() = animateTo(Hidden)

    /**
     * Animate to a [targetValue].
     * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
     * [targetValue] without updating the offset.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    internal suspend fun animateTo(
        targetValue: BottomSheetValue,
        velocity: Float = swipeableState.lastVelocity
    ) = swipeableState.animateTo(targetValue, velocity)

    /**
     * Snap to a [targetValue] without any animation.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    internal suspend fun snapTo(targetValue: BottomSheetValue) = swipeableState.snapTo(targetValue)

    internal fun trySnapTo(targetValue: BottomSheetValue): Boolean {
        return swipeableState.trySnapTo(targetValue)
    }

    /**
     * Require the current offset (in pixels) of the bottom sheet.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    @Suppress("unused")
    internal fun requireOffset() = swipeableState.requireOffset()

    internal val lastVelocity: Float get() = swipeableState.lastVelocity

    internal val isAnimationRunning: Boolean get() = swipeableState.isAnimationRunning

}

@Composable
internal fun BottomSheetLayout(
    modifier: Modifier,
    sheetContent: @Composable BoxScope.() -> Unit,
    sheetState: BottomSheetState,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val anchorChangeHandler = remember(sheetState, scope) {
        ModalBottomSheetAnchorChangeHandler(
            state = sheetState,
            animateTo = { target, velocity ->
                scope.launch { sheetState.animateTo(target, velocity = velocity) }
            },
            snapTo = { target ->
                val didSnapSynchronously = sheetState.trySnapTo(target)
                if (!didSnapSynchronously) scope.launch { sheetState.snapTo(target) }
            }
        )
    }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val fullHeight = constraints.maxHeight.toFloat()
        Box(
            Modifier
                .align(Alignment.TopCenter) // We offset from the top, so we'll center from there
                .widthIn(max = MaxModalBottomSheetWidth)
                .fillMaxWidth()
                .nestedScroll(
                    remember(sheetState.swipeableState) {
                        ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                            state = sheetState.swipeableState,
                            orientation = Orientation.Vertical
                        )
                    }
                )
                .offset {
                    IntOffset(
                        0,
                        sheetState.swipeableState.let { swipeableState ->
                            // FIXED: Offset is coerced here, so bottom sheet is not overshoot
                            // above the top line of the screen.
                            swipeableState
                                .requireOffset()
                                .coerceIn(swipeableState.minOffset, swipeableState.maxOffset)
                                .roundToInt()
                        }
                    )
                }
                .swipeableV2(
                    state = sheetState.swipeableState,
                    orientation = Orientation.Vertical,
                    enabled = sheetState.isVisible && !sheetState.isTransitionRunning,
                )
                .swipeAnchors(
                    state = sheetState.swipeableState,
                    possibleValues = setOf(Hidden, HalfExpanded, Expanded),
                    anchorChangeHandler = anchorChangeHandler
                ) { state, sheetSize ->
                    when (state) {
                        Hidden -> fullHeight
                        HalfExpanded -> when {
                            sheetSize.height < fullHeight / 2f -> null
                            sheetState.skipHalfExpanded -> null
                            else -> fullHeight / 2f
                        }

                        Expanded -> if (sheetSize.height != 0) {
                            max(0f, fullHeight - sheetSize.height)
                        } else null
                    }
                }
                .semantics {
                    if (sheetState.isVisible && !sheetState.isTransitionRunning) {
                        dismiss {
                            if (sheetState.swipeableState.confirmValueChange(Hidden)) {
                                onDismissRequest()
                            }
                            true
                        }
                        if (sheetState.swipeableState.currentValue == HalfExpanded) {
                            expand {
                                if (sheetState.swipeableState.confirmValueChange(Expanded)) {
                                    scope.launch { sheetState.expand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) {
                            collapse {
                                if (sheetState.swipeableState.confirmValueChange(HalfExpanded)) {
                                    scope.launch { sheetState.halfExpand() }
                                }
                                true
                            }
                        }
                    }
                },
            content = sheetContent
        )
    }
}

@Composable
internal fun Scrim(
    color: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )

        @SuppressLint("PrivateResource")
        val closeSheet = stringResource(androidx.compose.ui.R.string.close_sheet)
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = closeSheet
                    onClick { onDismissRequest(); true }
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

@Suppress("FunctionName")
private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    state: SwipeableV2State<*>,
    @Suppress("SameParameterValue") orientation: Orientation
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = state.requireOffset()
        return if (toFling < 0 && currentOffset > state.minOffset) {
            state.settle(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.settle(velocity = available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() =
        if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float =
        if (orientation == Orientation.Horizontal) x else y
}

@Suppress("FunctionName", "RemoveExplicitTypeArguments")
private fun ModalBottomSheetAnchorChangeHandler(
    state: BottomSheetState,
    animateTo: (target: BottomSheetValue, velocity: Float) -> Unit,
    snapTo: (target: BottomSheetValue) -> Unit,
) = AnchorChangeHandler<BottomSheetValue> { previousTarget, previousAnchors, newAnchors ->
    val previousTargetOffset = previousAnchors[previousTarget]
    // FIXED: New target selection is fixed here, the original implementation had an issue when
    // HalfExpanded state was selected incorrectly instead of Expanded state.
    // Now the selection priorities are correct.
    val newTarget = when (previousTarget) {
        Hidden -> Hidden
        HalfExpanded -> {
            when {
                newAnchors.containsKey(HalfExpanded) -> HalfExpanded
                newAnchors.containsKey(Expanded) -> Expanded
                else -> Hidden
            }
        }
        Expanded -> {
            when {
                newAnchors.containsKey(Expanded) -> Expanded
                newAnchors.containsKey(HalfExpanded) -> HalfExpanded
                else -> Hidden
            }
        }
    }
    val newTargetOffset = newAnchors.getValue(newTarget)
    if (newTargetOffset != previousTargetOffset) {
        if (state.isAnimationRunning) {
            // Re-target the animation to the new offset if it changed
            animateTo(newTarget, state.lastVelocity)
        } else {
            // Snap to the new offset value of the target if no animation was running
            snapTo(newTarget)
        }
    }
}

private val PositionalThreshold: Density.(Float) -> Float = { 56.dp.toPx() }
private val VelocityThreshold = 125.dp
private val MaxModalBottomSheetWidth = 640.dp