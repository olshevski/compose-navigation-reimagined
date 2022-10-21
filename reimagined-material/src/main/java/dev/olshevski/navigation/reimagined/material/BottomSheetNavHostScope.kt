package dev.olshevski.navigation.reimagined.material

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.ViewModelStoreOwner
import dev.olshevski.navigation.reimagined.NavHostEntry
import dev.olshevski.navigation.reimagined.NavHostScope
import dev.olshevski.navigation.reimagined.ScopedNavHostEntry
import dev.olshevski.navigation.reimagined.ScopingNavHostScope
import dev.olshevski.navigation.reimagined.currentHostEntry

@ExperimentalMaterialApi
interface BottomSheetNavHostScope<out T> : NavHostScope<T> {

    val sheetState: BottomSheetState

}

@ExperimentalMaterialApi
interface ScopingBottomSheetNavHostScope<out T, S> : BottomSheetNavHostScope<T>,
    ScopingNavHostScope<T, S>

@ExperimentalMaterialApi
internal class ScopingBottomSheetNavHostScopeImpl<out T, S>(
    override val hostEntries: List<NavHostEntry<T>>,
    private val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    override val sheetState: BottomSheetState
) : ScopingBottomSheetNavHostScope<T, S> {

    override fun getScopedViewModelStoreOwner(scope: S): ViewModelStoreOwner =
        scopedHostEntries[scope]
            ?: error("You should associate the scope ($scope) with the destination (${currentHostEntry.destination}) in a scopeSpec")

}