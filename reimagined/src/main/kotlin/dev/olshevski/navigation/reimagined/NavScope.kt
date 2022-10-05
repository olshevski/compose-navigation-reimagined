package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Stable
@JvmInline
value class NavScope internal constructor(private val key: @RawValue Any) : Parcelable

fun navScope(key: Any): NavScope {
    require(key !is NavScope) { "Do not place NavScope inside a NavScope" }
    return NavScope(key)
}