package dev.olshevski.navigation.reimagined

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class NavControllerTest {

    private enum class TestDestination {
        A, B
    }

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
        assertThat(navController.backstack.destinations).containsExactly(
            TestDestination.B
        ).inOrder()
        assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
    }

    @Test
    fun `modify existing list`() {
        val navController = navController(TestDestination.A)
        navController.setNewBackstack(
            entries = navController.backstack.entries + navEntry(TestDestination.B),
            action = NavAction.Navigate
        )
        assertThat(navController.backstack.destinations).containsExactly(
            TestDestination.A,
            TestDestination.B
        ).inOrder()
        assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
    }

}