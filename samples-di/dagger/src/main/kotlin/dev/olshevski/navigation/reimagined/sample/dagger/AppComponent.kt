package dev.olshevski.navigation.reimagined.sample.dagger

import dagger.Component
import dev.olshevski.navigation.reimagined.sample.dagger.repo.RepositoryModule
import dev.olshevski.navigation.reimagined.sample.dagger.ui.FirstViewModel
import dev.olshevski.navigation.reimagined.sample.dagger.ui.SecondViewModel
import dev.olshevski.navigation.reimagined.sample.dagger.ui.ThirdViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface AppComponent {

    fun firstViewModel(): FirstViewModel
    fun secondViewModelFactory(): SecondViewModel.Factory
    fun thirdViewModelFactory(): ThirdViewModel.Factory

}