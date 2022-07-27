package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Stable

/**
 * A navigation backstack. Contains the list of current [entries] and the last [action].
 */
@Stable
class NavBackstack<out T> internal constructor(

    /**
     * The list of current entries in the backstack. The last item in this list is the item that
     * will be displayed by [NavHost].
     *
     * May become empty if you pop all the items off the backstack.
     */
    val entries: List<NavEntry<T>>,

    /**
     * The action of the last [NavController.setNewBackstack] call.
     *
     * The initial value of every new instance of [NavController] is [NavAction.Idle].
     */
    val action: NavAction

) {

    override fun toString() = "NavBackstack(entries=$entries, action=$action)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NavBackstack<*>
        if (entries != other.entries) return false
        if (action != other.action) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entries.hashCode()
        result = 31 * result + action.hashCode()
        return result
    }

    operator fun component1() = entries

    operator fun component2() = action

}