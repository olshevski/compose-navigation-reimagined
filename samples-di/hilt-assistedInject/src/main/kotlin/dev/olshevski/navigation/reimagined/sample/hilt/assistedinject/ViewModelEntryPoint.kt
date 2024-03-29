package dev.olshevski.navigation.reimagined.sample.hilt.assistedinject

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.ui.FirstViewModel
import dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.ui.SecondViewModel
import dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.ui.ThirdViewModel

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ViewModelEntryPoint {

    fun firstViewModel(): FirstViewModel
    fun secondViewModelFactory(): SecondViewModel.Factory
    fun thirdViewModelFactory(): ThirdViewModel.Factory

}

val Context.viewModelEntryPoint: ViewModelEntryPoint
    get() = EntryPointAccessors.fromApplication(this)