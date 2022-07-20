package dev.olshevski.navigation.reimagined

interface NavHostScope<out T> {

    val backstack: NavBackstack<T>

    val currentNavHostEntry: NavHostEntry<T>

    fun getNavHostEntry(navId: NavId): NavHostEntry<T>?

}

internal open class NavHostScopeImpl<out T>(
    override val backstack: NavBackstack<T>,
    override val currentNavHostEntry: NavHostEntry<T>,
    private val navHostStateScope: NavHostStateScope<T>
) : NavHostScope<T> {

    override fun getNavHostEntry(navId: NavId): NavHostEntry<T>? =
        navHostStateScope.getNavHostEntry(navId)

}

fun <T> NavHostScope<T>.findNavHostEntry(
    match: Match = Match.Last,
    predicate: (T) -> Boolean
): NavHostEntry<T>? {
    val entryPredicate: (NavEntry<T>) -> Boolean = { predicate(it.destination) }
    return backstack.entries.run {
        when (match) {
            Match.First -> find(entryPredicate)
            Match.Last -> findLast(entryPredicate)
        }
    }?.let { entry ->
        getNavHostEntry(entry.id)
    }
}