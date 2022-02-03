package dev.olshevski.navigation.reimagined.sample.ui.tabs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ViewModelDestination : Parcelable {

    @Parcelize
    object First : ViewModelDestination()

    @Parcelize
    object Second : ViewModelDestination()

    @Parcelize
    data class Third(val text: String) : ViewModelDestination()

}