package dev.olshevski.navigation.reimagined

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