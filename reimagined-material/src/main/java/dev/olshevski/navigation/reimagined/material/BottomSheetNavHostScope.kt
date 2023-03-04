package dev.olshevski.navigation.reimagined.material

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModelStoreOwner
import dev.olshevski.navigation.reimagined.NavHostEntry
import dev.olshevski.navigation.reimagined.NavHostScope
import dev.olshevski.navigation.reimagined.ScopedNavHostEntry
import dev.olshevski.navigation.reimagined.ScopingNavHostScope
import dev.olshevski.navigation.reimagined.currentHostEntry

@ExperimentalMaterialApi
@Stable
interface BottomSheetNavHostScope<out T> : NavHostScope<T>, ColumnScope {

    /**
     * [BottomSheetState] of the current BottomSheet.
     */
    val sheetState: BottomSheetState

}

@ExperimentalMaterialApi
@Stable
interface ScopingBottomSheetNavHostScope<out T, S> : BottomSheetNavHostScope<T>,
    ScopingNavHostScope<T, S>

@ExperimentalMaterialApi
@Stable
internal class ScopingBottomSheetNavHostScopeImpl<out T, S>(
    override val hostEntries: List<NavHostEntry<T>>,
    private val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    override val sheetState: BottomSheetState,
    columnScope: ColumnScope
) : ScopingBottomSheetNavHostScope<T, S>, ColumnScope by columnScope {

    override fun getScopedViewModelStoreOwner(scope: S): ViewModelStoreOwner =
        scopedHostEntries[scope]
            ?: error("You should associate the scope ($scope) with the destination (${currentHostEntry.destination}) in a scopeSpec")

}