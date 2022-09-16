@file:Suppress("unused", "TestFunctionName")

package dev.olshevski.navigation.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private class ImmediateLaunchedEffectImpl(
    private val task: suspend CoroutineScope.() -> Unit
) : RememberObserver {
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var job: Job? = null

    override fun onRemembered() {
        job?.cancel("Old job was still running!")
        job = scope.launch(block = task)
    }

    override fun onForgotten() {
        job?.cancel()
        job = null
    }

    override fun onAbandoned() {
        job?.cancel()
        job = null
    }
}

/**
 * Similar to [LaunchedEffect], but uses [Dispatchers.Main.immediate] as a coroutine context.
 * This cause the underlying suspend block to be executed in order with DisposableEffects.
 *
 * In comparison, all LaunchedEffects are executed only after all DisposableEffects and all
 * SideEffects not depending on their order in composition.
 */
@Composable
@NonRestartableComposable
fun ImmediateLaunchedEffect(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    remember(key1) { ImmediateLaunchedEffectImpl(block) }
}

/**
 * Similar to [LaunchedEffect], but uses [Dispatchers.Main.immediate] as a coroutine context.
 * This cause the underlying suspend block to be executed in order with DisposableEffects.
 *
 * In comparison, all LaunchedEffects are executed only after all DisposableEffects and all
 * SideEffects not depending on their order in composition.
 */
@Composable
@NonRestartableComposable
fun ImmediateLaunchedEffect(
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    remember(key1, key2) { ImmediateLaunchedEffectImpl(block) }
}

/**
 * Similar to [LaunchedEffect], but uses [Dispatchers.Main.immediate] as a coroutine context.
 * This cause the underlying suspend block to be executed in order with DisposableEffects.
 *
 * In comparison, all LaunchedEffects are executed only after all DisposableEffects and all
 * SideEffects not depending on their order in composition.
 */
@Composable
@NonRestartableComposable
fun ImmediateLaunchedEffect(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    remember(key1, key2, key3) { ImmediateLaunchedEffectImpl(block) }
}

/**
 * Similar to [LaunchedEffect], but uses [Dispatchers.Main.immediate] as a coroutine context.
 * This cause the underlying suspend block to be executed in order with DisposableEffects.
 *
 * In comparison, all LaunchedEffects are executed only after all DisposableEffects and all
 * SideEffects not depending on their order in composition.
 */
@Composable
@NonRestartableComposable
fun ImmediateLaunchedEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    remember(*keys) { ImmediateLaunchedEffectImpl(block) }
}