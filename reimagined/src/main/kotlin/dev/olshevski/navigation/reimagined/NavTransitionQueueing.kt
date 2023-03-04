package dev.olshevski.navigation.reimagined

/**
 * A strategy of processing incoming transitions when transition animations run slower than being
 * added.
 */
enum class NavTransitionQueueing {

    /**
     * Each transition will be enqueued and played one by one in the exact order of being added.
     *
     * **Example:**
     * If transition animation from A to B is running, and you add a transition from B to C and
     * another transition from C to D, then the full queue of transition animations will be played:
     *
     * *A -> B -> C -> D*
     *
     * This is the default behaviour. [NavTransitionSpec] may support only the expected set of
     * possible transitions that your app does.
     */
    QueueAll,

    /**
     * Queued transitions will be skipped as much as possible. This strategy is useful when a user
     * can rapidly switch between different destination (e.g. when using BottomNavigation or
     * TabRow).
     *
     * **Example:**
     * If transition animation from A to B is running, and you add a transition from B to C and
     * another transition from C to D, then destination C will be completely skipped. B will be
     * animated straight to D:
     *
     * *A -> B -> D*
     *
     * Note that in this case your [NavTransitionSpec] should expect transitions between any
     * possible combination of two destinations.
     */
    Conflate

}