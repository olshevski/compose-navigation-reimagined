package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Stable
@JvmInline
value class NavKey internal constructor(private val key: @RawValue Any) : Parcelable

fun navKey(key: Any) = NavKey(key)