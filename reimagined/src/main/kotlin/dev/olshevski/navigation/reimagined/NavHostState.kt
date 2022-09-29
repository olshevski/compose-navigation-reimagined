package dev.olshevski.navigation.reimagined

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
            save = { hostState ->
                listOf(
                    hostState.hostId,
                    hostState.hostEntriesMap.keys.toTypedArray(),
                    hostState.sharedEntriesMap.values.map { it.toSharedHostEntryRecord() }
                        .toTypedArray()
                )
            },
            restore = { restored ->
                @Suppress("UNCHECKED_CAST")
                NavHostState(
                    hostId = restored[0] as NavHostId,
                    restoredHostEntryIds = (restored[1] as Array<Parcelable>)
                        .map { it as NavId },
                    restoredSharedEntryRecords = (restored[2] as Array<Parcelable>)
                        .map { it as SharedNavHostEntryRecord },
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
            hostId = NavHostId(),
            restoredHostEntryIds = emptyList(),
            restoredSharedEntryRecords = emptyList(),
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

/**
 * Stores and manages all components (lifecycles, saved states, view models).
 */
@Stable
internal class NavHostState<T>(
    val hostId: NavHostId,
    restoredHostEntryIds: List<NavId>,
    restoredSharedEntryRecords: List<SharedNavHostEntryRecord>,
    initialBackstack: NavBackstack<T>,
    private val saveableStateHolder: SaveableStateHolder,
    hostViewModelStoreOwner: ViewModelStoreOwner,
    private val hostLifecycle: Lifecycle,
    private val hostSavedStateRegistry: SavedStateRegistry,
    private val application: Application?
) {

    var backstack by mutableStateOf(initialBackstack)

    val hostEntriesMap = mutableMapOf<NavId, NavHostEntry<T>>()

    val sharedEntriesMap = mutableMapOf<NavKey, SharedNavHostEntry>()

    private val viewModelStoreProvider: ViewModelStoreProvider =
        ViewModelProvider(hostViewModelStoreOwner)[viewModelStoreProviderKey(hostId), NavHostViewModel::class.java]

    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        hostLifecycleState = event.targetState
        listOf(hostEntriesMap, sharedEntriesMap).flatMap { it.values }.forEach {
            it.hostLifecycleState = hostLifecycleState
        }
    }

    init {
        // Remove components of the entries that are no longer present in the backstack.
        val backstackEntryIds = backstack.entryIdsSet()
        restoredHostEntryIds.filter { it !in backstackEntryIds }.forEach { removeComponents(it) }

        // Shared entries are all restored. Let them be re-acquired during the first composition.
        // Outdated entries will be removed later in removeOutdatedHostEntries.
        restoredSharedEntryRecords.forEach {
            sharedEntriesMap[it.key] = newSharedEntry(it.id, it.key).apply {
                associatedEntryIds.addAll(it.associatedEntryIds)
            }
        }
    }

    val targetSnapshot by derivedStateOf {
        val backstackEntryIds = backstack.entryIdsSet()
        NavSnapshot(
            hostEntries = backstack.entries.map {
                hostEntriesMap.getOrPut(it.id) { newHostEntry(it) }
            },
            action = backstack.action,
            outdatedHostEntryIds = hostEntriesMap.keys.filter { it !in backstackEntryIds },
        )
    }

    private fun newHostEntry(entry: NavEntry<T>) = NavHostEntry(
        id = entry.id,
        destination = entry.destination,
        saveableStateHolder = saveableStateHolder,
        viewModelStore = viewModelStoreProvider.getViewModelStore(entry.id),
        application = application
    ).apply {
        initComponents(this)
    }

    fun getSharedHostEntry(
        key: NavKey,
        associatedEntryId: NavId
    ) = sharedEntriesMap.getOrPut(key) {
        newSharedEntry(
            sharedEntryId = NavId(),
            sharedEntryKey = key
        )
    }.apply {
        associatedEntryIds.add(associatedEntryId)
    }

    private fun newSharedEntry(
        sharedEntryId: NavId,
        sharedEntryKey: NavKey
    ) = SharedNavHostEntry(
        id = sharedEntryId,
        key = sharedEntryKey,
        viewModelStore = viewModelStoreProvider.getViewModelStore(sharedEntryId),
        application = application
    ).apply {
        initComponents(this)
    }

    private fun initComponents(baseEntry: BaseNavHostEntry) {
        val savedStateKey = savedStateKey(hostId, baseEntry.id)

        // state should be restored only in INITIALIZED state
        hostSavedStateRegistry.consumeRestoredStateForKey(savedStateKey).let { savedState ->
            baseEntry.restoreState(savedState ?: Bundle())
        }
        hostSavedStateRegistry.unregisterSavedStateProvider(savedStateKey)
        hostSavedStateRegistry.registerSavedStateProvider(
            savedStateKey,
            baseEntry.savedStateProvider
        )

        // apply actual states only after state restoration
        baseEntry.hostLifecycleState = hostLifecycleState
        baseEntry.maxLifecycleState = Lifecycle.State.CREATED
    }

    fun onCreate() {
        hostLifecycle.addObserver(lifecycleEventObserver)
    }

    fun onDispose() {
        hostLifecycle.removeObserver(lifecycleEventObserver)
        listOf(hostEntriesMap, sharedEntriesMap).flatMap { it.values }.forEach {
            it.hostLifecycleState = Lifecycle.State.DESTROYED
        }
    }

    fun onTransitionStart() {
        val lastHostEntry = targetSnapshot.hostEntries.lastOrNull()

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

    fun onAllTransitionsFinish() {
        val lastHostEntry = targetSnapshot.hostEntries.lastOrNull()

        // last entry is resumed, everything else is stopped
        hostEntriesMap.values
            .filter { it != lastHostEntry }
            .forEach {
                it.maxLifecycleState = Lifecycle.State.CREATED
            }
        lastHostEntry?.maxLifecycleState = Lifecycle.State.RESUMED
    }

    /**
     * Remove entries that are no longer in the snapshot.
     */
    fun removeOutdatedHostEntries(snapshot: NavSnapshot<T>) {
        snapshot.outdatedHostEntryIds
            .forEach { entryId ->
                hostEntriesMap.remove(entryId)?.let { hostEntry ->
                    hostEntry.maxLifecycleState = Lifecycle.State.DESTROYED
                }
                removeComponents(entryId)
            }

        val snapshotEntryIds = snapshot.entryIdsSet()
        sharedEntriesMap.entries
            .filter {
                it.value.associatedEntryIds.intersect(snapshotEntryIds).isEmpty()
            }
            .forEach { entry ->
                sharedEntriesMap.remove(entry.key)?.let { sharedEntry ->
                    sharedEntry.maxLifecycleState = Lifecycle.State.DESTROYED
                }
                removeComponents(entry.value.id)
            }
    }

    /**
     * Unregister saved state provider and cleanup view models for the specified entry id.
     */
    private fun removeComponents(entryId: NavId) {
        hostSavedStateRegistry.unregisterSavedStateProvider(savedStateKey(hostId, entryId))
        viewModelStoreProvider.removeViewModelStore(entryId)
        saveableStateHolder.removeState(entryId)
    }

}

private fun <T> NavBackstack<T>.entryIdsSet() = entries.map { it.id }.toHashSet()
private fun <T> NavSnapshot<T>.entryIdsSet() = hostEntries.map { it.id }.toHashSet()

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

