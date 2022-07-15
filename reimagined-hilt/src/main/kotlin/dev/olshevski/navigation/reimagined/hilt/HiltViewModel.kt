package dev.olshevski.navigation.reimagined.hilt

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dev.olshevski.navigation.reimagined.NavComponentEntry
import dev.olshevski.navigation.reimagined.NavController

/*
 All code in this file is taken from androidx.hilt:hilt-navigation-compose:1.0.0 and adapter
 for the library.
 */

/**
 * Returns an existing
 * [HiltViewModel](https://dagger.dev/api/latest/dagger/hilt/android/lifecycle/HiltViewModel)
 * -annotated [ViewModel] or creates a new one scoped to the current navigation graph present on
 * the [NavController] back stack.
 *
 * If no navigation graph is currently present then the current scope will be used, usually, a
 * fragment or an activity.
 *
 * @param defaultArguments arguments that should be passed into [SavedStateHandle] of the newly
 * created ViewModel
 */
@Composable
inline fun <reified VM : ViewModel> hiltViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    defaultArguments: Bundle? = null
): VM {
    val factory = createHiltViewModelFactory(viewModelStoreOwner, defaultArguments)
    return viewModel(viewModelStoreOwner, factory = factory)
}

@Composable
@PublishedApi
internal fun createHiltViewModelFactory(
    viewModelStoreOwner: ViewModelStoreOwner,
    defaultArguments: Bundle?
): ViewModelProvider.Factory? = if (viewModelStoreOwner is NavComponentEntry<*>) {
    createHiltViewModelFactory(
        context = LocalContext.current,
        navComponentEntry = viewModelStoreOwner,
        defaultArguments = defaultArguments
    )
} else {
    // Use the default factory provided by the ViewModelStoreOwner
    // and assume it is an @AndroidEntryPoint annotated fragment or activity
    null
}

/**
 * Creates a [ViewModelProvider.Factory] to get
 * [HiltViewModel](https://dagger.dev/api/latest/dagger/hilt/android/lifecycle/HiltViewModel)
 * -annotated `ViewModel` from a [NavComponentEntry].
 *
 * @param context the activity context
 * @param navComponentEntry the navigation back stack entry
 * @return the factory
 * @throws IllegalStateException if the context given is not an activity
 */
private fun createHiltViewModelFactory(
    context: Context,
    navComponentEntry: NavComponentEntry<*>,
    defaultArguments: Bundle?
): ViewModelProvider.Factory {
    val activity = context.let {
        var ctx = it
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return@let ctx
            }
            ctx = ctx.baseContext
        }
        throw IllegalStateException(
            "Expected an activity context for creating a HiltViewModelFactory for a " +
                    "NavComponentEntry but instead found: $ctx"
        )
    }
    return HiltViewModelFactory.createInternal(
        activity,
        navComponentEntry,
        defaultArguments,
        navComponentEntry.defaultViewModelProviderFactory,
    )
}