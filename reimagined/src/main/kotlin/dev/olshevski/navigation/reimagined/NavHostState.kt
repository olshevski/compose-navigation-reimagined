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
import androidx.compose.runtime.saveable.Saver
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
import kotlinx.parcelize.Parcelize

@Composable
internal fun <T, S> rememberNavHostState(
    backstack: NavBackstack<T>,
    scopeSpec: NavScopeSpec<T, S>
): NavHostState<T, S> {
    val saveableStateHolder = rememberSaveableStateHolder()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry

    // applicationContext may be not Application in IDE preview. Handle it gracefully here.
    val application = LocalContext.current.applicationContext as? Application

    return rememberSaveable(
        saver = Saver(
            save = { it.saveState() },
            restore = { savedState ->
                NavHostState(
                    savedState = savedState,
                    initialBackstack = backstack,
                    scopeSpec = scopeSpec,
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
            scopeSpec = scopeSpec,
            saveableStateHolder = saveableStateHolder,
            hostViewModelStoreOwner = viewModelStoreOwner,
            hostLifecycle = lifecycle,
            hostSavedStateRegistry = savedStateRegistry,
            application = application
        )
    }.also {
        it.backstack = backstack
    }
}

/**
 * Stores and manages saved state and all Android architecture components (Lifecycle,
 * ViewModelStore, SavedStateRegistry) for every entry.
 */
@Stable
internal class NavHostState<T, S>(
    savedState: NavHostSavedState<S>? = null,
    initialBackstack: NavBackstack<T>,
    private val scopeSpec: NavScopeSpec<T, S>,
    private val saveableStateHolder: SaveableStateHolder,
    hostViewModelStoreOwner: ViewModelStoreOwner,
    private val hostLifecycle: Lifecycle,
    private val hostSavedStateRegistry: SavedStateRegistry,
    private val application: Application?
) {

    val hostId: NavHostId = savedState?.hostId ?: NavHostId()

    var backstack by mutableStateOf(initialBackstack)

    private val hostEntriesMap = mutableMapOf<NavId, NavHostEntry<T>>()

    private val scopedHostEntriesMap = mutableMapOf<S, ScopedNavHostEntry<S>>()

    private val outdatedHostEntriesQueue = ArrayDeque<OutdatedHostEntriesQueueItem<T, S>>()

    private val viewModelStoreProvider: ViewModelStoreProvider =
        ViewModelProvider(hostViewModelStoreOwner)[viewModelStoreProviderKey(hostId), NavHostViewModel::class.java]

    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        hostLifecycleState = event.targetState
        getAllHostEntries().forEach {
            it.hostLifecycleState = hostLifecycleState
        }
    }

    init {
        if (savedState != null) {
            restoreState(savedState)
        }
    }

    private fun restoreState(savedState: NavHostSavedState<S>) {
        // Remove components of the entries that are no longer present in the backstack.
        val backstackEntryIds = backstack.entries.map { it.id }.toHashSet()
        savedState.hostEntryIds.filter { it !in backstackEntryIds }
            .forEach { removeComponents(it) }

        val backstackEntryScopes =
            backstack.entries.flatMap { scopeSpec.getScopes(it.destination) }
                .toHashSet()
        val (scopedRecordsToRestore, scopedRecordsToRemove) =
            savedState.scopedHostEntryRecords.partition { it.scope in backstackEntryScopes }
        scopedRecordsToRemove.forEach { removeComponents(it.id) }
        scopedRecordsToRestore.forEach {
            scopedHostEntriesMap.getOrPut(it.scope) {
                newScopedHostEntry(id = it.id, scope = it.scope)
            }
        }

        savedState.outdatedHostEntryIds.forEach { removeComponents(it) }
    }

    val snapshot by derivedStateOf {
        NavSnapshot(
            items = backstack.entries.map { entry ->
                NavSnapshotItem(
                    hostEntry = hostEntriesMap.getOrPut(entry.id) {
                        newHostEntry(entry)
                    },
                    scopedHostEntries = scopeSpec.getScopes(entry.destination)
                        .associateWith { scope ->
                            scopedHostEntriesMap.getOrPut(scope) {
                                newScopedHostEntry(id = NavId(), scope = scope)
                            }
                        }
                )
            },
            action = backstack.action
        ).also { snapshot ->
            val backstackEntryIds = snapshot.items
                .map { it.hostEntry.id }.toHashSet()
            val outdatedHostEntries = hostEntriesMap.keys
                .filter { it !in backstackEntryIds }
                .mapNotNull { hostEntriesMap.remove(it) }

            val backstackEntryScopes = snapshot.items
                .flatMap { it.scopedHostEntries.keys }.toHashSet()
            val outdatedScopedHostEntries = scopedHostEntriesMap.keys
                .filter { it !in backstackEntryScopes }
                .mapNotNull { scopedHostEntriesMap.remove(it) }

            outdatedHostEntriesQueue.addLast(
                OutdatedHostEntriesQueueItem(
                    snapshot = snapshot,
                    outdatedHostEntries = outdatedHostEntries + outdatedScopedHostEntries
                )
            )
        }
    }

    private fun getAllHostEntries() = listOf(
        hostEntriesMap.values,
        scopedHostEntriesMap.values,
        outdatedHostEntriesQueue.getAllHostEntries()
    ).flatten()

    private fun newHostEntry(entry: NavEntry<T>) = NavHostEntry(
        id = entry.id,
        destination = entry.destination,
        saveableStateHolder = saveableStateHolder,
        viewModelStore = viewModelStoreProvider.getViewModelStore(entry.id),
        application = application
    ).also {
        initComponents(it)
    }

    private fun newScopedHostEntry(
        id: NavId,
        scope: S
    ) = ScopedNavHostEntry(
        id = id,
        scope = scope,
        viewModelStore = viewModelStoreProvider.getViewModelStore(id),
        application = application
    ).also {
        initComponents(it)
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
        getAllHostEntries().forEach {
            it.hostLifecycleState = Lifecycle.State.DESTROYED
        }
    }

    fun onTransitionStart(visibleItems: Set<NavSnapshotItem<T, S>>) {
        val visibleHostEntries = visibleItems.getAllHostEntries()
        getAllHostEntries().filter { it !in visibleHostEntries }.forEach {
            it.maxLifecycleState = minOf(it.maxLifecycleState, Lifecycle.State.STARTED)
        }
        visibleHostEntries.forEach {
            it.maxLifecycleState = Lifecycle.State.STARTED
        }
    }

    fun onTransitionFinish(visibleItems: Set<NavSnapshotItem<T, S>>) {
        val visibleHostEntries = visibleItems.getAllHostEntries()
        getAllHostEntries().filter { it !in visibleHostEntries }.forEach {
            it.maxLifecycleState = Lifecycle.State.CREATED
        }
        visibleHostEntries.forEach {
            it.maxLifecycleState = Lifecycle.State.RESUMED
        }
    }

    /**
     * Remove entries that are no longer in the snapshot.
     */
    fun removeOutdatedEntries(snapshot: NavSnapshot<T, S>) {
        if (outdatedHostEntriesQueue.any { it.snapshot == snapshot }) {
            do {
                val item = outdatedHostEntriesQueue.removeFirst()
                item.outdatedHostEntries.forEach { entry ->
                    entry.maxLifecycleState = Lifecycle.State.DESTROYED
                    removeComponents(entry.id)
                }
            } while (item.snapshot != snapshot)
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

    internal fun saveState() = NavHostSavedState(
        hostId = hostId,
        hostEntryIds = hostEntriesMap.keys.toList(),
        scopedHostEntryRecords = scopedHostEntriesMap.values.map { it.toScopedHostEntryRecord() },
        outdatedHostEntryIds = outdatedHostEntriesQueue.getAllHostEntries().map { it.id }
    )

}

private fun <T, S> ArrayDeque<OutdatedHostEntriesQueueItem<T, S>>.getAllHostEntries() =
    flatMap { it.outdatedHostEntries }

private fun <T, S> Set<NavSnapshotItem<T, S>>.getAllHostEntries() =
    (map { it.hostEntry } + flatMap { it.scopedHostEntries.values }).toSet()

internal data class OutdatedHostEntriesQueueItem<out T, S>(
    val snapshot: NavSnapshot<T, S>,
    val outdatedHostEntries: List<BaseNavHostEntry>
)

@Parcelize
internal data class NavHostSavedState<out S>(
    val hostId: NavHostId,
    val hostEntryIds: List<NavId>,
    val scopedHostEntryRecords: List<ScopedNavHostEntryRecord<S>>,
    val outdatedHostEntryIds: List<NavId>
) : Parcelable

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

