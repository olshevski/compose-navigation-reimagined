package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStoreOwner

@Stable
interface ScopedNavHostScope<out T, S> : NavHostScope<T> {

    fun getScopedViewModelStoreOwner(scope: S): ViewModelStoreOwner

}

internal open class ScopedNavHostScopeImpl<out T, S>(
    override val hostEntries: List<NavHostEntry<T>>,
    private val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>
) : ScopedNavHostScope<T, S> {

    override fun getScopedViewModelStoreOwner(scope: S): ViewModelStoreOwner =
        scopedHostEntries[scope]
            ?: error("You should associate the scope ($scope) with the destination (${currentHostEntry.destination}) in a scopeSpec")

}
