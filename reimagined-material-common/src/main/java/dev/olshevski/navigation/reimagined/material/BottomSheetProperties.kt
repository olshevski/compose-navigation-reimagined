package dev.olshevski.navigation.reimagined.material

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Immutable
import dev.olshevski.navigation.reimagined.ExperimentalReimaginedApi
import dev.olshevski.navigation.reimagined.material.BottomSheetValue.Expanded
import dev.olshevski.navigation.reimagined.material.BottomSheetValue.Hidden

/**
 * Bottom sheet properties.
 *
 * @param animationSpec the default animation that will be used to animate to a new state
 *
 * @param confirmValueChange optional callback invoked to confirm or veto a pending value change
 *
 * @param skipHalfExpanded whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 */
@Immutable
class BottomSheetProperties(
    val animationSpec: AnimationSpec<Float> = @OptIn(ExperimentalReimaginedApi::class) (SwipeableV2Defaults.AnimationSpec),
    val confirmValueChange: (newValue: BottomSheetValue) -> Boolean = { true },
    val skipHalfExpanded: Boolean = false
) {

    @Deprecated(
        message = "This constructor is deprecated. `confirmStateChange` has been renamed to " +
                "`confirmValueChange`, `isSkipHalfExpanded` has been renamed to `skipHalfExpanded`",
        ReplaceWith("BottomSheetProperties(animationSpec, confirmStateChange, isSkipHalfExpanded)")
    )
    constructor(
        animationSpec: AnimationSpec<Float> = @OptIn(ExperimentalReimaginedApi::class) (SwipeableV2Defaults.AnimationSpec),
        isSkipHalfExpanded: Boolean = false,
        confirmStateChange: (newValue: BottomSheetValue) -> Boolean = { true }
    ) : this(
        animationSpec = animationSpec,
        skipHalfExpanded = isSkipHalfExpanded,
        confirmValueChange = confirmStateChange
    )

}

/**
 * Default bottom sheet properties. May be used as a default value for those bottom sheets that
 * do not require any customization.
 */
val DefaultBottomSheetProperties = BottomSheetProperties(
    confirmValueChange = { true }
)

/**
 * Interface to specify [BottomSheetProperties] for every BottomSheet destination
 */
fun interface BottomSheetPropertiesSpec<in T> {

    /**
     * Provides [BottomSheetProperties] for a [destination].
     */
    fun getBottomSheetProperties(destination: T): BottomSheetProperties

}

val DefaultBottomSheetPropertiesSpec = BottomSheetPropertiesSpec<Any?> {
    DefaultBottomSheetProperties
}

/**
 * Defines same [BottomSheetProperties] for all destinations.
 */
fun commonBottomSheetProperties(
    animationSpec: AnimationSpec<Float> = @OptIn(ExperimentalReimaginedApi::class) (SwipeableV2Defaults.AnimationSpec),
    confirmValueChange: (newValue: BottomSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = false
) = object : BottomSheetPropertiesSpec<Any?> {

    private val bottomSheetProperties = BottomSheetProperties(
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        skipHalfExpanded = skipHalfExpanded
    )

    override fun getBottomSheetProperties(destination: Any?) = bottomSheetProperties

}