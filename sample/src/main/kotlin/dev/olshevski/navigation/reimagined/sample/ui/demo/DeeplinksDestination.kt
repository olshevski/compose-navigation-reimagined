package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class DeeplinksDestination : Parcelable {

    @Parcelize
    object First : DeeplinksDestination()

    @Parcelize
    object Second : DeeplinksDestination()

    @Parcelize
    data class Third(val id: String) : DeeplinksDestination()

}