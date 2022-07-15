package dev.olshevski.navigation.reimagined.sample.ui.demo

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class ReturnResultsDestination : Parcelable {

    /*
    * 1) The type may be @Stable, not only @Immutable. This guarantee is backed up by MutableState
    *    here.
    * 2) MutableState is Parcelable, it just doesn't expose this interface, so we are fine.
    */
    @Stable
    @Parcelize
    data class First(
        override val resultFromSecond: @RawValue MutableState<String?> = mutableStateOf(null)
    ) : ReturnResultsDestination(), AcceptsResultFromSecond

    @Immutable
    @Parcelize
    object Second : ReturnResultsDestination()

}

interface AcceptsResultFromSecond {
    val resultFromSecond: MutableState<String?>
}