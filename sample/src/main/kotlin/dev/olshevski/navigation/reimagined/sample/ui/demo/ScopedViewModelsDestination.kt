package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ScopedViewModelsDestination : Parcelable {

    @Parcelize
    object First : ScopedViewModelsDestination()

    @Parcelize
    object Second : ScopedViewModelsDestination()

    @Parcelize
    object Third : ScopedViewModelsDestination()

}