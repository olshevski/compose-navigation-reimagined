package dev.olshevski.navigation.reimagined.sample.hilt.hiltviewmodel.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MainDestination : Parcelable {

    @Parcelize
    object First : MainDestination()

    @Parcelize
    data class Second(val id: Int) : MainDestination()

    @Parcelize
    data class Third(val text: String) : MainDestination()

}