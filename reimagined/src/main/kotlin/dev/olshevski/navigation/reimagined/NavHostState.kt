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
internal fun <T> rememberNavHostState(
    backstack: NavBackstack<T>
): NavHostState<T> {
    val saveableStateHolder = rememberSaveableStateHolder()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry

    // applicationContext may be not Application in IDE preview. Handle it gracefully here.
    val application = LocalContext.current.applicationContext as? Application

    return rememberSaveable(
        saver = listSaver(
            save = { listOf(it.id, it.hostEntryIds.toTypedArray()) },
            restore = { restored ->
                @Suppress("UNCHECKED_CAST")
                NavHostState(
                    id = restored[0] as NavHostId,
                    restoredHostEntryIds = (restored[1] as Array<Parcelable>)
                        .map { it as NavId }
                        .toSet(),
                    initialBackstack = backstack,
                    saveableStateHolder = saveableStateHolder,
                    hostViewModelStoreOwner = viewModelStoreOwner,
                    hostLifecycle = lifecycle,
                    hostSavedStateRegistry = savedStateRegistry,
                    application = application
                )
            }
        )
    ) {
        NavHostState(
            initialBackstack = backstack,
            saveableStateHolder = saveableStateHolder,
            hostViewModelStoreOwner = viewModelStoreOwner,
            hostLifecycle = lifecycle,
            hostSavedStateRegistry = savedStateRegistry,
            application = application
        )
    }.apply {
        this.backstack = backstack
    }
}

private const val PACKAGE_KEY = "dev.olshevski.navigation.reimagined.key"

/**
 * Stores and manages all components (lifecycles, saved states, view models).
 */
internal class NavHostState<T>(
    val id: NavHostId = NavHostId(),
    restoredHostEntryIds: Set<NavId> = emptySet(),
    initialBackstack: NavBackstack<T>,
    private val saveableStateHolder: SaveableStateHolder,
    hostViewModelStoreOwner: ViewModelStoreOwner,
    private val hostLifecycle: Lifecycle,
    private val hostSavedStateRegistry: SavedStateRegistry,
    private val application: Application?
) {

    var backstack by mutableStateOf(initialBackstack)

    private val entryIds by derivedStateOf {
        backstack.entries.map { it.id }.toHashSet()
    }

    private val viewModelStoreProvider: ViewModelStoreProvider =
        ViewModelProvider(hostViewModelStoreOwner)["$PACKAGE_KEY:$id", NavHostViewModel::class.java]

    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED

    private val hostEntriesMap = mutableMapOf<NavId, NavHostEntry<T>>()

    val hostEntryIds get() = hostEntriesMap.keys as Set<NavId>

    init {
        // Remove components of the entries that are no longer present in the backstack.
        restoredHostEntryIds.filter { it !in entryIds }.forEach { removeComponents(it) }
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        hostLifecycleState = event.targetState
        hostEntriesMap.values.forEach {
            it.hostLifecycleState = hostLifecycleState
        }
    }

    val hostEntries by derivedStateOf {
        backstack.entries.map {
            hostEntriesMap.getOrPut(it.id) { newHostEntry(it) }
        }
    }

    private fun newHostEntry(entry: NavEntry<T>): NavHostEntry<T> {
        val hostEntry = NavHostEntry(
            entry = entry,
            saveableStateHolder = saveableStateHolder,
            viewModelStore = viewModelStoreProvider.getViewModelStore(entry.id),
            application = application
        )

        // state should be restored only in INITIALIZED state
        savedStateKey(hostEntry.id).let { key ->
            hostSavedStateRegistry.consumeRestoredStateForKey(key).let { savedState ->
                hostEntry.restoreState(savedState ?: Bundle())
            }
            hostSavedStateRegistry.unregisterSavedStateProvider(key)
            hostSavedStateRegistry.registerSavedStateProvider(
                key,
                hostEntry.savedStateProvider
            )
        }

        // apply actual states only after state restoration
        hostEntry.hostLifecycleState = hostLifecycleState
        hostEntry.maxLifecycleState = Lifecycle.State.STARTED

        return hostEntry
    }

    private fun savedStateKey(entryId: NavId) = "$PACKAGE_KEY:$id:$entryId"

    fun onCreate() {
        hostLifecycle.addObserver(lifecycleEventObserver)
    }

    fun onDispose() {
        hostLifecycle.removeObserver(lifecycleEventObserver)
        hostEntriesMap.values.forEach {
            it.hostLifecycleState = Lifecycle.State.DESTROYED
        }
    }

    fun onTransitionStart() {
        val lastHostEntry = hostEntries.lastOrNull()

        // Before transition:
        // - all entries except newLastHostEntry are capped at STARTED state
        // - newLastHostEntry gets promoted to STARTED state
        //
        // Further state changes will be done when transition finishes.
        hostEntriesMap.values
            .filter { it != lastHostEntry }
            .forEach {
                it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED)
            }
        lastHostEntry?.maxLifecycleState = Lifecycle.State.STARTED
    }

    fun onTransitionFinish() {
        val lastHostEntry = hostEntries.lastOrNull()

        // last entry is resumed, everything else is stopped
        hostEntriesMap.values
            .filter { it != lastHostEntry }
            .forEach {
                it.maxLifecycleState = Lifecycle.State.CREATED
            }
        lastHostEntry?.maxLifecycleState = Lifecycle.State.RESUMED
    }

    /**
     * Remove entries that are no longer in the backstack.
     */
    fun removeOutdatedHostEntries() {
        hostEntriesMap.keys.filter { it !in entryIds }.forEach { entryId ->
            hostEntriesMap.remove(entryId)?.let { hostEntry ->
                hostEntry.maxLifecycleState = Lifecycle.State.DESTROYED
            }
            removeComponents(entryId)
        }
    }

    /**
     * Unregister saved state provider and cleanup view models for the specified entry id.
     */
    private fun removeComponents(entryId: NavId) {
        hostSavedStateRegistry.unregisterSavedStateProvider(savedStateKey(entryId))
        viewModelStoreProvider.removeViewModelStore(entryId)
        saveableStateHolder.removeState(entryId)
    }

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