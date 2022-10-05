package dev.olshevski.navigation.reimagined

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStore

/**
 * @param scope User key of this entry.
 */
@Stable
class ScopedNavHostEntry internal constructor(
    id: NavId,
    val scope: NavScope,
    viewModelStore: ViewModelStore,
    application: Application?
) : BaseNavHostEntry(id, viewModelStore, application) {

    override fun toString() = "SharedNavHostEntry(id=$id, scope=$scope)"

}