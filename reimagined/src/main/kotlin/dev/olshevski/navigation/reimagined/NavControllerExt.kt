package dev.olshevski.navigation.reimagined

/**
 * Adds new [destinations] to the backstack.
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [destinations] list may be empty.
 */
fun <T> NavController<T>.navigate(destinations: List<T>) {
    setNewBackstackEntries(
        entries = backstack.entries + destinations.map(::navEntry),
        action = NavAction.Navigate
    )
}

/**
 * Adds a new [destination] to the backstack. It will become the currently displayed item
 * in [NavHost].
 */
fun <T> NavController<T>.navigate(destination: T) =
    navigate(listOf(destination))

/**
 * Looks for the destination that meet the condition of the [predicate] and moves this destination
 * to the top of the backstack, effectively becoming the currently displayed item in [NavHost].
 *
 * In most cases this operation should be used together with [navigate]:
 *
 * ```
 * if (!navController.moveToTop { it is SomeDestination }) {
 *     // navigate to a new destination if there is no existing one
 *     navController.navigate(SomeDestination())
 * }
 * ```
 *
 * @param match specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the last matching item from the start of the backstack will be considered
 * the destination to move.
 *
 * @return `true` - if the item matching the predicate was found, `false` - otherwise
 */
fun <T> NavController<T>.moveToTop(match: Match = Match.Last, predicate: (T) -> Boolean): Boolean {
    val entryIndex = backstack.entries.indexOf(match, predicate)
    return if (entryIndex >= 0) {
        setNewBackstackEntries(
            entries = if (entryIndex == backstack.entries.lastIndex) {
                backstack.entries
            } else {
                backstack.entries.toMutableList().also {
                    val entry = it.removeAt(entryIndex)
                    it.add(entry)
                }
            },
            action = NavAction.Navigate
        )
        true
    } else {
        false
    }
}

/**
 * Pops the last destination off the backstack.
 *
 * @return `true` - if the item was successfully popped. If there are no items in the backstack,
 * `false` is returned, meaning that nothing has changed.
 */
fun <T> NavController<T>.pop(): Boolean = if (backstack.entries.isNotEmpty()) {
    setNewBackstackEntries(
        entries = backstack.entries.dropLast(1),
        action = NavAction.Pop
    )
    true
} else {
    false
}

/**
 * Pops all destinations off the backstack, making it empty.
 */
fun <T> NavController<T>.popAll() {
    setNewBackstackEntries(
        entries = emptyList(),
        action = NavAction.Pop
    )
}

/**
 * Pops all destinations off the backstack up to the destination that meets the condition
 * of the [upToPredicate].
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param match specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the last matching item from the start of the backstack will be considered
 * the point up to which to pop.
 *
 * @return `true` - if the item matching the predicate was found, `false` - otherwise
 */
fun <T> NavController<T>.popUpTo(
    inclusive: Boolean = false,
    match: Match = Match.Last,
    upToPredicate: (T) -> Boolean
): Boolean {
    val entryIndex = backstack.entries.indexOf(match, upToPredicate)
    return if (entryIndex >= 0) {
        setNewBackstackEntries(
            entries = backstack.entries.subList(
                fromIndex = 0,
                toIndex = if (inclusive) entryIndex else entryIndex + 1
            ),
            action = NavAction.Pop
        )
        true
    } else {
        false
    }
}

/**
 * Pops the last destination off the backstack and replaces it with [newDestinations].
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * @return If the backstack is empty, meaning there is no last item, `false` is returned and
 * the [newDestinations] are not added to the backstack. If there is at least one destination,
 * it is replaced as expected and `true` is returned.
 */
fun <T> NavController<T>.replaceLast(newDestinations: List<T>): Boolean =
    if (backstack.entries.isNotEmpty()) {
        setNewBackstackEntries(
            entries = backstack.entries.dropLast(1) + newDestinations.map(::navEntry),
            action = NavAction.Replace
        )
        true
    } else {
        false
    }

/**
 * Pops the last destination off the backstack and replaces it with a [newDestination].
 *
 * @return If the backstack is empty, meaning there is no last item, `false` is returned and
 * the [newDestination] is not added to the backstack. If there is at least one destination,
 * it is replaced as expected and `true` is returned.
 */
fun <T> NavController<T>.replaceLast(newDestination: T): Boolean =
    replaceLast(listOf(newDestination))

/**
 * Pops all destinations off the backstack and replaces them with [newDestinations].
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * If the backstack is empty, the [newDestinations] are still added.
 */
fun <T> NavController<T>.replaceAll(newDestinations: List<T>) {
    setNewBackstackEntries(
        entries = newDestinations.map(::navEntry),
        action = NavAction.Replace
    )
}

/**
 * Pops all destinations off the backstack and replaces them with a [newDestination].
 *
 * If the backstack is empty, the [newDestination] is still added.
 */
fun <T> NavController<T>.replaceAll(newDestination: T) =
    replaceAll(listOf(newDestination))

/**
 * Pops all destinations off the backstack up to the destination that meets the condition
 * of the [upToPredicate] and replaces them with [newDestinations].
 *
 * The order of the items in the list is interpreted as going from the bottom of the backstack
 * to the top. It means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param match specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the last matching item from the start of the backstack will be considered
 * the point up to which to replace.
 *
 * @return `true` - if the item matching the predicate was found, `false` - otherwise
 */
fun <T> NavController<T>.replaceUpTo(
    newDestinations: List<T>,
    inclusive: Boolean = false,
    match: Match = Match.Last,
    upToPredicate: (T) -> Boolean
): Boolean {
    val entryIndex = backstack.entries.indexOf(match, upToPredicate)
    return if (entryIndex >= 0) {
        setNewBackstackEntries(
            entries = backstack.entries.subList(
                fromIndex = 0,
                toIndex = if (inclusive) entryIndex else entryIndex + 1
            ) + newDestinations.map(::navEntry),
            action = NavAction.Replace
        )
        true
    } else {
        false
    }
}

/**
 * Pops all destinations off the backstack up to the destination that meets the condition
 * of the [upToPredicate] and replaces them with a [newDestination].
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param match specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the last matching item from the start of the backstack will be considered
 * the point up to which to replace.
 *
 * @return `true` - if the item matching the predicate was found, `false` - otherwise
 */
fun <T> NavController<T>.replaceUpTo(
    newDestination: T,
    inclusive: Boolean = false,
    match: Match = Match.Last,
    upToPredicate: (T) -> Boolean
): Boolean = replaceUpTo(listOf(newDestination), inclusive, match, upToPredicate)

/**
 * The policy of selecting the target item in case of multiple matching items.
 */
enum class Match {

    /**
     * Selects the first item from the start of the backstack that matches the predicate.
     */
    First,

    /**
     * Selects the last item from the start of the backstack that matches the predicate.
     */
    Last
}

private fun <T> List<NavEntry<T>>.indexOf(
    match: Match,
    predicate: (T) -> Boolean
): Int {
    val entryPredicate: (NavEntry<T>) -> Boolean = { predicate(it.destination) }
    return when (match) {
        Match.First -> indexOfFirst(entryPredicate)
        Match.Last -> indexOfLast(entryPredicate)
    }
}