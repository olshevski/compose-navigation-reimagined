package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Used for differentiating host id from entry ids, because when everything is named "id" it is
 * just confusing.
 */
@Parcelize
@JvmInline
internal value class NavHostId(private val id: NavId = NavId()) : Parcelable {
    override fun toString(): String = id.toString()
}