package dev.olshevski.navigation.reimagined.material

import androidx.compose.runtime.Stable
import dev.olshevski.navigation.reimagined.NavHostEntry
import dev.olshevski.navigation.reimagined.NavHostScope
import dev.olshevski.navigation.reimagined.ScopedNavHostEntry
import dev.olshevski.navigation.reimagined.ScopingNavHostScope

@Stable
interface BottomSheetNavHostScope<out T> : NavHostScope<T> {

    /**
     * [BottomSheetState] of the current BottomSheet.
     */
    val sheetState: BottomSheetState

}

@Stable
interface ScopingBottomSheetNavHostScope<out T, S> : BottomSheetNavHostScope<T>,
    ScopingNavHostScope<T, S>

@Stable
internal class ScopingBottomSheetNavHostScopeImpl<out T, S>(
    override val hostEntries: List<NavHostEntry<T>>,
    override val scopedHostEntries: Map<S, ScopedNavHostEntry<S>>,
    override val sheetState: BottomSheetState,
) : ScopingBottomSheetNavHostScope<T, S>