package dev.olshevski.navigation.reimagined.sample.ui

import android.os.Parcelable
import dev.olshevski.navigation.reimagined.sample.ui.demo.DeeplinksDestination
import kotlinx.parcelize.Parcelize

sealed class MainDestination : Parcelable {

    @Parcelize
    object Splash : MainDestination()

    @Parcelize
    object DemoSelection : MainDestination()

    @Parcelize
    object PassValues : MainDestination()

    @Parcelize
    object ReturnResults : MainDestination()

    @Parcelize
    object AnimatedNavHost : MainDestination()

    @Parcelize
    object DialogNavHost : MainDestination()

    @Parcelize
    object BottomSheetNavHost : MainDestination()

    @Parcelize
    object ViewModels : MainDestination()

    @Parcelize
    object ScopedViewModels : MainDestination()

    @Parcelize
    object BottomNavigation : MainDestination()

    @Parcelize
    data class Deeplinks(
        val initialBackstack: List<DeeplinksDestination> = listOf(DeeplinksDestination.First)
    ) : MainDestination()

}