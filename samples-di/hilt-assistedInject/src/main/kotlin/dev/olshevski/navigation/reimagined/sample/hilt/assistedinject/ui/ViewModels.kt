@file:Suppress("UNUSED_PARAMETER")

package dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.repo.DemoRepository
import javax.inject.Inject

open class LoggingViewModel : ViewModel() {

    private val viewModelClassName = this::class.simpleName!!

    init {
        Log.v(viewModelClassName, "init")
    }

    override fun onCleared() {
        Log.v(viewModelClassName, "cleared")
    }
}

class FirstViewModel @Inject constructor(
    demoRepository: DemoRepository
) : LoggingViewModel()

class SecondViewModel @AssistedInject constructor(
    @Assisted id: Int,
    demoRepository: DemoRepository
) : LoggingViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(id: Int): SecondViewModel

    }

}

class ThirdViewModel @AssistedInject constructor(
    @Assisted text: String,
    @Assisted savedStateHandle: SavedStateHandle,
    demoRepository: DemoRepository
) : LoggingViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(text: String, savedStateHandle: SavedStateHandle): ThirdViewModel

    }

}