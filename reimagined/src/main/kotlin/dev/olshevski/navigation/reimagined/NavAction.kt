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
    data object Idle : NavAction

    /**
     * An action type that tells [NavController.navigate] or [NavController.moveToTop] was
     * the last successful call.
     */
    @Immutable
    @Parcelize
    data object Navigate : NavAction

    /**
     * An action type that tells [NavController.replaceLast], [NavController.replaceAll] or
     * [NavController.replaceUpTo] was the last successful call.
     */
    @Immutable
    @Parcelize
    data object Replace : NavAction

    /**
     * An action type that tells [NavController.pop], [NavController.popAll] or
     * [NavController.popUpTo] was the last successful call.
     */
    @Immutable
    @Parcelize
    data object Pop : NavAction

}
