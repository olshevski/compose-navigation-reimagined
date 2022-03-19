package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ViewModelsDestination : Parcelable {

    @Parcelize
    object First : ViewModelsDestination()

    @Parcelize
    object Second : ViewModelsDestination()

    @Parcelize
    data class Third(val text: String) : ViewModelsDestination()

}