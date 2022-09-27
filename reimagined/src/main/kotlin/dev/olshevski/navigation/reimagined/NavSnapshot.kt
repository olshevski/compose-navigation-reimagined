package dev.olshevski.navigation.reimagined

internal data class NavSnapshot<out T> internal constructor(
    val hostEntries: List<NavHostEntry<T>>,
    val action: NavAction,
    internal val outdatedEntryIds: List<NavId>
)