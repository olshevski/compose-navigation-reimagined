package dev.olshevski.navigation.reimagined

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
inline fun <reified T : Any> StateBackNavigator(state: T, controller: NavController<T>) {
    var previousState by remember { mutableStateOf(state) }

    SideEffect {
        if (previousState::class != state::class) {
            val secondToLast = controller.backstack.entries.secondToLast()?.destination

            if (secondToLast != null && secondToLast::class == state::class) {
                previousState = secondToLast
                controller.pop()
            } else {
                previousState = state
                controller.navigate(state)
            }
        }
    }
}

fun <E> List<E>.secondToLast(): E? = if (lastIndex - 1 >= 0) {
    this[lastIndex - 1]
} else {
    null
}
