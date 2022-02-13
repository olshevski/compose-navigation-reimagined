package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A navigation action hint. It is passed as a parameter into
 * [NavController.setNewBackstackEntries] and then used in
 * [AnimatedNavHostTransitionSpec.getContentTransform] to select some animation.
 *
 * May be extended to create more action types.
 */
abstract class NavAction : Parcelable {

    /**
     * The default action for every new instance of [NavController].
     */
    @Parcelize
    object Idle : NavAction() {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.navigate] was the last successful call.
     */
    @Parcelize
    object Navigate : NavAction() {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.replaceLast], [NavController.replaceAll] or
     * [NavController.replaceUpTo] was the last successful call.
     */
    @Parcelize
    object Replace : NavAction() {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.pop], [NavController.popAll] or
     * [NavController.popUpTo] was the last successful call.
     */
    @Parcelize
    object Pop : NavAction() {
        override fun toString() = this::class.simpleName!!
    }

}
