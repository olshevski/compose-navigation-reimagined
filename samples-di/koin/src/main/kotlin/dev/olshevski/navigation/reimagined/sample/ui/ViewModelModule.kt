package dev.olshevski.navigation.reimagined.sample.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {

    viewModel { FirstViewModel(get()) }
    viewModel { params -> SecondViewModel(params.get(), get()) }
    viewModel { params -> ThirdViewModel(params.get(), params.get(), get()) }

}