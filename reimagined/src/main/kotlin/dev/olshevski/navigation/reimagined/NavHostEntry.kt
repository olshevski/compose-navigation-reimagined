package dev.olshevski.navigation.reimagined

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry

/**
 * Wraps around [NavEntry] and serves as an owner of its associated components ([Lifecycle],
 * [SavedStateRegistry], [ViewModelStore]).
 *
 * @param destination A destination you passed into [navigate], [replaceLast] or other extension
 * method. If you used [NavController.setNewBackstack] directly this is the destination you
 * passed into [navEntry] method.
 */
@Stable
class NavHostEntry<out T> internal constructor(
    id: NavId,
    val destination: T,
    private val saveableStateHolder: SaveableStateHolder,
    viewModelStore: ViewModelStore,
    application: Application?
) : BaseNavHostEntry(id, viewModelStore, application) {

    @Composable
    internal fun SaveableStateProvider(content: @Composable () -> Unit) =
        saveableStateHolder.SaveableStateProvider(
            key = id,
            content = content
        )

    override fun toString() = "NavHostEntry(id=$id, destination=$destination)"

}

@Composable
internal fun <T> NavHostEntry<T>.ComponentProvider(
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalViewModelStoreOwner provides this,
    LocalLifecycleOwner provides this,
    LocalSavedStateRegistryOwner provides this
) {
    this.SaveableStateProvider(content)
}

@Suppress("unused")
@Deprecated(message = "Renamed to NavHostEntry", replaceWith = ReplaceWith("NavHostEntry<T>"))
typealias NavComponentEntry<T> = NavHostEntry<T>