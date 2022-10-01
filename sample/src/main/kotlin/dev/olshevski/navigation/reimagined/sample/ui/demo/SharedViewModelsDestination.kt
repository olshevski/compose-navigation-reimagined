package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class SharedViewModelsDestination : Parcelable {

    @Parcelize
    object First : SharedViewModelsDestination()

    @Parcelize
    object Second : SharedViewModelsDestination()

    @Parcelize
    object Third : SharedViewModelsDestination()

}