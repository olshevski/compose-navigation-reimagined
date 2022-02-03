package dev.olshevski.navigation.reimagined

/**
 * The navigation action hint. It is passed as a parameter into
 * [NavController.setNewBackstackEntries] and then used in
 * [AnimatedNavHostTransitionSpec.getContentTransform] to select some animation.
 */
sealed interface NavAction {

    /**
     * The logical "forward" action. Usually means that some new destination was added
     * to the backstack.
     *
     * May be extended to provide more specific action types.
     */
    abstract class Forward : NavAction

    /**
     * The logical "backward" action. Usually means that some existing destination was removed
     * from the backstack.
     *
     * May be extended to provide more specific action types.
     */
    abstract class Backward : NavAction

    /**
     * The action type that tells [NavController.navigate] was the last successful call.
     *
     * May be used as a default type for any logical "forward" action.
     */
    object Navigate : Forward()

    /**
     * The action type that tells [NavController.replaceLast], [NavController.replaceAll] or
     * [NavController.replaceUpTo] was the last successful call.
     *
     * Also, this will be the action for replacing the whole [NavController] in [NavHost] with
     * another `NavController` instance. Same stands for replacing the [NavBackstack] as it's linked
     * to the instance of `NavController`.
     */
    object Replace : Forward()

    /**
     * The action type that tells [NavController.pop], [NavController.popAll] or
     * [NavController.popUpTo] was the last successful call.
     *
     * May be used as a default type for any logical "backward" action.
     */
    object Pop : Backward()
}
