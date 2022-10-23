package dev.olshevski.navigation.reimagined

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

/**
 * A navigation action hint. It is passed as a parameter into
 * [NavController.setNewBackstack] and then used in
 * [NavTransitionSpec.getContentTransform] to select some animation.
 *
 * You may implement this interface to create more action types. If you want to use the last action
 * from the [NavBackstack] as a parameter for a composable function, make sure your action class
 * is [Stable] or [Immutable].
 */
@Stable
interface NavAction : Parcelable {

    /**
     * The default action for every new instance of [NavController].
     */
    @Immutable
    @Parcelize
    object Idle : NavAction {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.navigate] or [NavController.moveToTop] was
     * the last successful call.
     */
    @Immutable
    @Parcelize
    object Navigate : NavAction {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.replaceLast], [NavController.replaceAll] or
     * [NavController.replaceUpTo] was the last successful call.
     */
    @Immutable
    @Parcelize
    object Replace : NavAction {
        override fun toString() = this::class.simpleName!!
    }

    /**
     * An action type that tells [NavController.pop], [NavController.popAll] or
     * [NavController.popUpTo] was the last successful call.
     */
    @Immutable
    @Parcelize
    object Pop : NavAction {
        override fun toString() = this::class.simpleName!!
    }

}
