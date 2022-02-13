package dev.olshevski.navigation.reimagined

/**
 * Adds the [destinations] to the backstack.
 *
 * The order of items in the list is interpreted as going from the bottom of the backstack
 * to the top. This means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [destinations] list may be empty and in this case nothing is changed.
 */
fun <T> NavController<T>.navigate(destinations: Collection<T>) {
    if (destinations.isNotEmpty()) {
        setNewBackstackEntries(
            entries = backstack.entries + destinations.map(::navEntry),
            action = NavAction.Navigate
        )
    }
}

/**
 * Adds the [destination] to the backstack. It will become the currently displayed item
 * in [NavHost].
 */
fun <T> NavController<T>.navigate(destination: T) =
    navigate(listOf(destination))

/**
 * Pops the last destination off the backstack.
 *
 * @return `true` - if the item was popped. If there are no items in the backstack, `false`
 * is returned, meaning that nothing has been changed.
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
 *
 * @return `true` - if the items were popped. If there are no items in the backstack, `false`
 * is returned, meaning that nothing has been changed.
 */
fun <T> NavController<T>.popAll(): Boolean = if (backstack.entries.isNotEmpty()) {
    setNewBackstackEntries(
        entries = emptyList(),
        action = NavAction.Pop
    )
    true
} else {
    false
}

/**
 * Pops all destinations off the backstack up to the item that meets the condition
 * of the [upToPredicate].
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param upToPolicy specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the first matching item from the start of the backstack will be considered
 * the point up to which to pop.
 *
 * @return `true` - if the item matching the predicate was found and any item up to it was popped.
 * `false` - if the item wasn't found or it is the last item and [inclusive] parameter is `false`.
 */
fun <T> NavController<T>.popUpTo(
    inclusive: Boolean = false,
    upToPolicy: UpToPolicy = UpToPolicy.FirstMatching,
    upToPredicate: (T) -> Boolean
): Boolean {
    val entryIndex = backstack.entries.indexOf(upToPolicy, upToPredicate)
    val toIndex = if (inclusive) entryIndex else entryIndex + 1
    return if (entryIndex >= 0 && toIndex < backstack.entries.size) {
        setNewBackstackEntries(
            entries = backstack.entries.subList(
                fromIndex = 0,
                toIndex = toIndex
            ),
            action = NavAction.Pop
        )
        true
    } else {
        false
    }
}

/**
 * Pops the last destination off the backstack and replaces it with the [newDestinations].
 *
 * The order of items in the list is interpreted as going from the bottom of the backstack
 * to the top. This means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * @return If the backstack is empty, meaning there is no last item, `false` is returned and
 * the [newDestinations] are not added to the backstack. If there is at least one destination,
 * it is replaced as expected and `true` is returned.
 */
fun <T> NavController<T>.replaceLast(newDestinations: Collection<T>): Boolean =
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
 * Pops the last destination off the backstack and replaces it with the [newDestination].
 *
 * @return If the backstack is empty, meaning there is no last item, `false` is returned and
 * the [newDestination] is not added to the backstack. If there is at least one destination,
 * it is replaced as expected and `true` is returned.
 */
fun <T> NavController<T>.replaceLast(newDestination: T): Boolean =
    replaceLast(listOf(newDestination))

/**
 * Pops all destinations off the backstack and replaces them with the [newDestinations].
 *
 * The order of items in the list is interpreted as going from the bottom of the backstack
 * to the top. This means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * If the backstack is empty, the [newDestinations] are still added.
 */
fun <T> NavController<T>.replaceAll(newDestinations: Collection<T>) {
    setNewBackstackEntries(
        entries = newDestinations.map(::navEntry),
        action = NavAction.Replace
    )
}

/**
 * Pops all destinations off the backstack and replaces them with the [newDestination].
 *
 * If the backstack is empty, the [newDestination] is still added.
 */
fun <T> NavController<T>.replaceAll(newDestination: T) =
    replaceAll(listOf(newDestination))

/**
 * Pops all destinations off the backstack up to the item that meets the condition
 * of the [upToPredicate] and replaces them with the [newDestinations].
 *
 * The order of items in the list is interpreted as going from the bottom of the backstack
 * to the top. This means that the last item of the list will become the currently displayed item
 * in [NavHost].
 *
 * The [newDestinations] list may be empty.
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param upToPolicy specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the first matching item from the start of the backstack will be considered
 * the point up to which to pop.
 *
 * @return `true` - if the item matching the predicate was found. `false` - if the item wasn't
 * found.
 */
fun <T> NavController<T>.replaceUpTo(
    newDestinations: Collection<T>,
    inclusive: Boolean = false,
    upToPolicy: UpToPolicy = UpToPolicy.FirstMatching,
    upToPredicate: (T) -> Boolean
): Boolean {
    val entryIndex = backstack.entries.indexOf(upToPolicy, upToPredicate)
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
 * Pops all destinations off the backstack up to the item that meets the condition
 * of the [upToPredicate] and replaces them with the [newDestination].
 *
 * @param inclusive whether the item itself should be popped or not, default value is `false`
 *
 * @param upToPolicy specifies the policy of selecting the target item in case of multiple matching
 * items. By default, the first matching item from the start of the backstack will be considered
 * the point up to which to pop.
 *
 * @return `true` - if the item matching the predicate was found. `false` - if the item wasn't
 * found.
 */
fun <T> NavController<T>.replaceUpTo(
    newDestination: T,
    inclusive: Boolean = false,
    upToPolicy: UpToPolicy = UpToPolicy.FirstMatching,
    upToPredicate: (T) -> Boolean
): Boolean = replaceUpTo(listOf(newDestination), inclusive, upToPolicy, upToPredicate)

/**
 * The policy of selecting the target item in case of multiple matching items.
 */
enum class UpToPolicy {

    /**
     * Selects the first item from the start of the backstack that matches the predicate.
     */
    FirstMatching,

    /**
     * Selects the last item from the start of the backstack that matches the predicate.
     */
    LastMatching
}

private fun <T> List<NavEntry<T>>.indexOf(
    upToPolicy: UpToPolicy,
    predicate: (T) -> Boolean
): Int {
    val entryPredicate: (NavEntry<T>) -> Boolean = { predicate(it.destination) }
    return when (upToPolicy) {
        UpToPolicy.FirstMatching -> indexOfFirst(entryPredicate)
        UpToPolicy.LastMatching -> indexOfLast(entryPredicate)
    }
}