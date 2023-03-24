package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedContent

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
     * [NavTransitionSpec] may support only the expected set of possible transitions that your app
     * does.
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
    Conflate,

    /**
     * The currently running transition will be interrupted and replaced with a new transition.
     *
     * It is up to the underlying implementation (e.g. for [AnimatedContent] in [AnimatedNavHost])
     * to handle and animate after the interruption correctly. Note that it may not look
     * visually pleasing in all the cases. You can choose to [Conflate] or [QueueAll] in order
     * to guarantee that the currently running transition finishes before moving to the next one.
     *
     * This is the default behaviour. Same as in `QueueAll` [NavTransitionSpec] may support only
     * the expected set of possible transitions that your app does.
     */
    Interrupt

}