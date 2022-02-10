package dev.olshevski.navigation.reimagined

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry

@Composable
internal fun <T> rememberNavComponentHolder(
    backstack: NavBackstack<T>
): NavComponentHolder<T> {
    val saveableStateHolder = rememberSaveableStateHolder()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry

    // applicationContext may be not Application in IDE preview. Handle it gracefully here.
    val application = LocalContext.current.applicationContext as? Application

    return rememberSaveable(
        saver = listSaver(
            save = { listOf(it.id, it.managedEntryIds.toTypedArray()) },
            restore = { restored ->
                @Suppress("UNCHECKED_CAST")
                NavComponentHolder(
                    id = restored[0] as NavId,
                    initialManagedEntryIds = (restored[1] as Array<Parcelable>).map { it as NavId },
                    initialBackstack = backstack,
                    saveableStateHolder = saveableStateHolder,
                    navHostViewModelStoreOwner = viewModelStoreOwner,
                    navHostLifecycle = lifecycle,
                    navHostSavedStateRegistry = savedStateRegistry,
                    application = application
                )
            }
        )
    ) {
        NavComponentHolder(
            initialManagedEntryIds = emptyList(),
            saveableStateHolder = saveableStateHolder,
            initialBackstack = backstack,
            navHostViewModelStoreOwner = viewModelStoreOwner,
            navHostLifecycle = lifecycle,
            navHostSavedStateRegistry = savedStateRegistry,
            application = application
        )
    }.apply {
        // support setting new backstacks
        this.backstack = backstack
    }
}

/**
 * Stores and manages all components (lifecycles, saved states, view models).
 */
internal class NavComponentHolder<T>(
    val id: NavId = NavId(),
    initialManagedEntryIds: List<NavId>,
    initialBackstack: NavBackstack<T>,
    private val saveableStateHolder: SaveableStateHolder,
    navHostViewModelStoreOwner: ViewModelStoreOwner,
    private val navHostLifecycle: Lifecycle,
    private val navHostSavedStateRegistry: SavedStateRegistry,
    private val application: Application?
) {

    /**
     * We need to keep track of all [NavComponentEntry] ids we've created, so this info can
     * be used to remove redundant ViewModelStores from [ViewModelStoreProvider] and states
     * from [SaveableStateHolder].
     */
    val managedEntryIds = initialManagedEntryIds.toMutableSet()

    var backstack by mutableStateOf(initialBackstack)

    private val componentEntries = mutableMapOf<NavId, NavComponentEntry<T>>()

    val lastComponentEntry = derivedStateOf {
        backstack.entries.lastOrNull()?.let { lastEntry ->
            managedEntryIds.add(lastEntry.id)
            componentEntries.getOrPut(lastEntry.id) {
                newComponentEntry(lastEntry)
            }
        }.also { lastComponentEntry ->
            // Before transition all  entries are capped at STARTED state, and the new last entry
            // gets promoted to STARTED state. Further state changes will be done after transition.
            componentEntries.values
                .filter { it != lastComponentEntry }
                .forEach {
                    it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED)
                }
            lastComponentEntry?.maxLifecycleState = Lifecycle.State.STARTED
        }
    }

    private val backstackIds = derivedStateOf {
        backstack.entries.map { it.id }.toHashSet()
    }

    private val viewModelStoreProvider: ViewModelStoreProvider =
        ViewModelProvider(navHostViewModelStoreOwner)[id.toString(), NavHostViewModel::class.java]

    private var navHostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        navHostLifecycleState = event.targetState
        componentEntries.values.forEach {
            it.navHostLifecycleState = navHostLifecycleState
        }
    }

    fun onCreate() {
        cleanupComponentEntries()
        setPostTransitionLifecycleStates()
        navHostLifecycle.addObserver(lifecycleEventObserver)
    }

    private fun newComponentEntry(entry: NavEntry<T>): NavComponentEntry<T> {
        val componentEntry = NavComponentEntry(
            entry = entry,
            saveableStateHolder = saveableStateHolder,
            viewModelStore = viewModelStoreProvider.getViewModelStore(entry.id),
            application = application
        )
        componentEntry.navHostLifecycleState = navHostLifecycleState

        savedStateKey(componentEntry).let { key ->
            navHostSavedStateRegistry.consumeRestoredStateForKey(key).let { savedState ->
                componentEntry.restoreState(savedState ?: Bundle())
            }
            navHostSavedStateRegistry.unregisterSavedStateProvider(key)
            navHostSavedStateRegistry.registerSavedStateProvider(key, componentEntry)
        }

        return componentEntry
    }

    fun onTransitionFinish() {
        cleanupComponentEntries()
        setPostTransitionLifecycleStates()
        // https://www.youtube.com/watch?v=cwyTleTL06Y
    }

    /**
     * Last entry is resumed, everything else is stopped.
     */
    private fun setPostTransitionLifecycleStates() {
        componentEntries.values
            .filter { it != lastComponentEntry.value }
            .forEach {
                it.maxLifecycleState = Lifecycle.State.CREATED
            }
        lastComponentEntry.value?.maxLifecycleState = Lifecycle.State.RESUMED
    }

    /**
     * Remove entries that are no longer in backstack.
     */
    private fun cleanupComponentEntries() {
        val unusedIds = managedEntryIds.filter { it !in backstackIds.value }
        unusedIds.forEach { id ->
            componentEntries.remove(id)?.let { componentEntry ->
                componentEntry.maxLifecycleState = Lifecycle.State.DESTROYED
                navHostSavedStateRegistry.unregisterSavedStateProvider(
                    savedStateKey(componentEntry)
                )
            }
            viewModelStoreProvider.removeViewModelStore(id)
            saveableStateHolder.removeState(id)
        }
        managedEntryIds.removeAll(unusedIds.toSet())
    }

    fun onDispose() {
        navHostLifecycle.removeObserver(lifecycleEventObserver)
        componentEntries.values.forEach {
            it.navHostLifecycleState = Lifecycle.State.DESTROYED
        }
    }

    private fun <T> savedStateKey(componentEntry: NavComponentEntry<T>) =
        "dev.olshevski.navigation.reimagined.key:$id:${componentEntry.id}"

}

private interface ViewModelStoreProvider {

    fun getViewModelStore(id: NavId): ViewModelStore
    fun removeViewModelStore(id: NavId)

}

internal class NavHostViewModel : ViewModel(), ViewModelStoreProvider {

    private val viewModelStores = mutableMapOf<NavId, ViewModelStore>()

    override fun getViewModelStore(id: NavId) = viewModelStores.getOrPut(id) {
        ViewModelStore()
    }

    override fun removeViewModelStore(id: NavId) {
        viewModelStores.remove(id)?.also { it.clear() }
    }

    override fun onCleared() {
        for (store in viewModelStores.values) {
            store.clear()
        }
        viewModelStores.clear()
    }

}