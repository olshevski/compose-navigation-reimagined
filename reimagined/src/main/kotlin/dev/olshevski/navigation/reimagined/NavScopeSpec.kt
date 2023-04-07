package dev.olshevski.navigation.reimagined

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Specifies scopes for every destination. This gives you the ability to easily create and access
 * scoped [ViewModelStore][ViewModelStore].
 *
 * To do so, you must return a set of desired scopes for each requested destination from
 * [getScopes]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStore
 * and all of its ViewModels will be cleared.
 *
 * Scoped ViewModelStoreOwner is implemented by [ScopedNavHostEntry] class. You can acquire all
 * scoped entries associated with the current destination through the [ScopingNavHostScope]
 * receiver of the `contentSelector` parameter. To be specific, the map of all scoped entries is
 * accessible through [ScopingNavHostScope.scopedHostEntries] property. Keys in this map
 * are the same scope objects you've returned from [getScopes].
 *
 * You may pass then any ScopedNavHostEntry into [viewModel] method as
 * the `viewModelStoreOwner` parameter and create shared ViewModels, accessible
 * from different destinations.
 *
 * Alternatively, you can access scoped ViewModelStoreOwners through the
 * [LocalScopedViewModelStoreOwners] composition local.
 */
fun interface NavScopeSpec<in T, out S> {

    /**
     * Returns the set of scopes associated with this [destination]. Each returned object
     * in the set must be immutable and writable to Parcel - it could be Parcelable, Serializable,
     * string/primitive, or other supported type.
     */
    fun getScopes(destination: T): Set<S>

}

/**
 * No scopes are associated with any of the entries. May be used to effectively disable scoping.
 */
val EmptyScopeSpec = NavScopeSpec<Any?, Nothing> { emptySet() }