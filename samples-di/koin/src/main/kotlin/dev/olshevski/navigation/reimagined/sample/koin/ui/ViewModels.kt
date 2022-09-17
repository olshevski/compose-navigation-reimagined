@file:Suppress("UNUSED_PARAMETER")

package dev.olshevski.navigation.reimagined.sample.koin.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olshevski.navigation.reimagined.sample.koin.repo.DemoRepository

open class LoggingViewModel : ViewModel() {

    private val viewModelClassName = this::class.simpleName!!

    init {
        Log.v(viewModelClassName, "init")
    }

    override fun onCleared() {
        Log.v(viewModelClassName, "cleared")
    }
}

class FirstViewModel(
    demoRepository: DemoRepository
) : LoggingViewModel()

class SecondViewModel(
    id: Int,
    demoRepository: DemoRepository
) : LoggingViewModel()

class ThirdViewModel(
    text: String,
    savedStateHandle: SavedStateHandle,
    demoRepository: DemoRepository
) : LoggingViewModel()