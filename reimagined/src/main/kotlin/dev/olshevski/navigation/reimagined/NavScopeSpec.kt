package dev.olshevski.navigation.reimagined

fun interface NavScopeSpec<in T> {

    fun getDestinationScopes(destination: T): Set<NavScope>

}

internal val EmptyScopeSpec = NavScopeSpec<Any?> { emptySet() }