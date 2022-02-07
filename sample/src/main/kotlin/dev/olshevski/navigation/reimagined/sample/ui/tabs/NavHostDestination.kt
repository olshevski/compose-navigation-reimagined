package dev.olshevski.navigation.reimagined.sample.ui.tabs

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * In many cases Compose can detect stability/immutability by itself. In some cases you may
 * choose to mark it explicitly.
 */
@Stable
sealed class NavHostDestination : Parcelable {

    @Immutable
    @Parcelize
    object First : NavHostDestination()

    @Immutable
    @Parcelize
    data class Second(val id: Int) : NavHostDestination()

    @Immutable
    @Parcelize
    object Third : NavHostDestination()

    /*
     * 1) The type may be @Stable, not only @Immutable. This guarantee is backed up by MutableState
     *    here.
     * 2) MutableState is Parcelable, it's just doesn't expose this interface, so we are fine.
     */
    @Stable
    @Parcelize
    data class Forth(
        override val resultFromFifth: @RawValue MutableState<String?> = mutableStateOf(null)
    ) : NavHostDestination(), AcceptsResultFromFifth

    @Immutable
    @Parcelize
    object Fifth : NavHostDestination()

}

interface AcceptsResultFromFifth {
    val resultFromFifth: MutableState<String?>
}