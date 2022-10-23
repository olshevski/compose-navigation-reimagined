package dev.olshevski.navigation.reimagined

fun interface NavScopeSpec<in T, out S> {

    fun getScopes(destination: T): Set<S>

}

val EmptyScopeSpec = NavScopeSpec<Any?, Nothing> { emptySet() }