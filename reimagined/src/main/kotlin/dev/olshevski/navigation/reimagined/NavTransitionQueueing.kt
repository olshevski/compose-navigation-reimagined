package dev.olshevski.navigation.reimagined

import androidx.compose.animation.AnimatedContent

/**
 * A strategy of processing incoming transitions when transition animations run slower than being
 * added.
 */
enum class NavTransitionQueueing {

    /**
     * Each transition will be enqueued and fully animated one by one in the exact order of being
     * added.
     *
     * **Example:**
     * If a transition animation from A to B is running, and you add a transition from B to C and
     * another transition from C to D, then all transition animations will be played sequentially:
     *
     * *A -> B -> C -> D*
     *
     * [NavTransitionSpec] may support only the expected set of possible transitions that your app
     * does. For example, if your app transitions from A to B and from B to C and never directly
     * from A to C, then you may expect the request for transition animation from A to C
     * to **never** appear in NavTransitionSpec.
     */
    QueueAll,

    /**
     * Queued transitions will be skipped as much as possible, but the currently running transition
     * animation will always be played until the end.
     *
     * **Example:**
     * If a transition animation from A to B is running, and you add a transition from B to C and
     * another transition from C to D, then destination C will be skipped completely. As soon as
     * A -> B finishes animating, B will be animated straight to D:
     *
     * *A -> B -> D*
     *
     * Note that in this case your [NavTransitionSpec] should expect transitions between any
     * possible combination of two destinations. For example, if your app transitions from A to B
     * and from B to C, then you should also expect the request for transition animation
     * from A to C in NavTransitionSpec.
     */
    ConflateQueued,

    /**
     * The currently running transition animation will be interrupted and replaced with the next
     * transition animation.
     *
     * It is up to the underlying implementation (e.g. for [AnimatedContent] in [AnimatedNavHost])
     * to handle and animate the interruption correctly. Note that it may not look
     * visually pleasing in all the cases. You may choose [ConflateQueued] or [QueueAll] in order
     * to guarantee that the currently running transition animation finishes before moving to
     * the next one.
     *
     * [NavTransitionSpec] may support only the expected set of possible transitions that your app
     * does. For example, if your app transitions from A to B and from B to C and never directly
     * from A to C, then you may expect the request for transition animation from A to C
     * to **never** appear in NavTransitionSpec.
     */
    InterruptCurrent

}