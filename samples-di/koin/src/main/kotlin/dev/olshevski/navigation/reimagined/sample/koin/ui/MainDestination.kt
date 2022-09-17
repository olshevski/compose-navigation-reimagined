package dev.olshevski.navigation.reimagined.sample.koin.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MainDestination : Parcelable {

    @Parcelize
    object First : MainDestination()

    @Parcelize
    class Second(val id: Int) : MainDestination()

    @Parcelize
    class Third(val text: String) : MainDestination()

}