package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PassValuesDestination : Parcelable {

    @Parcelize
    data object A : PassValuesDestination()

    @Parcelize
    data class B(val id: Int) : PassValuesDestination()

    @Parcelize
    data class C(val text: String) : PassValuesDestination()

}