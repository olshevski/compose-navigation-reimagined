package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PassValuesDestination : Parcelable {

    @Parcelize
    object A : PassValuesDestination()

    @Parcelize
    class B(val id: Int) : PassValuesDestination()

    @Parcelize
    class C(val text: String) : PassValuesDestination()
    
}