package dev.olshevski.navigation.reimagined.param

import androidx.compose.runtime.Composable
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner

enum class ViewModelFactoryParam {
    Legacy,
    CreationExtras
}

@Composable
inline fun <reified VM : ViewModel> paramViewModel(
    factoryParam: ViewModelFactoryParam,
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current!!,
    crossinline initializer: (savedStateHandle: SavedStateHandle) -> VM
) {
    when (factoryParam) {
        ViewModelFactoryParam.Legacy -> {
            viewModel(
                viewModelStoreOwner = viewModelStoreOwner,
                factory = object :
                    AbstractSavedStateViewModelFactory(
                        viewModelStoreOwner as SavedStateRegistryOwner,
                        null
                    ) {

                    override fun <T : ViewModel> create(
                        key: String,
                        modelClass: Class<T>,
                        handle: SavedStateHandle
                    ): T {
                        @Suppress("UNCHECKED_CAST")
                        return initializer(handle) as T
                    }

                }
            )
        }
        ViewModelFactoryParam.CreationExtras -> {
            viewModel(viewModelStoreOwner = viewModelStoreOwner) {
                initializer(createSavedStateHandle())
            }
        }
    }
}