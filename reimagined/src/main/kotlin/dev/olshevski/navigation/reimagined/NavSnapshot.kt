package dev.olshevski.navigation.reimagined

data class NavSnapshot<out T> internal constructor(
    val hostEntries: List<NavHostEntry<T>>,
    val action: NavAction
)