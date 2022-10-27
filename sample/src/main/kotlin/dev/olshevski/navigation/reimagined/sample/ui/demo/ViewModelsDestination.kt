package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ViewModelsDestination : Parcelable {

    @Parcelize
    data object First : ViewModelsDestination()

    @Parcelize
    data object Second : ViewModelsDestination()

    @Parcelize
    data class Third(val text: String) : ViewModelsDestination()

}