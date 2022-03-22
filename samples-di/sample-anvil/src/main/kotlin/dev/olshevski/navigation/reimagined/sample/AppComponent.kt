package dev.olshevski.navigation.reimagined.sample

import com.squareup.anvil.annotations.MergeComponent
import dev.olshevski.navigation.reimagined.sample.ui.FirstViewModel
import dev.olshevski.navigation.reimagined.sample.ui.SecondViewModel
import dev.olshevski.navigation.reimagined.sample.ui.ThirdViewModel
import javax.inject.Singleton

@Singleton
@MergeComponent(AppScope::class)
interface AppComponent {

    fun firstViewModel(): FirstViewModel
    fun secondViewModelFactory(): SecondViewModel.Factory
    fun thirdViewModelFactory(): ThirdViewModel.Factory

}