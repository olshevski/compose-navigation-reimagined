package dev.olshevski.navigation.reimagined

import androidx.lifecycle.ViewModelStoreOwner

interface BaseNavHostScope<out T> {

    fun getSharedViewModelStoreOwner(key: NavKey, associatedEntryId: NavId): ViewModelStoreOwner

}

internal class BaseNavHostScopeImpl<out T>(
    private val state: NavHostState<T>
) : BaseNavHostScope<T> {

    override fun getSharedViewModelStoreOwner(key: NavKey, associatedEntryId: NavId) =
        state.getSharedViewModelStoreOwner(key, associatedEntryId)

}