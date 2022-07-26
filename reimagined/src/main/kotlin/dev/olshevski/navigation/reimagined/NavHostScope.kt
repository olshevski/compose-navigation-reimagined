package dev.olshevski.navigation.reimagined

interface NavHostScope<out T> {

    val backstack: NavBackstack<T>

    val currentHostEntry: NavHostEntry<T>

    fun getHostEntry(id: NavId): NavHostEntry<T>?

}

internal open class NavHostScopeImpl<out T>(
    override val backstack: NavBackstack<T>,
    override val currentHostEntry: NavHostEntry<T>,
    private val hostStateScope: NavHostStateScope<T>
) : NavHostScope<T> {

    override fun getHostEntry(id: NavId): NavHostEntry<T>? = hostStateScope.getHostEntry(id)

}

fun <T> NavHostScope<T>.findHostEntry(
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
        getHostEntry(entry.id)
    }
}