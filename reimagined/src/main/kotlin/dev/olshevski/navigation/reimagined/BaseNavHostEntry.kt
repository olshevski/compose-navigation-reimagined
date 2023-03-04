package dev.olshevski.navigation.reimagined

import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.properties.Delegates

@Suppress("LeakingThis")
@Stable
sealed class BaseNavHostEntry(
    val id: NavId,
    override val viewModelStore: ViewModelStore,
    private val application: Application?
) : ViewModelStoreOwner,
    LifecycleOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    internal var hostLifecycleState by Delegates.observable(Lifecycle.State.INITIALIZED) { _, _, _ ->
        updateLifecycleRegistry()
    }

    internal var maxLifecycleState by Delegates.observable(Lifecycle.State.INITIALIZED) { _, _, _ ->
        updateLifecycleRegistry()
    }

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    internal val savedStateProvider = SavedStateRegistry.SavedStateProvider {
        Bundle().also { bundle ->
            savedStateRegistryController.performSave(bundle)
        }
    }

    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    private val defaultFactory by lazy {
        SavedStateViewModelFactory(application, this, null)
    }

    init {
        savedStateRegistryController.performAttach()
        enableSavedStateHandles()
    }

    private fun updateLifecycleRegistry() {
        val currentState = lifecycle.currentState
        val newState = minOf(maxLifecycleState, hostLifecycleState)

        if (currentState != newState) {
            if (currentState == Lifecycle.State.DESTROYED) {
                error("Moving from DESTROYED state is not allowed")
            }
            if (currentState == Lifecycle.State.INITIALIZED && newState == Lifecycle.State.DESTROYED) {
                // Lifecycle should not move straight from INITIALIZED to DESTROYED,
                // only INITIALIZED -> STARTED -> DESTROYED is allowed
                lifecycle.currentState = Lifecycle.State.STARTED
            }
            lifecycle.currentState = newState
        }
    }

    internal fun restoreState(savedState: Bundle) {
        savedStateRegistryController.performRestore(savedState)
    }

    override val defaultViewModelProviderFactory = defaultFactory

    override val defaultViewModelCreationExtras: CreationExtras
        get() {
            val extras = MutableCreationExtras()
            if (application != null) {
                extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] = application
            }
            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this
            return extras
        }
}