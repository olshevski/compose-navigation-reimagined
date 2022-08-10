package dev.olshevski.navigation.reimagined

interface NavHostScope<out T> {
    val hostEntries: List<NavHostEntry<T>>
}

internal open class NavHostScopeImpl<out T>(
    override val hostEntries: List<NavHostEntry<T>>
) : NavHostScope<T>

val <T> NavHostScope<T>.currentHostEntry: NavHostEntry<T> get() = hostEntries.last()

fun <T> NavHostScope<T>.findHostEntry(
    match: Match = Match.Last,
    predicate: (T) -> Boolean
): NavHostEntry<T>? {
    val entryPredicate: (NavHostEntry<T>) -> Boolean = { predicate(it.destination) }
    return hostEntries.run {
        when (match) {
            Match.First -> find(entryPredicate)
            Match.Last -> findLast(entryPredicate)
        }
    }
}