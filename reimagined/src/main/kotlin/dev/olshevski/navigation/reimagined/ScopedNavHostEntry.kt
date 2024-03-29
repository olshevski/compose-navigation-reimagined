package dev.olshevski.navigation.reimagined

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStore

@Stable
class ScopedNavHostEntry<out S> internal constructor(
    id: NavId,
    val scope: S,
    viewModelStore: ViewModelStore,
    application: Application?
) : BaseNavHostEntry(id, viewModelStore, application) {

    override fun toString() = "ScopedNavHostEntry(id=$id, scope=$scope)"

}