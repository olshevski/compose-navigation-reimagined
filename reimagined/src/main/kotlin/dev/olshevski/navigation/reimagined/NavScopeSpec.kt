package dev.olshevski.navigation.reimagined

import androidx.lifecycle.ViewModelStoreOwner

/**
 * Specifies scopes for every destination. This gives you the ability to easily create and access
 * scoped [ViewModelStoreOwners][ViewModelStoreOwner].
 *
 * To do so, you must return a desired set of scopes for each requested destination in
 * [getScopes]. This information will then be used to associate different entries to specified
 * scopes and keep each scoped ViewModelStoreOwner until any of its associated entries is present
 * in the backstack. When none of the entries are present anymore, the scoped ViewModelStoreOwner
 * and all of its ViewModels will be cleared.
 *
 * To access a scoped ViewModelStoreOwner, you may call
 * [ScopingNavHostScope.getScopedViewModelStoreOwner] inside `contentSelector` of [ScopingNavHost],
 * [ScopingAnimatedNavHost] or other `Scoping...NavHost` implementation with the same scope
 * object you've returned in [getScopes]. Then you may pass this scoped ViewModelStoreOwner
 * as a parameter into a ViewModel provider method of choice and create shared ViewModels,
 * easily accessible from different destinations.
 */
fun interface NavScopeSpec<in T, out S> {

    /**
     * Returns the set of scopes associated with this [destination].
     */
    fun getScopes(destination: T): Set<S>

}

/**
 * No scopes are associated with any of the entries. May be used to effectively disable scoping.
 */
val EmptyScopeSpec = NavScopeSpec<Any?, Nothing> { emptySet() }