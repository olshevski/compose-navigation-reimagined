package dev.olshevski.navigation.reimagined.param

import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.ExperimentalReimaginedApi
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.NavHostScope
import dev.olshevski.navigation.reimagined.NavHostState

enum class NavHostParam {
    NavHost,
    AnimatedNavHost
}

@Suppress("TestFunctionName")
@OptIn(ExperimentalReimaginedApi::class)
@Composable
internal fun <T> ParamNavHost(
    param: NavHostParam,
    state: NavHostState<T>,
    content: @Composable NavHostScope<T>.(destination: T) -> Unit
) {
    when (param) {
        NavHostParam.NavHost -> NavHost(state) { content(it) }
        NavHostParam.AnimatedNavHost -> AnimatedNavHost(state) { content(it) }
    }
}