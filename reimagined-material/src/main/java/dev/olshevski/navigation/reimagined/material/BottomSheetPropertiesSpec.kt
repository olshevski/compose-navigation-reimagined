package dev.olshevski.navigation.reimagined.material

import androidx.compose.material.ExperimentalMaterialApi

fun interface BottomSheetPropertiesSpec<in T> {

    @ExperimentalMaterialApi
    fun getBottomSheetProperties(destination: T): BottomSheetProperties

}

@ExperimentalMaterialApi
val DefaultBottomSheetProperties = BottomSheetProperties()

@ExperimentalMaterialApi
internal val DefaultBottomSheetPropertiesSpec = BottomSheetPropertiesSpec<Any?> {
    DefaultBottomSheetProperties
}