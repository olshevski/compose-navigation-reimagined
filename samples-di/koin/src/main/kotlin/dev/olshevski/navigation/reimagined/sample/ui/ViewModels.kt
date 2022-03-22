package dev.olshevski.navigation.reimagined.sample.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olshevski.navigation.reimagined.sample.repo.DemoRepository

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
    val id: Int,
    val demoRepository: DemoRepository
) : LoggingViewModel()

class ThirdViewModel(
    val text: String,
    val savedStateHandle: SavedStateHandle,
    val demoRepository: DemoRepository
) : LoggingViewModel()