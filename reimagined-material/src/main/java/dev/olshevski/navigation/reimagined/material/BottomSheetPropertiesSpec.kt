package dev.olshevski.navigation.reimagined.material

import androidx.compose.material.ExperimentalMaterialApi

/**
 * Interface to specify [BottomSheetProperties] for every BottomSheet destination
 */
fun interface BottomSheetPropertiesSpec<in T> {

    @ExperimentalMaterialApi
    fun getBottomSheetProperties(destination: T): BottomSheetProperties

}

@ExperimentalMaterialApi
internal val DefaultBottomSheetPropertiesSpec = BottomSheetPropertiesSpec<Any?> {
    DefaultBottomSheetProperties
}