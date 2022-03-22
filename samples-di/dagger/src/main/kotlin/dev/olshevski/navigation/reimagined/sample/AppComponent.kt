package dev.olshevski.navigation.reimagined.sample

import dagger.Component
import dev.olshevski.navigation.reimagined.sample.repo.RepositoryModule
import dev.olshevski.navigation.reimagined.sample.ui.FirstViewModel
import dev.olshevski.navigation.reimagined.sample.ui.SecondViewModel
import dev.olshevski.navigation.reimagined.sample.ui.ThirdViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface AppComponent {

    fun firstViewModel(): FirstViewModel
    fun secondViewModelFactory(): SecondViewModel.Factory
    fun thirdViewModelFactory(): ThirdViewModel.Factory

}