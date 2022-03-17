package dev.olshevski.navigation.reimagined

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class NavControllerTest {

    private enum class TestDestination {
        A, B
    }

    private class OnBackstackChangeCallback<T> : (NavBackstack<T>) -> Unit {

        var isCalled = false
            private set

        var backstack: NavBackstack<T>? = null
            private set

        override fun invoke(backstack: NavBackstack<T>) {
            isCalled = true
            this.backstack = backstack
        }

    }

    @Nested
    inner class setNewBackstack {

        @Test
        fun `set empty list`() {
            val navController = navController(TestDestination.A)
            navController.setNewBackstack(
                entries = emptyList(),
                action = NavAction.Pop
            )
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `set new list`() {
            val navController = navController(TestDestination.A)
            navController.setNewBackstack(
                entries = listOf(navEntry(TestDestination.B)),
                action = NavAction.Navigate
            )
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun `modify existing list`() {
            val navController = navController(TestDestination.A)
            navController.setNewBackstack(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

    }

    @Nested
    inner class onBackstackChange {

        @Test
        fun `empty list`() {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstack(
                entries = emptyList(),
                action = NavAction.Pop
            )
            assertThat(onBackstackChangeCallback.isCalled).isEqualTo(true)
            assertThat(onBackstackChangeCallback.backstack).isNotNull()
            assertThat(onBackstackChangeCallback.backstack!!.entries).hasSize(0)
            assertThat(onBackstackChangeCallback.backstack!!.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `new entry`() {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstack(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            assertThat(onBackstackChangeCallback.isCalled).isEqualTo(true)
            assertThat(onBackstackChangeCallback.backstack).isNotNull()
            assertThat(onBackstackChangeCallback.backstack!!.entries.map { it.destination }).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
            )
            assertThat(onBackstackChangeCallback.backstack!!.action).isEqualTo(NavAction.Navigate)
        }

    }

}