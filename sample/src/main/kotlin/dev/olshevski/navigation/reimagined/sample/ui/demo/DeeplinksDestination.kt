package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class DeeplinksDestination : Parcelable {

    @Parcelize
    data object First : DeeplinksDestination()

    @Parcelize
    data object Second : DeeplinksDestination()

    @Parcelize
    data class Third(val id: String) : DeeplinksDestination()

}