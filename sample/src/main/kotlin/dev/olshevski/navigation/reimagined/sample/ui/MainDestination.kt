package dev.olshevski.navigation.reimagined.sample.ui

import android.os.Parcelable
import dev.olshevski.navigation.reimagined.sample.ui.demo.DeeplinksDestination
import kotlinx.parcelize.Parcelize

sealed class MainDestination : Parcelable {

    @Parcelize
    data object Splash : MainDestination()

    @Parcelize
    data object DemoSelection : MainDestination()

    @Parcelize
    data object PassValues : MainDestination()

    @Parcelize
    data object ReturnResults : MainDestination()

    @Parcelize
    data object AnimatedNavHost : MainDestination()

    @Parcelize
    data object DialogNavHost : MainDestination()

    @Parcelize
    data object BottomSheetNavHost : MainDestination()

    @Parcelize
    data object ViewModels : MainDestination()

    @Parcelize
    data object ScopedViewModels : MainDestination()

    @Parcelize
    data object BottomNavigation : MainDestination()

    @Parcelize
    data class Deeplinks(
        val initialBackstack: List<DeeplinksDestination> = listOf(DeeplinksDestination.First)
    ) : MainDestination()

    @Parcelize
    data object BetterDialogTransitions : MainDestination()
}