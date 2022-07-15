package dev.olshevski.navigation.reimagined.sample.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.sample.repo.DemoRepository
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

@HiltViewModel
class FirstViewModel @Inject constructor(
    demoRepository: DemoRepository
) : LoggingViewModel()

@HiltViewModel
class SecondViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    demoRepository: DemoRepository
) : LoggingViewModel() {

    private val id: Int = savedStateHandle["id"]!!

}

@HiltViewModel
class ThirdViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    demoRepository: DemoRepository
) : LoggingViewModel() {

    private val text: String = savedStateHandle["text"]!!

}