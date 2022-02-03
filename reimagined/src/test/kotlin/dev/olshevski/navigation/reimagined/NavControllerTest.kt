package dev.olshevski.navigation.reimagined

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@RobolectricTest
class NavControllerTest : FunSpec({

    context("modifyBackstack") {

        test("set empty list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = emptyList(),
                action = NavAction.Pop
            )
            navController.backstack.destinations shouldHaveSize 0
        }

        test("set new list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = listOf(navEntry(TestDestination.B)),
                action = NavAction.Navigate
            )
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
        }

        test("modify existing list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
            )
        }

    }

    context("onBackstackChange") {

        test("empty list") {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstackEntries(
                entries = emptyList(),
                action = NavAction.Pop
            )
            onBackstackChangeCallback.isCalled shouldBe true
            onBackstackChangeCallback.entries shouldNotBe null
            onBackstackChangeCallback.entries!! shouldHaveSize 0
        }

        test("new entry") {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstackEntries(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            onBackstackChangeCallback.isCalled shouldBe true
            onBackstackChangeCallback.entries shouldNotBe null
            onBackstackChangeCallback.entries!!.map { it.destination } shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
            )
        }

    }

})

private class OnBackstackChangeCallback<T> : (List<NavEntry<T>>) -> Unit {

    var isCalled = false
        private set

    var entries: List<NavEntry<T>>? = null
        private set

    override fun invoke(entries: List<NavEntry<T>>) {
        isCalled = true
        this.entries = entries
    }

}

