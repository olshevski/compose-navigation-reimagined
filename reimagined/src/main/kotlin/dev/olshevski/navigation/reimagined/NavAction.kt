package dev.olshevski.navigation.reimagined

/**
 * The navigation action hint. It is passed as a parameter into
 * [NavController.setNewBackstackEntries] and then used in
 * [AnimatedNavHostTransitionSpec.getContentTransform] to select some animation.
 *
 * May be extended to provide more specific action types.
 */
abstract class NavAction {

    /**
     * The default action for every new instance of [NavController].
     *
     * The only instance of this class is internal, so the action cannot be passed into
     * [NavController.setNewBackstackEntries].
     */
    internal object Idle : NavAction()

    /**
     * The action type that tells [NavController.navigate] was the last successful call.
     */
    object Navigate : NavAction()

    /**
     * The action type that tells [NavController.replaceLast], [NavController.replaceAll] or
     * [NavController.replaceUpTo] was the last successful call.
     */
    object Replace : NavAction()

    /**
     * The action type that tells [NavController.pop], [NavController.popAll] or
     * [NavController.popUpTo] was the last successful call.
     */
    object Pop : NavAction()

}
